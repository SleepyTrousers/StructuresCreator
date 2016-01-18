package crazypants.structures.creator.block.component.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.creator.endercore.MessageTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetTaggedLocation extends MessageTileEntity<TileComponentEditor> implements IMessageHandler<PacketSetTaggedLocation, IMessage> {

  private List<String> tags;
  private Point3i loc;

  public PacketSetTaggedLocation() {
    super();
  }

  public PacketSetTaggedLocation(TileComponentEditor tile, Point3i loc, Collection<String> tags) {
    super(tile);
    if(tags != null & !tags.isEmpty()) {
      this.tags = new ArrayList<String>(tags);
    }
    this.loc = loc;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    
    if(tags == null) {
      buf.writeInt(0);
    } else {
      buf.writeInt(tags.size());
      for(String tag : tags) {
        ByteBufUtils.writeUTF8String(buf, tag);    
      }
    }
    buf.writeInt(loc.x);
    buf.writeInt(loc.y);
    buf.writeInt(loc.z);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    
    int numTags = buf.readInt();
    if(numTags > 0) {
      tags = new ArrayList<String>(numTags);
      for(int i=0;i<numTags;i++) {
        tags.add(ByteBufUtils.readUTF8String(buf));
      }
    }    
    loc = new Point3i(buf.readInt(), buf.readInt(), buf.readInt());
  }

  @Override
  public IMessage onMessage(PacketSetTaggedLocation message, MessageContext ctx) {
    TileComponentEditor te = message.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
    if(te == null) {
      return null;
    }
    te.setTagsAtPosition(message.loc, message.tags);
    return null;
  }

}
