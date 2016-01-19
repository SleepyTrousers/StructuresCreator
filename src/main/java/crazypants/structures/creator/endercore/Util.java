package crazypants.structures.creator.endercore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class Util {


  public static Vec3 getEyePosition(EntityPlayer player) {
    double y = player.posY;
    y += player.getEyeHeight();
    //This dose not seem to be needed anymore since 1.8.9
//    if (player.worldObj.isRemote) {
//      //take into account any eye changes done by mods.
//      y += player.getEyeHeight() - player.getDefaultEyeHeight();
//    } else {
//      y += player.getEyeHeight();
//      if (player instanceof EntityPlayerMP && player.isSneaking()) {
//        y -= 0.08;
//      }
//    }
//    System.out.println("Util.getEyePosition: " + player.posY + " " + y);
    return new Vec3(player.posX, y, player.posZ);
  }
  
  public static Vector3d getLookVecEio(EntityPlayer player) {
    Vec3 lv = player.getLookVec();
    return new Vector3d(lv.xCoord, lv.yCoord, lv.zCoord);
  }
  
}
