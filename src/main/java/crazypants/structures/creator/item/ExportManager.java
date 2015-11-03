package crazypants.structures.creator.item;

import java.io.File;

import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.resource.DirectoryResourcePath;

public class ExportManager {

//  private static String MIN_TEMPLATE = "{" +
//      "\"StructureGenerator\" : { \"uid\" : \"$GEN_UID\"," +
//      "\"templates\" : [" +
//      "{\"uid\" : \"$TEMPLATE_UID\"}" +
//      "]," +
//      "\"LocationSampler\" : { \"type\" : \"SurfaceSampler\"} }" +      
//      "}";

  static final String STRUCT_NAME = "structure";

  public static final File EXPORT_DIR = new File("exportedStructureData");

  public static final ExportManager instance = new ExportManager();

  public ExportManager() {
    if(EXPORT_DIR.exists()) {
      EXPORT_DIR.mkdir();      
    }            
  }

  public void loadExportFolder() {    
    if(!EXPORT_DIR.exists()) {
      return;
    }
    
    StructureGenRegister.instance.getResourceManager().addResourceDirectory(EXPORT_DIR);
    DirectoryResourcePath path = new DirectoryResourcePath(EXPORT_DIR);    
    StructureGenRegister.instance.loadAndRegisterAllResources(path, false);

  }

//  public String getNextExportUid() {
//    String res = STRUCT_NAME;
//    File file = new File(EXPORT_DIR, res + StructureResourceManager.COMPONENT_EXT);
//    int num = 1;
//    while (file.exists() && num < 100) {
//      res = STRUCT_NAME + "_" + num;
//      file = new File(EXPORT_DIR, res + StructureResourceManager.COMPONENT_EXT);
//      num++;
//    }
//    return res;
//  }

//  public static void writeToFile(EntityPlayer entityPlayer, StructureComponentNBT st, boolean createDefaultGenerator) {
//    EXPORT_DIR.mkdir();
//    if(!EXPORT_DIR.exists()) {
//      entityPlayer.addChatComponentMessage(new ChatComponentText("Could not make folder " + EXPORT_DIR.getAbsolutePath()));
//      return;
//    }
//    boolean saved = doWriteToFile(entityPlayer, st);
//    if(!createDefaultGenerator || !saved) {
//      return;
//    }
//  }
//
//
//
//  private static boolean doWriteToFile(EntityPlayer entityPlayer, StructureComponentNBT st) {
//    boolean saved = false;
//    File file = new File(EXPORT_DIR, st.getUid() + StructureResourceManager.COMPONENT_EXT);
//    FileOutputStream fos = null;
//    try {
//      fos = new FileOutputStream(file, false);
//      st.write(fos);
//      fos.flush();
//      fos.close();
//      saved = true;
//      entityPlayer.addChatComponentMessage(new ChatComponentText("Saved to " + file.getAbsolutePath()));
//    } catch (Exception e) {
//      e.printStackTrace();
//      entityPlayer.addChatComponentMessage(new ChatComponentText("Could not save to " + file.getAbsolutePath()));
//    } finally {
//      IOUtils.closeQuietly(fos);
//    }
//    return saved;
//  }

}
