package crazypants.structures.creator.block.component;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.structures.api.util.Point3i;
import io.netty.buffer.ByteBuf;

public class PacketAddRemoveTaggedLocation extends MessageTileEntity<TileComponentTool> implements IMessageHandler<PacketAddRemoveTaggedLocation, IMessage> {

  private String tag;
  private Point3i loc;
  private boolean isAdd;

  public PacketAddRemoveTaggedLocation() {
    super();
  }

  public PacketAddRemoveTaggedLocation(TileComponentTool tile, String tag, Point3i loc, boolean isAdd) {
    super(tile);
    this.tag= tag;
    this.loc = loc;
    this.isAdd = isAdd;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    ByteBufUtils.writeUTF8String(buf, tag);
    buf.writeBoolean(loc != null);
    if(loc != null) {
      buf.writeInt(loc.x);
      buf.writeInt(loc.y);
      buf.writeInt(loc.z);
    }
    buf.writeBoolean(isAdd);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    tag = ByteBufUtils.readUTF8String(buf);
    boolean hasLoc = buf.readBoolean();
    if(hasLoc) {
      loc = new Point3i(buf.readInt(), buf.readInt(), buf.readInt());
    }
    isAdd = buf.readBoolean();
  }

  @Override
  public IMessage onMessage(PacketAddRemoveTaggedLocation message, MessageContext ctx) {
    TileComponentTool te = message.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
    if(te == null) {
      return null;
    }
    
    if(message.isAdd) {
      te.addTag(message.tag, message.loc);
    } else {
      if(loc != null) {
        te.removeTag(message.tag, message.loc);        
      } else {
        te.removeTags(message.tag);
      }
    }    
    return null;
  }

}
