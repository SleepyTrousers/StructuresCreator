package crazypants.structures.creator.block.component.packet;

import java.io.File;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.structures.creator.CreatorUtil;
import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.structure.StructureComponentNBT;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSaveComponent extends MessageTileEntity<TileComponentEditor> implements IMessageHandler<PacketSaveComponent, IMessage> {

  private File file;
  private String uid;

  public PacketSaveComponent() {
    super();
  }

  public PacketSaveComponent(TileComponentEditor tile, File file, String uid) {
    super(tile);
    this.file = file;
    this.uid = uid;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    ByteBufUtils.writeUTF8String(buf, uid);
    ByteBufUtils.writeUTF8String(buf, file.getAbsolutePath());
    
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    uid = ByteBufUtils.readUTF8String(buf);
    file = new File(ByteBufUtils.readUTF8String(buf));    
  }

  
  @Override
  public IMessage onMessage(PacketSaveComponent message, MessageContext ctx) {

    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileComponentEditor tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }
    StructureComponentNBT comp = CreatorUtil.createComponent(message.uid, tile.getWorld(), tile.getStructureBounds(), tile.getSurfaceOffset());
    comp.setTags(tile.getTaggedLocations());
    if(ExportManager.writeToFile(message.file, comp, Minecraft.getMinecraft().thePlayer)) {
      StructureGenRegister.instance.registerStructureComponent(comp);
    }    
    return null;
  }

}
