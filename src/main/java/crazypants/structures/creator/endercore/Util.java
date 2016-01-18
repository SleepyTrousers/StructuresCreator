package crazypants.structures.creator.endercore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;

public class Util {


  public static Vec3 getEyePosition(EntityPlayer player) {
    double y = player.posY;
    if (player.worldObj.isRemote) {
      //take into account any eye changes done by mods.
      y += player.getEyeHeight() - player.getDefaultEyeHeight();
    } else {
      y += player.getEyeHeight();
      if (player instanceof EntityPlayerMP && player.isSneaking()) {
        y -= 0.08;
      }
    }
    return new Vec3(player.posX, y, player.posZ);
  }
  
  public static Vector3d getLookVecEio(EntityPlayer player) {
    Vec3 lv = player.getLookVec();
    return new Vector3d(lv.xCoord, lv.yCoord, lv.zCoord);
  }
  
}
