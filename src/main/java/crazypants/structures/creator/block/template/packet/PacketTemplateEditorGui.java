package crazypants.structures.creator.block.template.packet;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.structures.creator.block.template.TileTemplateEditor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketTemplateEditorGui extends MessageTileEntity<TileTemplateEditor> implements IMessageHandler<PacketTemplateEditorGui, IMessage> {

  
  private String name;
  private String exportDir;
  
  public PacketTemplateEditorGui() {        
  }
  
  public PacketTemplateEditorGui(TileTemplateEditor tile) {
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
  public IMessage onMessage(PacketTemplateEditorGui message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileTemplateEditor tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }
    tile.setExportDir(message.exportDir);
    tile.setName(message.name);    
    return null;
    
  }

}
