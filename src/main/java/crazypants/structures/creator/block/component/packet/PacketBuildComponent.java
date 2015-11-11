package crazypants.structures.creator.block.component.packet;

import java.util.List;
import java.util.Map;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.block.component.TileComponentTool;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.structure.StructureBlock;
import crazypants.structures.gen.structure.StructureComponentNBT;
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
    
    IStructureComponent component = StructureGenRegister.instance.getStructureComponent(message.structureName, true);
    if(component == null) {
      return null;
    }    
    tile.setComponent(message.structureName , component);
    
    AxisAlignedBB bb = tile.getStructureBounds();
    World wld = tile.getWorldObj();
    Rotation rotation = Rotation.DEG_0;
    int x = (int) bb.minX;
    int y = (int) bb.minY;
    int z = (int) bb.minZ;
    component.build(wld, x, y, z, rotation, null);
    
    if(component instanceof StructureComponentNBT) {
      StructureComponentNBT cnbt = (StructureComponentNBT)component;
      Map<StructureBlock, List<Point3i>> blks = cnbt.getBlocks();
      for(StructureBlock blk : blks.keySet()) {
        if(blk.isAir()) {
          List<Point3i> locs = blks.get(blk);
          if(locs != null) {            
            for(Point3i loc : locs) {
              loc = VecUtil.rotatePosition(loc, component, rotation);
              wld.setBlock(x + loc.x, y + loc.y, z + loc.z, EnderStructuresCreator.blockClearMarker);    
            }
          }
          
        }
      }
    }
    return null;
  }

  private void clearBounds(TileComponentTool tile) {
    
    tile.getTaggedLocations().clear();
    tile.markDirty();
    
    AxisAlignedBB bb = tile.getStructureBounds();
    World wld = tile.getWorldObj();
    for (int x = (int) bb.minX; x < bb.maxX; x++) {
      for (int y = (int) bb.minY; y < bb.maxY; y++) {
        for (int z = (int) bb.minZ; z < bb.maxZ; z++) {
          wld.setBlockToAir(x, y, z);;
        }
      }
    }
    @SuppressWarnings("unchecked")
    List<EntityItem> ents = wld.getEntitiesWithinAABB(EntityItem.class, tile.getStructureBounds());
    if(ents == null || ents.isEmpty()) {
      return;
    }    
    for(EntityItem item : ents) {
      item.setDead();
    }
  }

}
