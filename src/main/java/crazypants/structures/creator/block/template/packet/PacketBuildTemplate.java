package crazypants.structures.creator.block.template.packet;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.creator.block.template.TileTemplateEditor;
import crazypants.structures.gen.StructureGenRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class PacketBuildTemplate extends MessageTileEntity<TileTemplateEditor> implements IMessageHandler<PacketBuildTemplate, IMessage> {

  private String structureName;
  private boolean clearOnly;

  public PacketBuildTemplate() {
    super();
  }

  public PacketBuildTemplate(TileTemplateEditor tile, boolean clearOnly) {
    super(tile);
    this.structureName = tile.getName();
    this.clearOnly = clearOnly;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if(structureName == null) {
      structureName = "";
    }
    ByteBufUtils.writeUTF8String(buf, structureName);
    buf.writeBoolean(clearOnly);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    structureName = ByteBufUtils.readUTF8String(buf);
    if(structureName.length() == 0) {
      structureName = null;
    }
    clearOnly = buf.readBoolean();
  }

  //@Override
  @Override
  public IMessage onMessage(PacketBuildTemplate message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileTemplateEditor tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }

    if(tile.getName() != null) {
      loadAndClear(player.worldObj, tile);
    }
    if(message.clearOnly) {
      return null;
    }

    tile.setName(message.structureName);
    if(message.structureName == null) {      
      return null;
    }
    IStructure str = loadAndClear(player.worldObj, tile);    
    str.getTemplate().build(str, player.worldObj, player.worldObj.rand, null);

    return null;
  }

  protected IStructure loadAndClear(World world, TileTemplateEditor tile) {
    IStructureTemplate tmpl = StructureGenRegister.instance.getStructureTemplate(tile.getName(), true);
    if(tmpl != null) {
      IStructure inst = tmpl.createInstance(tile.getRotation() == null ? Rotation.DEG_0 : tile.getRotation());
      inst.setOrigin(new Point3i(tile.xCoord + tile.getOffsetX(), tile.yCoord + tile.getOffsetY(), tile.zCoord + tile.getOffsetZ()));
      clearBounds(world, inst);
      return inst;
    }
    return null;
  }

  private void clearBounds(World wld, IStructure tile) {    
    AxisAlignedBB bb = tile.getBounds();    
    StructureUtils.clearBounds(bb, wld);
  }

}
