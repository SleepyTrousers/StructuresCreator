package crazypants.structures.creator.block.template.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.sun.glass.events.KeyEvent;

import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.block.template.TileTemplateEditor;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import net.minecraft.client.Minecraft;

public class DialogTemplateEditor extends JDialog {

  private static final long serialVersionUID = 1L;

  private static Map<Point3i, DialogTemplateEditor> openDialogs = new HashMap<Point3i, DialogTemplateEditor>();

  public static void openDialog(TileTemplateEditor tile) {
    Point3i key = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    DialogTemplateEditor res = openDialogs.get(key);
    if(res == null) {
      res = new DialogTemplateEditor(tile);
      openDialogs.put(key, res);
    }
    res.open();

  }

  private final TileTemplateEditor tile;
  private final Point3i position;

  private JTextField nameTF; 

  private JButton openB;
  private JButton importB;

  private JButton exportB;

  private JButton clearB;

  public DialogTemplateEditor(TileTemplateEditor tile) {
    this.tile = tile;
    position = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    setModal(false);
    setAlwaysOnTop(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    initComponents();
    addComponents();
    addListeners();

    updateFieldsFromTE();
  }

  public void open() {
    pack();
    setLocation(Display.getX(), Display.getY());
    setVisible(true);
    requestFocus();
  }

  private void updateFieldsFromTE() {
    ignoreGuiUpdates = true;
    nameTF.setText(tile.getName());    
    ignoreGuiUpdates = false;
  }

  boolean ignoreGuiUpdates = false;

  private void updateTileFromGui() {
    if(ignoreGuiUpdates) {
      return;
    }
    tile.setName(nameTF.getText());   
    sendUpdatePacket();
  }

  private void sendUpdatePacket() {
//    PacketComponentToolGui packet = new PacketComponentToolGui(tile);
//    PacketHandler.INSTANCE.sendToServer(packet);
  }

  private void initComponents() {

    DocumentListener updateListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent e) {
        updateTileFromGui();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateTileFromGui();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateTileFromGui();
      }

    };

    nameTF = createTF(25, updateListener);
    
    clearB = new JButton("New");
    openB = new JButton("Open");
    importB = new JButton("Import");
    exportB = new JButton("Export");
  }

  private JTextField createTF(int cols, DocumentListener updateListener) {
    JTextField res = new JTextField(cols);
    res.getDocument().addDocumentListener(updateListener);
    return res;
  }

  private void addComponents() {
    JPanel rootPan = new JPanel();
    rootPan.setLayout(new GridBagLayout());

    Insets insets = new Insets(4, 0, 4, 0);
    int x = 0;
    int y = 0;
    rootPan.add(new JLabel("Name: "), new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
    x++;
    rootPan.add(nameTF, new GridBagConstraints(x, y, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    
    x = 0;
    y++;

    JPanel bPan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
    bPan.add(clearB);
    bPan.add(openB);
    bPan.add(importB);
    bPan.add(exportB);
    rootPan.add(bPan, new GridBagConstraints(x, y, 2, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

    getContentPane().setLayout(new GridBagLayout());
    getContentPane().add(rootPan,
        new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));

  }

  private void openRegisteredComponent() {
    Map<String, IStructureTemplate> comps = StructureGenRegister.instance.getStructureTemplateMap();
    JPopupMenu menu = new JPopupMenu();
    for (Entry<String, IStructureTemplate> comp : comps.entrySet()) {
      final Entry<String, IStructureTemplate> c = comp;
      JMenuItem mi = new JMenuItem(comp.getKey());
      mi.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          openTemplate(c.getKey(), c.getValue());
        }
      });
      menu.add(mi);
    }
    menu.show(openB, 0, 0);
  }

  private void importFromFile() {
    File startDir = new File(tile.getExportDir() == null ? ExportManager.EXPORT_DIR.getName() : tile.getExportDir());
    JFileChooser fc = new JFileChooser(startDir);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setDialogTitle("Select Component File");
    int res = fc.showOpenDialog(this);    
    if(res != JFileChooser.APPROVE_OPTION) {
      return;
    }
        
    IStructureTemplate sc = loadFromFile(fc.getSelectedFile());
    if(sc != null) {
      StructureGenRegister.instance.registerTemplate(sc);
    } else {
      JOptionPane.showMessageDialog(this, "Could not load component.", "Bottoms", JDialog.ERROR );
    }
    String name = sc.getUid();
    
    openTemplate(name, sc);
  }
  
  public IStructureTemplate loadFromFile(File file) {
    String name = file.getName();
    if(name.endsWith(StructureResourceManager.TEMPLATE_EXT)) {
      name = name.substring(0, name.length() - StructureResourceManager.TEMPLATE_EXT.length());
    }
    
    InputStream stream = null;
    try {      
      stream = new FileInputStream(file);
      return StructureGenRegister.instance.getResourceManager().loadTemplate(name, stream);      
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(stream);
    }
    return null;
  }

  private void exportToFile() {

    String name = nameTF.getText();
    if(name == null || name.trim().length() == 0) {
      JOptionPane.showMessageDialog(this, "No name specified", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return;
    }

    File startDir = new File(tile.getExportDir() == null ? ExportManager.EXPORT_DIR.getName() : tile.getExportDir());
    JFileChooser fc = new JFileChooser(startDir);
    fc.setSelectedFile(new File(name + StructureResourceManager.COMPONENT_EXT));
    fc.setDialogTitle("Select Directory");
    int res = fc.showSaveDialog(this);

    if(res != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File dir;
    File file = fc.getSelectedFile();
    if(file.isDirectory()) {
      dir = file;
      file = new File(dir, name + StructureResourceManager.COMPONENT_EXT);
    } else {
      dir = file.getParentFile();
      if(!file.exists() && !file.getName().endsWith(StructureResourceManager.COMPONENT_EXT)) {
        file = new File(dir, file.getName() + StructureResourceManager.COMPONENT_EXT);
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

    if(file.exists()) {
      res = JOptionPane.showConfirmDialog(this, "Replace existing file?");
      if(res != JFileChooser.APPROVE_OPTION) {
        return;
      }
    }

//    StructureComponentNBT comp = CreatorUtil.createComponent(name, tile.getWorldObj(), tile.getStructureBounds(), tile.getSurfaceOffset());
//    comp.setTags(tile.getTaggedLocations());
//    if(comp != null) {
//      StructureGenRegister.instance.registerStructureComponent(comp);
//      ExportManager.writeToFile(file, comp, Minecraft.getMinecraft().thePlayer);
//    }
  }

  

  private void openTemplate(String name, IStructureTemplate iStructureTemplate) {
    if(name == null || iStructureTemplate == null) {
      return;
    }
    
    clearBounds();
//    tile.setComponent(name, iStructureTemplate);
    updateFieldsFromTE();

//    PacketBuildComponent packet = new PacketBuildComponent(tile, name);
//    PacketHandler.INSTANCE.sendToServer(packet);
  }

  private void clearBounds() {
    //tile.getTaggedLocations().clear();
    tile.markDirty();
//    PacketBuildComponent packet = new PacketBuildComponent(tile, null);
//    PacketHandler.INSTANCE.sendToServer(packet);
  }

  private void onClose() {
    openDialogs.remove(position);
    Mouse.setCursorPosition(Display.getX() - Display.getWidth() / 2, Display.getY() - Display.getHeight() / 2);
        if(Minecraft.getMinecraft().currentScreen instanceof GuiTemplateEditor) {
          Minecraft.getMinecraft().thePlayer.closeScreen();
        }
  }

  private boolean checkClear() {
    return JFileChooser.APPROVE_OPTION == JOptionPane.showConfirmDialog(DialogTemplateEditor.this, "Clear existing data?");
  }

  private void addListeners() {

    openB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if(checkClear()) {
          openRegisteredComponent();
        }

      }
    });

    importB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if(checkClear()) {
          importFromFile();
        }
      }
    });

    exportB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        exportToFile();
      }

    });

    clearB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if(checkClear()) {
          nameTF.setText("PaulTheNew");
          clearBounds();
        }
      }

    });

    addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosed(WindowEvent e) {
        onClose();
      }
    });

    ActionListener al = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        onClose();
        setVisible(false);
      }
    };
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    JRootPane rootPane = getRootPane();
    rootPane.registerKeyboardAction(al, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

  }

}
