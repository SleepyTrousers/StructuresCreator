package crazypants.structures.creator.block.component.packet;

import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.creator.endercore.MessageTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketComponentEditorGui extends MessageTileEntity<TileComponentEditor> implements IMessageHandler<PacketComponentEditorGui, IMessage> {

  
  private NBTTagCompound data;
  
  public PacketComponentEditorGui() {    
    
  }
  
  public PacketComponentEditorGui(TileComponentEditor tile) {
    super(tile);
    data = new NBTTagCompound();
    tile.writeCustomNBT(data);
  }

  @Override
  public void toBytes(ByteBuf buf) {  
    super.toBytes(buf);
    ByteBufUtils.writeTag(buf, data);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    data = ByteBufUtils.readTag(buf);
  }

  @Override
  public IMessage onMessage(PacketComponentEditorGui message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileComponentEditor tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }
    tile.readCustomNBT(message.data);
    return null;    
  }

}
