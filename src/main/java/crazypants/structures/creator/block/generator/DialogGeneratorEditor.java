package crazypants.structures.creator.block.generator;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.commons.io.IOUtils;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.block.AbstractResourceDialog;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.FileControls;
import crazypants.structures.creator.block.tree.EditorTreeControl;
import crazypants.structures.creator.block.tree.Icons;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import crazypants.structures.gen.structure.StructureGenerator;
import crazypants.structures.gen.structure.sampler.SurfaceLocationSampler;
import crazypants.structures.gen.structure.validator.RandomValidator;
import crazypants.structures.gen.structure.validator.SpacingValidator;
import net.minecraft.client.Minecraft;

public class DialogGeneratorEditor extends AbstractResourceDialog {

  
  private static final long serialVersionUID = 1L;

  private static Map<Point3i, DialogGeneratorEditor> openDialogs = new HashMap<Point3i, DialogGeneratorEditor>();

  public static void openDialog(TileGeneratorEditor tile) {
    Point3i key = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    DialogGeneratorEditor res = openDialogs.get(key);
    if(res == null) {
      res = new DialogGeneratorEditor(tile);
      openDialogs.put(key, res);
    }
    res.openDialog();
  }

  private final TileGeneratorEditor tile;
  private final Point3i position;  
  private IStructureGenerator curGenerator;
  
  private FileControls fileControls;
  private EditorTreeControl treeControl;

  public DialogGeneratorEditor(TileGeneratorEditor tile) {
    this.tile = tile;
    position = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    setIconImage(Icons.GENERATOR.getImage());    
    setTitle("Generator Editor");

    initComponents();
    addComponents();
    addListeners();

    if(tile.getName() != null && tile.getName().trim().length() > 0 && tile.getExportDir() != null) {
      try {
        curGenerator = loadFromFile(new File(tile.getExportDir(), tile.getName() + StructureResourceManager.GENERATOR_EXT));
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

  private void buildTree() {
    String name = tile.getName();
    if(curGenerator == null) {
      StructureGenerator gen = new StructureGenerator();
      gen.setUid(name);
      curGenerator = gen;       
    }
    treeControl.buildTree(curGenerator);        
  }
  
  @Override
  protected boolean checkClear() {
    return !treeControl.isDirty() || super.checkClear();     
  }

  @Override
  protected void createNewResource() {
    if(!treeControl.isDirty() || checkClear()) {
      tile.setName("NewGenerator");
      tile.setExportDir(ExportManager.instance.getDefaultDirectory().getAbsolutePath());
      sendUpdatePacket();
      curGenerator = null;
      StructureGenerator gen = new StructureGenerator();
      gen.setUid(tile.getName());
      
      gen.setLocationSampler(new SurfaceLocationSampler());
      gen.addChunkValidator(new RandomValidator(0.1f));
      gen.addChunkValidator(new SpacingValidator(100));
      gen.addChunkValidator(new SpacingValidator(500, tile.getName()));      
      
      curGenerator = gen;       
            
      buildTree();
    }    
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
      final IStructureGenerator gen = loadFromFile(file);
      if(gen != null) {
        JMenuItem mi = new JMenuItem(file.getName());
        mi.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            openGeneratorFromFile(file, gen);
          }
        });
        menu.add(mi);
      } else {
        System.out.println("DialogGeneratorEditor.openResource: Could not load template from file: " + file.getAbsolutePath());
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

  @Override
  public String getResourceUid() {
    if(curGenerator == null || curGenerator.getUid() == null) {
      return null;
    }    
    return curGenerator.getUid().trim();
  }
  
  @Override
  public String getResourceExtension() {
    return StructureResourceManager.GENERATOR_EXT;
  }

  @Override
  public AbstractResourceTile getTile() {
    return tile;
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
  
  @Override
  protected void writeToFile(File file, String newUid) {
    if(ExportManager.writeToFile(file, (StructureGenerator)curGenerator, Minecraft.getMinecraft().thePlayer)) {
      Log.info("DialogTemplateEditor.save: Saved template to " + file.getAbsolutePath());
      if(!newUid.equals(curGenerator.getUid())) {
        ((StructureGenerator) curGenerator).setUid(newUid);
        tile.setName(newUid);
        sendUpdatePacket();
        buildTree();
        treeControl.setDirty(true);
      }
      treeControl.setDirty(false);
      StructureGenRegister.instance.registerGenerator(curGenerator);
    }    
  }
  
  private void openGenerator(IStructureGenerator gen) {
    if(gen == null) {
      return;
    }   
    tile.setName(gen.getUid());
    sendUpdatePacket();
    curGenerator = gen;
    onDirtyChanged(false);
    buildTree();
  }
  
  private void openFromFile() {    
    File file = selectFileToOpen();
    if(file == null) {
      return;
    }    
    IStructureGenerator res = openGeneratorFromFile(file);
    if(res == null) {
      JOptionPane.showMessageDialog(this, "Could not load generator.", "Bottoms", JOptionPane.ERROR_MESSAGE);
    }        
  }
  
  private IStructureGenerator openGeneratorFromFile(File file) {
    return openGeneratorFromFile(file, loadFromFile(file));
  }
  
  private IStructureGenerator openGeneratorFromFile(File file, IStructureGenerator gen) {    
    if(gen == null) {
      return null;
    }
    StructureGenRegister.instance.getResourceManager().addResourceDirectory(file.getParentFile());
    StructureGenRegister.instance.registerGenerator(gen);
    
    tile.setExportDir(file.getParentFile().getAbsolutePath());
    sendUpdatePacket();
    
    openGenerator(gen);
    return gen;        
  }
  
  private IStructureGenerator loadFromFile(File file) {   
    InputStream stream = null;
    try {
      stream = new FileInputStream(file);
      StructureGenRegister.instance.getResourceManager().addResourceDirectory(file.getParentFile());      
      return StructureGenRegister.instance.getResourceManager().loadGenerator(getUidFromFileName(file), stream);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(stream);
    }
    return null;
  }

  private void initComponents() {
    fileControls = new FileControls(this);
    treeControl = new EditorTreeControl(this);
  }

  private void addComponents() {
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(fileControls.getPanel(), BorderLayout.NORTH);
    cp.add(treeControl.getRoot(), BorderLayout.CENTER);
  }

  private void addListeners() {
    
  }
}

