package crazypants.structures.creator.block.component;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
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

import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.CreatorUtil;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import crazypants.structures.gen.structure.StructureComponentNBT;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class DialogComponentTool extends JDialog {

  private static final long serialVersionUID = 1L;

  private static Map<Point3i, DialogComponentTool> openDialogs = new HashMap<Point3i, DialogComponentTool>();

  public static void openDialog(TileComponentTool tile) {
    Point3i key = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    DialogComponentTool res = openDialogs.get(key);
    if(res == null) {
      res = new DialogComponentTool(tile);
      openDialogs.put(key, res);
    }
    res.open();

  }

  private final TileComponentTool tile;
  private final Point3i position;

  private JTextField nameTF;
  private JTextField widthTF;
  private JTextField heightTF;
  private JTextField lengthTF;
  private JTextField grounLevelTF;

  private JButton openB;
  private JButton importB;

  private JButton exportB;

  private JButton clearB;

  public DialogComponentTool(TileComponentTool tile) {
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
    widthTF.setText(tile.getWidth() + "");
    heightTF.setText(tile.getHeight() + "");
    lengthTF.setText(tile.getLength() + "");
    grounLevelTF.setText(tile.getSurfaceOffset() + "");
    ignoreGuiUpdates = false;
  }

  boolean ignoreGuiUpdates = false;

  private void updateTileFromGui() {
    if(ignoreGuiUpdates) {
      return;
    }
    tile.setName(nameTF.getText());
    tile.setWidth(getInt(widthTF, tile.getWidth()));
    tile.setHeight(getInt(heightTF, tile.getHeight()));
    tile.setLength(getInt(lengthTF, tile.getLength()));
    tile.setSurfaceOffset(getInt(grounLevelTF, tile.getSurfaceOffset()));

    sendUpdatePacket();
  }

  private void sendUpdatePacket() {
    PacketComponentToolGui packet = new PacketComponentToolGui(tile);
    PacketHandler.INSTANCE.sendToServer(packet);
  }

  private int getInt(JTextField tf, int def) {
    String txt = tf.getText();
    try {
      return Integer.parseInt(txt);
    } catch (Exception e) {
    }
    return def;
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
    widthTF = createTF(5, updateListener);
    heightTF = createTF(5, updateListener);
    lengthTF = createTF(5, updateListener);
    grounLevelTF = createTF(3, updateListener);

    clearB = new JButton("Clear");
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
    y++;
    x = 0;
    rootPan.add(new JLabel("Bounds: "), new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
    x++;

    JPanel bPan = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    bPan.add(new JLabel(" W:"));
    bPan.add(widthTF);
    bPan.add(new JLabel("H:"));
    bPan.add(heightTF);
    bPan.add(new JLabel("L:"));
    bPan.add(lengthTF);
    rootPan.add(bPan, new GridBagConstraints(x, y, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

    x = 0;
    y++;

    rootPan.add(new JLabel("Grnd Lvl: "), new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
    x++;
    rootPan.add(grounLevelTF, new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

    x = 0;
    y++;

    bPan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
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
    //EnderStructures.structureRegister.
    Map<String, IStructureComponent> comps = StructureGenRegister.instance.getStructureComponentMap();

    JPopupMenu menu = new JPopupMenu();
    for (Entry<String, IStructureComponent> comp : comps.entrySet()) {
      final Entry<String, IStructureComponent> c = comp;
      JMenuItem mi = new JMenuItem(comp.getKey());
      mi.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          openComponent(c.getKey(), c.getValue());
        }
      });
      menu.add(mi);
    }
    menu.show(openB, 0, 0);
  }

  private void importFromFile() {
    // TODO Auto-generated method stub

  }

  private void exportToFile() {

    String name = nameTF.getText();
    if(name == null || name.trim().length() == 0) {
      JOptionPane.showMessageDialog(this, "No name specified", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return;
    }

    JFileChooser fc = new JFileChooser(tile.getExportDir() == null ? ExportManager.EXPORT_DIR.getName() : tile.getExportDir());
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc.setDialogTitle("Select Directory");
    int res = fc.showSaveDialog(this);

    if(res != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File dir = fc.getSelectedFile();
    if(!dir.exists() || !dir.isDirectory()) {
      return;
    }
    File f = new File(dir, name + StructureResourceManager.COMPONENT_EXT);

    if(f.exists()) {
      res = JOptionPane.showConfirmDialog(this, "Replace existing file?");
      if(res != JFileChooser.APPROVE_OPTION) {
        return;
      }
    }

    StructureComponentNBT comp = CreatorUtil.createComponent(name, tile.getWorldObj(), tile.getStructureBounds(), tile.getSurfaceOffset());
    if(comp != null) {
      StructureGenRegister.instance.registerStructureComponent(comp);
      writeToFile(f, comp, Minecraft.getMinecraft().thePlayer);
    }

  }

  private static boolean writeToFile(File file, StructureComponentNBT st, EntityPlayer player) {
    boolean saved = false;
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file, false);
      st.write(fos);
      fos.flush();
      fos.close();
      saved = true;
      player.addChatComponentMessage(new ChatComponentText("Saved to " + file.getAbsolutePath()));
    } catch (Exception e) {
      e.printStackTrace();
      player.addChatComponentMessage(new ChatComponentText("Could not save to " + file.getAbsolutePath()));
    } finally {
      IOUtils.closeQuietly(fos);
    }
    return saved;
  }

  private void openComponent(String name, IStructureComponent component) {

    if(name == null || component == null) {
      return;
    }

    clearBounds();

    tile.setComponent(name, component);
    updateFieldsFromTE();

    PacketBuildComponent packet = new PacketBuildComponent(tile, name);
    PacketHandler.INSTANCE.sendToServer(packet);
  }

  private void clearBounds() {
    PacketBuildComponent packet = new PacketBuildComponent(tile, null);
    PacketHandler.INSTANCE.sendToServer(packet);
  }

  private void onClose() {
    openDialogs.remove(position);
    Mouse.setCursorPosition(Display.getX() - Display.getWidth() / 2, Display.getY() - Display.getHeight() / 2);
    if(Minecraft.getMinecraft().currentScreen instanceof GuiComponentTool) {
      Minecraft.getMinecraft().thePlayer.closeScreen();
    }
  }

  private boolean checkClear() {
    return JFileChooser.APPROVE_OPTION == JOptionPane.showConfirmDialog(DialogComponentTool.this, "Clear existing data?");
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
