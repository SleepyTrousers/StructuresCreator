package crazypants.structures.creator.block.template;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.IOUtils;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.AbstractResourceDialog;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.FileControls;
import crazypants.structures.creator.block.template.packet.PacketBuildStructure;
import crazypants.structures.creator.block.template.packet.PacketClearStructure;
import crazypants.structures.creator.block.tree.EditorTreeControl;
import crazypants.structures.creator.block.tree.Icons;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import crazypants.structures.gen.structure.StructureTemplate;
import crazypants.structures.gen.structure.preperation.ClearPreperation;
import crazypants.structures.gen.structure.preperation.CompositePreperation;
import crazypants.structures.gen.structure.preperation.FillPreperation;
import crazypants.structures.gen.structure.validator.CompositeSiteValidator;
import crazypants.structures.gen.structure.validator.LevelGroundValidator;
import net.minecraft.client.Minecraft;

public class DialogTemplateEditor extends AbstractResourceDialog {

  private static final long serialVersionUID = 1L;

  private static Map<Point3i, DialogTemplateEditor> openDialogs = new HashMap<Point3i, DialogTemplateEditor>();

  public static void openDialog(TileTemplateEditor tile) {
    Point3i key = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    DialogTemplateEditor res = openDialogs.get(key);
    if(res == null) {
      res = new DialogTemplateEditor(tile);
      openDialogs.put(key, res);
    }
    res.openDialog();
  }

  private final TileTemplateEditor tile;
  private final Point3i position;
  private IStructureTemplate curTemplate;

  private FileControls fileControls;
  private EditorTreeControl treeControl;

  private JButton genB;
  private JButton clearB;
  private JComboBox<Rotation> rotCB;

  public DialogTemplateEditor(TileTemplateEditor tile) {
    this.tile = tile;
    position = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    setIconImage(Icons.TEMPLATE.getImage());
    setTitle("Template Editor");

    initComponents();
    addComponents();
    addListeners();

    if(tile.getName() != null && tile.getName().trim().length() > 0 && tile.getExportDir() != null) {
      try {
        curTemplate = loadFromFile(new File(tile.getExportDir(), tile.getName() + StructureResourceManager.TEMPLATE_EXT));
      } catch (Exception e) {
        treeControl.setDirty(false);
        createNewResource();
      }

    } else {
      treeControl.setDirty(false);
      createNewResource();
    }
    buildTree();
  }

  @Override
  protected void saveAs() {
    if(isTemplateValid()) {
      super.saveAs();
    }
  }

  @Override
  protected boolean checkClear() {
    return !treeControl.isDirty() || super.checkClear();
  }

  @Override
  protected void createNewResource() {
    if(!treeControl.isDirty() || checkClear()) {
      tile.setName("NewTemplate");
      tile.setExportDir(ExportManager.instance.getDefaultDirectory().getAbsolutePath());
      sendUpdatePacket();
      curTemplate = new StructureTemplate(tile.getName());

      CompositePreperation cp = new CompositePreperation();
      cp.add(new ClearPreperation());
      cp.add(new FillPreperation());
      curTemplate.setSitePreperation(cp);

      CompositeSiteValidator val = new CompositeSiteValidator();
      val.add(new LevelGroundValidator());
      curTemplate.setSiteValidator(val);

      buildTree();
    }
  }

  private void buildTree() {
    String name = tile.getName();
    if(curTemplate == null) {
      curTemplate = new StructureTemplate(name);
    }
    treeControl.buildTree(curTemplate);
  }

  private void initComponents() {

    treeControl = new EditorTreeControl(this);

    fileControls = new FileControls(this);

    genB = new JButton(Icons.GENERATE);
    genB.setToolTipText("Generate Structure");
    clearB = new JButton(Icons.CLEAR);
    clearB.setToolTipText("Clear current structure");
    rotCB = new JComboBox<Rotation>(Rotation.values());
    rotCB.setSelectedIndex(0);

  }

  private void addComponents() {

    JPanel generatePan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
    generatePan.setBorder(new TitledBorder("Generate"));
    generatePan.add(clearB);
    generatePan.add(new JLabel("Rot:"));
    generatePan.add(rotCB);
    generatePan.add(genB);

    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(fileControls.getPanel(), BorderLayout.NORTH);
    cp.add(treeControl.getRoot(), BorderLayout.CENTER);
    cp.add(generatePan, BorderLayout.SOUTH);
  }

  private void addListeners() {

    clearB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        clearBounds();
      }
    });

    genB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        clearBounds();
        if(rotCB.getSelectedIndex() >= 0) {
          generate(rotCB.getItemAt(rotCB.getSelectedIndex()));
        }
      }

    });

  }

  @Override
  protected void openResource() {

    if(treeControl.isDirty() && !checkClear()) {
      return;
    }

    StructureResourceManager resMan = StructureGenRegister.instance.getResourceManager();
    List<File> files = resMan.getFilesWithExt(getResourceExtension());

    JPopupMenu menu = new JPopupMenu();
    for (final File file : files) {      
      final IStructureTemplate template = loadFromFile(file);
      if(template != null) {
        JMenuItem mi = new JMenuItem(file.getName());
        mi.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {            
            openTemplateFromFile(file, template);
          }
        });
        menu.add(mi);
      } else {
        Log.warn("DialogTemplateEditor: Could not open " + file);
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
    IStructureTemplate sc = openTemplateFromFile(file);
    if(sc == null) {
      JOptionPane.showMessageDialog(this, "Could not load template.", "Bottoms", JOptionPane.ERROR_MESSAGE);
    }        
  }

  private IStructureTemplate openTemplateFromFile(File file, IStructureTemplate sc) {
    if(sc == null) {
      return null;
    }
    StructureGenRegister.instance.getResourceManager().addResourceDirectory(file.getParentFile());
    StructureGenRegister.instance.registerTemplate(sc);
    
    tile.setExportDir(file.getParent());
    sendUpdatePacket();
    
    openTemplate(sc);
    return sc;
  }
  
  private IStructureTemplate openTemplateFromFile(File file) {
    return openTemplateFromFile(file, loadFromFile(file));    
  }

  private IStructureTemplate loadFromFile(File file) {    
    InputStream stream = null;
    try {
      stream = new FileInputStream(file);
      StructureGenRegister.instance.getResourceManager().addResourceDirectory(file.getParentFile());      
      return StructureGenRegister.instance.getResourceManager().loadTemplate(getUidFromFileName(file), stream);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(stream);
    }
    return null;
  }

  

  @Override
  protected void save() {
    if(curTemplate == null) {
      treeControl.setDirty(false);
      return;
    }
    super.save();
  }

  @Override
  protected void writeToFile(File file, String newUid) {
    if(ExportManager.writeToFile(file, (StructureTemplate) curTemplate, Minecraft.getMinecraft().thePlayer)) {
      Log.info("DialogTemplateEditor.save: Saved template to " + file.getAbsolutePath());
      if(!newUid.equals(curTemplate.getUid())) {
        ((StructureTemplate) curTemplate).setUid(newUid);
        tile.setName(newUid);
        sendUpdatePacket();
        buildTree();
        treeControl.setDirty(true);
      }
      treeControl.setDirty(false);
      StructureGenRegister.instance.registerTemplate(curTemplate);
    }

  }

  @Override
  public String getResourceUid() {
    if(curTemplate == null || curTemplate.getUid() == null) {
      return null;
    }
    return curTemplate.getUid().trim();
  }

  @Override
  public String getResourceExtension() {
    return StructureResourceManager.TEMPLATE_EXT;
  }

  @Override
  public AbstractResourceTile getTile() {
    return tile;
  }

  protected boolean isTemplateValid() {
    if(curTemplate == null) {
      return false;
    }
    if(!curTemplate.isValid()) {
      JOptionPane.showMessageDialog(this, "Current template is not valid", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return false;
    }

    String uid = curTemplate.getUid();
    if(uid == null || uid.trim().length() == 0) {
      JOptionPane.showMessageDialog(this, "No name specified", "Boo hoo", JOptionPane.ERROR_MESSAGE, null);
      return false;
    }
    return true;
  }

  private void openTemplate(IStructureTemplate template) {
    if(template == null) {
      return;
    }
    tile.setName(template.getUid());
    sendUpdatePacket();
    
    clearBounds();    
    curTemplate = template;
    onDirtyChanged(false);
    buildTree();
  }

  private void generate(Rotation rot) {
    sendUpdatePacket();
    PacketBuildStructure packet = new PacketBuildStructure(tile, rot);
    PacketHandler.INSTANCE.sendToServer(packet);
  }

  private void clearBounds() {
    if(tile != null) {
      PacketClearStructure packet = new PacketClearStructure(tile);
      PacketHandler.INSTANCE.sendToServer(packet);
    }
  }

  @Override
  protected void onDialogClose() {
    openDialogs.remove(position);
    super.onDialogClose();
  }

  @Override
  public void onDirtyChanged(boolean dirty) {
    super.onDirtyChanged(dirty);
    fileControls.getSaveB().setEnabled(dirty);
  }

}
