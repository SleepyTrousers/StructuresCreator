package crazypants.structures.creator.block.component.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.IOUtils;

import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.CreatorUtil;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.AbstractResourceDialog;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.FileControls;
import crazypants.structures.creator.block.component.TileComponentTool;
import crazypants.structures.creator.block.component.packet.PacketBuildComponent;
import crazypants.structures.creator.block.component.packet.PacketComponentToolGui;
import crazypants.structures.creator.block.tree.Icons;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import crazypants.structures.gen.structure.StructureComponentNBT;
import net.minecraft.client.Minecraft;

public class DialogComponentTool extends AbstractResourceDialog {

  private static final long serialVersionUID = 1L;

  private static Map<Point3i, DialogComponentTool> openDialogs = new HashMap<Point3i, DialogComponentTool>();

  public static void openDialog(TileComponentTool tile) {
    Point3i key = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    DialogComponentTool res = openDialogs.get(key);
    if(res == null) {
      res = new DialogComponentTool(tile);
      openDialogs.put(key, res);
    }
    res.openDialog();

  }

  private final TileComponentTool tile;
  private final Point3i position;

  private JTextField nameTF;
  private JTextField widthTF;
  private JTextField heightTF;
  private JTextField lengthTF;
  private JTextField grounLevelTF;

  private FileControls fileControls;

  public DialogComponentTool(TileComponentTool tile) {
    this.tile = tile;
    position = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);

    initComponents();
    addComponents();
    addListeners();

    updateFieldsFromTE();

    setTitle("Component Editor");
    setIconImage(Icons.COMPONENT.getImage());
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

  @Override
  protected void sendUpdatePacket() {
    PacketComponentToolGui packet = new PacketComponentToolGui(tile);
    PacketHandler.INSTANCE.sendToServer(packet);
  }

  @Override
  protected void createNewResource() {
    if(checkClear()) {
      nameTF.setText("PaulTheNew");
      clearBounds();
    }
  }

  @Override
  protected void openResource() {

    if(!checkClear()) {
      return;
    }

    StructureResourceManager resMan = StructureGenRegister.instance.getResourceManager();
    List<File> files = resMan.getFilesWithExt(getResourceExtension());

    JPopupMenu menu = new JPopupMenu();
    for (File file : files) {

      final String uid = file.getName().substring(0, file.getName().length() - getResourceExtension().length());
      final StructureComponentNBT component = readFromFile(file);
      if(component != null) {
        JMenuItem mi = new JMenuItem(file.getName());
        mi.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            openComponent(uid, component);
          }
        });
        menu.add(mi);
      } else {
        System.out.println("DialogComponentTool.openRegisteredTemplate: Could not load component from file: " + file.getAbsolutePath());
      }

    }

    JMenuItem mi = new JMenuItem("...");
    mi.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        openFromFile();
      }
    });
    menu.add(mi);

    menu.show(fileControls.getOpenB(), 0, 0);
  }

  private void openFromFile() {
    File file = selectFileToOpen();
    if(file == null) {
      return;
    }

    StructureComponentNBT sc = readFromFile(file);
    if(sc != null) {
      StructureGenRegister.instance.registerStructureComponent(sc);
      openComponent(sc.getUid(), sc);
    } else {
      JOptionPane.showMessageDialog(this, "Could not load component.", "Bottoms", JOptionPane.ERROR_MESSAGE);
    }
  }

  public StructureComponentNBT readFromFile(File file) {
    String name = file.getName();
    if(name.endsWith(StructureResourceManager.COMPONENT_EXT)) {
      name = name.substring(0, name.length() - StructureResourceManager.COMPONENT_EXT.length());
    }

    InputStream stream = null;
    try {
      stream = new FileInputStream(file);
      return new StructureComponentNBT(name, stream);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(stream);
    }
    return null;
  }

  @Override
  protected void writeToFile(File file, String uid) {
    String name = nameTF.getText().trim();
    if(!uid.equals(name)) {
      nameTF.setText(uid);

    }
    StructureComponentNBT comp = CreatorUtil.createComponent(uid, tile.getWorldObj(), tile.getStructureBounds(), tile.getSurfaceOffset());
    comp.setTags(tile.getTaggedLocations());
    if(ExportManager.writeToFile(file, comp, Minecraft.getMinecraft().thePlayer)) {
      StructureGenRegister.instance.registerStructureComponent(comp);
    }
    sendUpdatePacket();
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
    tile.getTaggedLocations().clear();
    tile.markDirty();
    PacketBuildComponent packet = new PacketBuildComponent(tile, null);
    PacketHandler.INSTANCE.sendToServer(packet);
  }

  @Override
  protected void onDialogClose() {
    openDialogs.remove(position);
  }

  @Override
  protected String getResourceUid() {
    String res = nameTF.getText();
    if(res == null) {
      return null;
    }
    return res.trim();
  }

  @Override
  protected String getResourceExtension() {
    return StructureResourceManager.COMPONENT_EXT;
  }

  @Override
  protected AbstractResourceTile getTile() {
    return tile;
  }

  private JTextField createTF(int cols, DocumentListener updateListener) {
    JTextField res = new JTextField(cols);
    res.getDocument().addDocumentListener(updateListener);
    return res;
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

    fileControls = new FileControls(this);
  }

  private void addComponents() {
    JPanel rootPan = new JPanel();
    rootPan.setLayout(new GridBagLayout());

    Insets insets = new Insets(4, 0, 4, 0);
    int x = 0;
    int y = 0;

    rootPan.add(fileControls.getPanel(), new GridBagConstraints(x, y, 2, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    x = 0;
    y++;

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

    getContentPane().setLayout(new GridBagLayout());
    getContentPane().add(rootPan,
        new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));

  }

  private void addListeners() {

  }

}
