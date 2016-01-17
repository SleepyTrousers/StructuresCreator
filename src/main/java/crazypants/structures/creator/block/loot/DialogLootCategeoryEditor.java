package crazypants.structures.creator.block.loot;

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
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.block.AbstractResourceDialog;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.FileControls;
import crazypants.structures.creator.block.tree.EditorTreeControl;
import crazypants.structures.creator.block.tree.Icons;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.LootCategeoriesParser;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import crazypants.structures.gen.structure.loot.LootCategories;
import net.minecraft.client.Minecraft;

public class DialogLootCategeoryEditor extends AbstractResourceDialog {

  
  private static final long serialVersionUID = 1L;

  private static Map<Point3i, DialogLootCategeoryEditor> openDialogs = new HashMap<Point3i, DialogLootCategeoryEditor>();

  public static void openDialog(TileLootCategory tile) {
    Point3i key = new Point3i(tile.getPos());
    DialogLootCategeoryEditor res = openDialogs.get(key);
    if(res == null) {
      res = new DialogLootCategeoryEditor(tile);
      openDialogs.put(key, res);
    }
    res.openDialog();
  }

  private final TileLootCategory tile;
  private final Point3i position;  
  
  private LootCategories curCategories;
  
  private LootCategories lastSaveCategeories;
  
  private FileControls fileControls;
  private EditorTreeControl treeControl;

  public DialogLootCategeoryEditor(TileLootCategory tile) {
    this.tile = tile;    
    position = new Point3i(tile.getPos());
    setIconImage(Icons.LOOT_EDITOR.getImage());    
    setTitle("Loot Category Editor");

    initComponents();
    addComponents();
    addListeners();

    if(tile.getName() != null && tile.getName().trim().length() > 0 && tile.getExportDir() != null) {
      try {
        curCategories = loadFromFile(new File(tile.getExportDir(), tile.getName() + getResourceExtension()));
      } catch (Exception e) {
        tile.setName("NewGenerator");
        e.printStackTrace();
      }

    } else {
      tile.setName("NewGenerator");
    }
    buildTree();
  }

  private void buildTree() {
    String name = tile.getName();
    if(curCategories == null) {            
      curCategories = new LootCategories();
      curCategories.setUid(name);
    }
    treeControl.buildTree(curCategories);        
  }
  
  @Override
  protected boolean checkClear() {
    return !treeControl.isDirty() || super.checkClear();     
  }

  @Override
  protected void createNewResource() {
    if(!treeControl.isDirty() || checkClear()) {
      tile.setName("NewLootz");
      sendUpdatePacket();      
      curCategories = null;
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
      final LootCategories cat = loadFromFile(file);
      if(cat != null) {
        JMenuItem mi = new JMenuItem(file.getName());
        mi.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            openFromFile(file, cat);            
          }
        });
        menu.add(mi);
      } else {
        System.out.println("DialogLootCategoryEditor.openResource: Could not load from file: " + file.getAbsolutePath());
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
    if(curCategories == null || curCategories.getUid() == null) {
      return null;
    }    
    return curCategories.getUid().trim();
  }
  
  @Override
  public String getResourceExtension() {
    return StructureResourceManager.LOOT_EXT;
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
    if(curCategories == null) {
      return;
    }
    
    if(ExportManager.writeToFile(file, curCategories, Minecraft.getMinecraft().thePlayer)) {
      Log.info("DialogTemplateEditor.save: Saved template to " + file.getAbsolutePath());
      if(!newUid.equals(curCategories.getUid())) {
        curCategories.setUid(newUid);
        tile.setName(newUid);
        sendUpdatePacket();
        buildTree();
        treeControl.setDirty(true);
      }
      treeControl.setDirty(false);  
      
      if(lastSaveCategeories != null) {
        lastSaveCategeories.deregister();
      }
      curCategories.register();  
      lastSaveCategeories = new LootCategories(curCategories);
    }    
  }
  
  private void openCategories(LootCategories cats) {
    if(cats == null) {
      return;
    } 
    
    tile.setName(cats.getUid());
    sendUpdatePacket();
    
    curCategories = cats;
    lastSaveCategeories = new LootCategories(curCategories);
    
    curCategories.deregister(); //make sure we dont add everything again
    curCategories.register();
    
    onDirtyChanged(false);
    buildTree();
  }
  
  
  private void openFromFile() {
    File file = selectFileToOpen();
    if(file == null) {
      return;
    }    
    LootCategories lc = openFromFile(file);
    if(lc == null) {
      JOptionPane.showMessageDialog(this, "Could not load template.", "Bottoms", JOptionPane.ERROR_MESSAGE);
    }        
  }

  private LootCategories openFromFile(File file, LootCategories sc) {
    if(sc == null) {
      return null;
    }    
    tile.setExportDir(file.getParent());
    sendUpdatePacket();
    
    openCategories(sc);
    return sc;
  }
  
  private LootCategories openFromFile(File file) {
    return openFromFile(file, loadFromFile(file));    
  }
  
  private LootCategories loadFromFile(File file) {   
    InputStream stream = null;
    try {
      stream = new FileInputStream(file);
      String uid = getUidFromFileName(file);
      String json = StructureGenRegister.instance.getResourceManager().loadText(uid, stream);      
      return LootCategeoriesParser.parseLootCategories(uid, json);
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