package crazypants.structures.creator;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class PacketHandler {

  public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(EnderStructuresCreator.MODID);

  private static int ID = 0;

  public static int nextID() {
    return ID++;
  }

  public static void sendToAllAround(IMessage message, TileEntity te, int range) {
    INSTANCE.sendToAllAround(message, new TargetPoint(te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord, range));
  }

  public static void sendToAllAround(IMessage message, TileEntity te) {
    sendToAllAround(message, te, 64);
  }

  public static void sendTo(IMessage message, EntityPlayerMP player) {
    INSTANCE.sendTo(message, player);
  }

}
