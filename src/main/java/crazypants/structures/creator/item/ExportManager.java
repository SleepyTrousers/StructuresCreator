package crazypants.structures.creator.item;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.config.Config;
import crazypants.structures.gen.io.GsonIO;
import crazypants.structures.gen.io.ResourceWrapper;
import crazypants.structures.gen.structure.StructureComponentNBT;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class ExportManager {

  private File exportDir;

  public static final ExportManager instance = new ExportManager();

  public ExportManager() {            
  }

  public File getDefaultDirectory() {
    if(exportDir != null) {
      return exportDir;
    }
    if(Config.configDirectory != null) {
      exportDir = new File(Config.configDirectory.getAbsolutePath()); 
      return exportDir;
    }
    return new File("StructureCreator");
  }
  
  public static boolean writeToFile(File file, StructureComponentNBT st, EntityPlayer player) {
    boolean saved = false;
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file, false);
      st.write(fos);
      fos.flush();
      fos.close();
      saved = true;
      if(player != null) {
        player.addChatComponentMessage(new ChatComponentText("Saved to " + file.getAbsolutePath()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      if(player != null) {
        player.addChatComponentMessage(new ChatComponentText("Could not save to " + file.getAbsolutePath()));
      }
    } finally {
      IOUtils.closeQuietly(fos);
    }
    return saved;
  }

  public static boolean writeToFile(File file, IStructureTemplate curTemplate, EntityClientPlayerMP player) {

    String json = null;
    try {            
      ResourceWrapper rw = new ResourceWrapper();
      rw.setStructureTemplate(curTemplate);      
      json = GsonIO.INSTANCE.getGson().toJson(rw);
    } catch (Exception e) {
      e.printStackTrace();
      if(player != null) {
        player.addChatComponentMessage(new ChatComponentText("Could not generate json: " + e.getMessage()));
      }
      return false;
    }

    try {
      FileUtils.write(file, json);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      if(player != null) {
        player.addChatComponentMessage(new ChatComponentText("Could not save to " + file.getAbsolutePath()));
      }
    }
    return false;
  }

}
