package crazypants.structures.creator.block.generator;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

import org.apache.commons.io.IOUtils;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.block.AbstractResourceDialog;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.FileControls;
import crazypants.structures.creator.block.tree.Icons;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import crazypants.structures.gen.structure.StructureGenerator;
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

  public DialogGeneratorEditor(TileGeneratorEditor tile) {
    this.tile = tile;
    position = new Point3i(tile.xCoord, tile.yCoord, tile.zCoord);
    setIconImage(Icons.GENERATOR.getImage());
    setModal(false);
    setAlwaysOnTop(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle("Generator Editor");

    initComponents();
    addComponents();
    addListeners();

    if(tile.getName() != null && tile.getName().trim().length() > 0 && tile.getExportDir() != null) {
      try {
        curGenerator = loadFromFile(new File(tile.getExportDir(), tile.getName() + StructureResourceManager.GENERATOR_EXT));
      } catch (Exception e) {
        tile.setName("NewTemplate");
        e.printStackTrace();
      }

    } else {
      tile.setName("NewTemplate");
    }
//    buildTree();
  }

  @Override
  protected void createNewResource() {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void openResource() {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected String getResourceUid() {
    if(curGenerator == null || curGenerator.getUid() == null) {
      return null;
    }    
    return curGenerator.getUid().trim();
  }
  
  @Override
  protected String getResourceExtension() {
    return StructureResourceManager.GENERATOR_EXT;
  }

  @Override
  protected AbstractResourceTile getTile() {
    return tile;
  }
  
  @Override
  protected void onDialogClose() {
    openDialogs.remove(position);
    super.onDialogClose();
  }
  
  @Override
  protected void writeToFile(File file, String newUid) {
    if(ExportManager.writeToFile(file, curGenerator, Minecraft.getMinecraft().thePlayer)) {
      Log.info("DialogTemplateEditor.save: Saved template to " + file.getAbsolutePath());
      if(!newUid.equals(curGenerator.getUid())) {
        ((StructureGenerator) curGenerator).setUid(newUid);
        tile.setName(newUid);
        sendUpdatePacket();
//        buildTree();
//        dirtyMonitor.setDirty(true);
      }
//      dirtyMonitor.setDirty(false);
      StructureGenRegister.instance.registerGenerator(curGenerator);
    }    
  }
  
  private IStructureGenerator loadFromFile(File file) {
    String name = file.getName();
    if(name.endsWith(StructureResourceManager.GENERATOR_EXT)) {
      name = name.substring(0, name.length() - StructureResourceManager.GENERATOR_EXT.length());
    }

    InputStream stream = null;
    try {
      stream = new FileInputStream(file);
      StructureGenRegister.instance.getResourceManager().addResourceDirectory(file.getParentFile());
      IStructureGenerator res = StructureGenRegister.instance.getResourceManager().loadGenerator(name, stream);
      if(res != null) {
        tile.setExportDir(file.getParentFile().getAbsolutePath());
        sendUpdatePacket();
      }
      return res;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(stream);
    }
    return null;
  }

  private void initComponents() {
    fileControls = new FileControls(this);
  }

  private void addComponents() {
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(fileControls.getPanel(), BorderLayout.NORTH);
  }

  private void addListeners() {
    
  }
}

