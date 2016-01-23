package crazypants.structures.creator.block.template.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.structures.creator.block.AbstractResourceTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketResourceTileGui extends MessageTileEntity<AbstractResourceTile> implements IMessageHandler<PacketResourceTileGui, IMessage> {

  
  private String name;
  private String exportDir;
  
  public PacketResourceTileGui() {            
  }
  
  public PacketResourceTileGui(AbstractResourceTile tile) {
    super(tile);
    name = tile.getName();
    exportDir = tile.getExportDir();
  }

  @Override
  public void toBytes(ByteBuf buf) {  
    super.toBytes(buf);
    ByteBufUtils.writeUTF8String(buf, name == null ? "" : name);
    ByteBufUtils.writeUTF8String(buf, exportDir == null ? "" : exportDir);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    name = ByteBufUtils.readUTF8String(buf);
    exportDir = ByteBufUtils.readUTF8String(buf);
  }

  @Override
  public IMessage onMessage(PacketResourceTileGui message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    AbstractResourceTile tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }
    tile.setExportDir(message.exportDir);
    tile.setName(message.name);    
    return null;
    
  }

}
