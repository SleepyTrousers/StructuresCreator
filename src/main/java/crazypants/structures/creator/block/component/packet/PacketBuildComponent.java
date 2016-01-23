package crazypants.structures.creator.block.component.packet;

import java.util.List;
import java.util.Map;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.structure.StructureBlock;
import crazypants.structures.gen.structure.StructureComponentNBT;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBuildComponent extends MessageTileEntity<TileComponentEditor> implements IMessageHandler<PacketBuildComponent, IMessage> {

  private String structureName;

  public PacketBuildComponent() {
    super();
  }

  public PacketBuildComponent(TileComponentEditor tile, String structureName) {
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
    TileComponentEditor tile = message.getTileEntity(player.worldObj);
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
    tile.setComponent(component);
    
    AxisAlignedBB bb = tile.getStructureBounds();
    World wld = tile.getWorld();
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
              wld.setBlockState(new BlockPos(x + loc.x, y + loc.y, z + loc.z), EnderStructuresCreator.blockClearMarker.getDefaultState());    
            }
          }          
        }
      }
    }
    return null;
  }

  private void clearBounds(TileComponentEditor tile) {
    
    tile.getTaggedLocations().clear();
    tile.markDirty();
    
    AxisAlignedBB bb = tile.getStructureBounds();
    World wld = tile.getWorld();
    StructureUtils.clearBounds(bb, wld);
  }

  

}
