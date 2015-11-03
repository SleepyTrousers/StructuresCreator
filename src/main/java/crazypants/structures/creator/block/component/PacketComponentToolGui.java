package crazypants.structures.creator.block.component;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PacketComponentToolGui extends MessageTileEntity<TileComponentTool> implements IMessageHandler<PacketComponentToolGui, IMessage> {

  
  private NBTTagCompound data;
  
  public PacketComponentToolGui() {    
    
  }
  
  public PacketComponentToolGui(TileComponentTool tile) {
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
  public IMessage onMessage(PacketComponentToolGui message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileComponentTool tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }
    tile.readCustomNBT(message.data);
    return null;
    
  }

}
