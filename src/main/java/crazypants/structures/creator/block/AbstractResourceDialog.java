package crazypants.structures.creator.block;

import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.template.packet.PacketResourceTileGui;
import crazypants.structures.creator.item.ExportManager;
import net.minecraft.client.Minecraft;

public abstract class AbstractResourceDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  static {          
    setUIFont (new FontUIResource("Dialog.plain",Font.PLAIN, 14));    
  }
  
  private static void setUIFont(javax.swing.plaf.FontUIResource f) {
    Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if(value != null && value instanceof javax.swing.plaf.FontUIResource)
        UIManager.put(key, f);
    }
  } 

  private FileNameExtensionFilter filter;
  
  protected AbstractResourceDialog() {
    setModal(false);
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

    addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) {
        onDialogClose();
      }

    });

    ActionListener al = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        onDialogClose();        
      }
    };
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    JRootPane rootPane = getRootPane();
    rootPane.registerKeyboardAction(al, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
      @Override
      public boolean dispatchKeyEvent(java.awt.event.KeyEvent e) {
        boolean keyHandled = false;
        if(e.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {
          if(e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
            keyHandled = true;
            save();
          }
        }
        return keyHandled;
      }
    });
    
  }
  
  protected FileFilter getFileFilter() {
    if(filter == null) {
      String ext = getResourceExtension();
      ext = ext.substring(1, ext.length());
      filter = new FileNameExtensionFilter(ext, ext);
    }
    return filter;
  }

  protected void onDialogClose() {    
    if(!checkClear()) {
      return;
    }
    Mouse.setCursorPosition(Display.getX() - Display.getWidth() / 2, Display.getY() - Display.getHeight() / 2);
    if(Minecraft.getMinecraft().thePlayer != null) {
      Minecraft.getMinecraft().thePlayer.closeScreen();
    }
    setVisible(false);
    dispose();
  }
  
  protected void openDialog() {
    pack();
    setLocation(Display.getX(), Display.getY());
    setVisible(true);
    requestFocus();
    
    
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
          toFront();
          requestFocus();
          repaint();
      }
  });
    
  }

  protected boolean checkClear() {
    return JFileChooser.APPROVE_OPTION == JOptionPane.showConfirmDialog(this, "Discard any unsaved changes?");
  }
  
  public abstract String getResourceUid();
  public abstract String getResourceExtension();
  public abstract AbstractResourceTile getTile();  

  protected abstract void createNewResource();
  
  protected abstract void openResource();
  
  public void onDirtyChanged(boolean dirty) {
    String title = getResourceUid();
    title = title + getResourceExtension();
    if(dirty) {
      title += "*";
    }
    setTitle(title);
  }
  
  protected String getUidFromFileName(File file) {
    String uid = file.getName();
    if(uid.endsWith(getResourceExtension())) {
      uid = uid.substring(0, uid.length() - getResourceExtension().length());
    }
    return uid;
  }
  
  protected void save() {
    String uid = getResourceUid();
    AbstractResourceTile tile = getTile();
    if(tile.getExportDir() == null) {
      tile.setExportDir(ExportManager.instance.getDefaultDirectory().getAbsolutePath());
      sendUpdatePacket();
    }
    if(!StringUtils.equals(uid, tile.getName())) {
      tile.setName(uid);
      sendUpdatePacket();
    }
    if(uid == null || uid.trim().length() == 0) {
      JOptionPane.showMessageDialog(this, "No name specified", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return;
    }
    File f = new File(tile.getExportDir(), uid + getResourceExtension());
    writeToFile(f, uid);  
  }

  protected void saveAs() {

    String name = getResourceUid();
    String ext = getResourceExtension();
    AbstractResourceTile tile = getTile();    
    if(name == null || name.trim().length() == 0) {
      JOptionPane.showMessageDialog(this, "No name specified", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return;
    }

    File startDir = new File(tile.getExportDir() == null ? ExportManager.instance.getDefaultDirectory().getAbsolutePath() : tile.getExportDir());
    JFileChooser fc = new JFileChooser(startDir);
    fc.setSelectedFile(new File(name + ext));
    fc.setDialogTitle("Save As");                
    fc.setFileFilter(getFileFilter());
    
    int res = fc.showSaveDialog(this);

    if(res != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File dir;
    File file = fc.getSelectedFile();
    if(file.isDirectory()) {
      dir = file;
      file = new File(dir, name + ext);
    } else {
      dir = file.getParentFile();
      if(!file.exists() && !file.getName().endsWith(ext)) {
        file = new File(dir, file.getName() + ext);
      }
    }
    if(!dir.exists()) {
      dir.mkdirs();
    }
    if(!dir.exists()) {
      return;
    }

    tile.setExportDir(dir.getPath());
    sendUpdatePacket();

    String fileName = file.getName();
    if(!fileName.endsWith(ext)) {
      fileName = fileName + ext;
      file = new File(file.getParentFile(), fileName);
    }

    if(file.exists()) {
      res = JOptionPane.showConfirmDialog(this, "Replace existing file?");
      if(res != JFileChooser.APPROVE_OPTION) {
        return;
      }
    }

    String newName = file.getName().substring(0, file.getName().length() - getResourceExtension().length());
    writeToFile(file, newName);
  }
  
  protected void sendUpdatePacket() {    
    if(getTile() != null) {
      PacketResourceTileGui packet = new PacketResourceTileGui(getTile());
      PacketHandler.INSTANCE.sendToServer(packet);
    }
  }
  
  protected File selectFileToOpen() {
    AbstractResourceTile tile = getTile();   
    File startDir = new File(tile.getExportDir() == null ? ExportManager.instance.getDefaultDirectory().getAbsolutePath() : tile.getExportDir());
    JFileChooser fc = new JFileChooser(startDir);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setDialogTitle("Open");
    fc.setFileFilter(getFileFilter());    
    int res = fc.showOpenDialog(this);
    if(res != JFileChooser.APPROVE_OPTION) {
      return null;
    }
    File file = fc.getSelectedFile();
    return file;
  }

  protected abstract void writeToFile(File file, String newName);

}
