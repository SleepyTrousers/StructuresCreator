package crazypants.structures.creator.block.component;

import java.util.List;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.StructureGenRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class PacketBuildComponent extends MessageTileEntity<TileComponentTool> implements IMessageHandler<PacketBuildComponent, IMessage> {

  private String structureName;

  public PacketBuildComponent() {
    super();
  }

  public PacketBuildComponent(TileComponentTool tile, String structureName) {
    super(tile);
    this.structureName = structureName;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if(structureName == null) {
      structureName = "";
    }
    ByteBufUtils.writeUTF8String(buf, structureName);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    structureName = ByteBufUtils.readUTF8String(buf);
    if(structureName.length() == 0) {
      structureName = null;
    }
  }

  //@Override
  @Override
  public IMessage onMessage(PacketBuildComponent message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileComponentTool tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }

    clearBounds(tile);
    if(message.structureName == null) {
      return null;
    }
    
    IStructureComponent c = StructureGenRegister.instance.getStructureComponent(message.structureName, true);
    if(c == null) {
      return null;
    }
    
    tile.setComponent(message.structureName , c);
    
    AxisAlignedBB bb = tile.getStructureBounds();
    World wld = tile.getWorldObj();
    c.build(wld, (int) bb.minX, (int) bb.minY, (int) bb.minZ, Rotation.DEG_0, null);
    

    return null;
  }

  private void clearBounds(TileComponentTool tile) {
    
    AxisAlignedBB bb = tile.getStructureBounds();
    System.out.println("PacketBuildComponent.clearBounds: " + bb);
    World wld = tile.getWorldObj();
    for (int x = (int) bb.minX; x < bb.maxX; x++) {
      for (int y = (int) bb.minY; y < bb.maxY; y++) {
        for (int z = (int) bb.minZ; z < bb.maxZ; z++) {
          wld.setBlockToAir(x, y, z);
          //wld.setBlock(x, y, z, Blocks.air, 0, 0);
        }
      }
    }
    List<EntityItem> ents = wld.getEntitiesWithinAABB(EntityItem.class, tile.getStructureBounds());
    if(ents == null || ents.isEmpty()) {
      return;
    }
    
    System.out.println("PacketBuildComponent.clearBounds: " + ents.size());
    for(EntityItem item : ents) {
      item.setDead();
    }
  }

}
