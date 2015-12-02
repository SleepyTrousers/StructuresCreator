package crazypants.structures.creator.item;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.GsonIO;
import crazypants.structures.gen.io.resource.DirectoryResourcePath;
import crazypants.structures.gen.structure.StructureComponentNBT;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class ExportManager {

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

  public static void writeToFile(File file, IStructureTemplate curTemplate, EntityClientPlayerMP player) {

    String json = null;
    try {
      json = GsonIO.INSTANCE.getGson().toJson(curTemplate);
    } catch (Exception e) {
      e.printStackTrace();
      if(player != null) {
        player.addChatComponentMessage(new ChatComponentText("Could not generate json: " + e.getMessage()));
      }
      return;
    }

    try {
      FileUtils.write(file, json);
    } catch (IOException e) {
      e.printStackTrace();
      if(player != null) {
        player.addChatComponentMessage(new ChatComponentText("Could not save to " + file.getAbsolutePath()));
      }
    }

  }

}
