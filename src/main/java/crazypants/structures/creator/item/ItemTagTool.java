package crazypants.structures.creator.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.EmptyContainer;
import crazypants.structures.creator.block.component.EditorRegister;
import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.creator.block.component.gui.GuiTagEditor;
import crazypants.structures.creator.block.component.packet.PacketAddRemoveTaggedLocation;
import crazypants.structures.creator.endercore.Util;
import crazypants.structures.creator.endercore.Vector3d;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemTagTool extends Item implements IGuiHandler {

  public static final String NAME = "itemTagTool";

  // dont like this, but need to pass info from onItemFirstUsed to getClientGui
  private TileComponentEditor currentTile;

  public static ItemTagTool create() {
    ItemTagTool res = new ItemTagTool();
    res.init();
    return res;
  }

  private ItemTagTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
    EnderStructuresCreator.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TAG_EDITOR, this);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (currentTile == null) {
      return null;
    }
    return new EmptyContainer();
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (currentTile == null) {
      return null;
    }
    return new GuiTagEditor(player, player.inventory, currentTile, new Point3i(x, y, z));
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
    return true;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

    if (world.isRemote) {
      player.swingItem();
    }

    // Catch the editing of tagged 'air' blocks
    Vec3 startVec = Util.getEyePosition(player);
    Vector3d end3d = Util.getLookVecEio(player);
    end3d.scale(6);
    end3d.add(startVec.xCoord, startVec.yCoord, startVec.zCoord);
    Vec3 endVec = new Vec3(end3d.x, end3d.y, end3d.z);

    List<Hit> coordsInSight = getBlocksInPath(world, startVec, endVec, false, false, true, true);
    for (Hit hit : coordsInSight) {
      currentTile = EditorRegister.getTooleRegister(world).getClosestTileInBounds(world, hit.blockCoord.x, hit.blockCoord.y, hit.blockCoord.z);
      if (currentTile != null) {
        if (hit.isSolid) {
          return stack;
        }
        Point3i localPos = currentTile.getStructureLocalPosition(hit.blockCoord);
        Collection<String> tags = currentTile.getTagsAtLocation(localPos);
        if (!tags.isEmpty()) {
          if (world.isRemote) {
            player.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_TAG_EDITOR, world, localPos.x, localPos.y, localPos.z);
          }

          return stack;
        }
      }
    }

    return stack;
  }

  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {

    currentTile = EditorRegister.getTooleRegister(world).getClosestTileInBounds(world, pos.getX(), pos.getY(), pos.getZ());
    if (currentTile == null) {
      return true;
    }

    BlockPos tp = currentTile.getPos();
    int localX = pos.getX() - tp.getX() - currentTile.getOffsetX();
    int localY = pos.getY() - tp.getY() - currentTile.getOffsetY();
    int localZ = pos.getZ() - tp.getZ() - currentTile.getOffsetZ();

    Point3i offset = new Point3i(localX, localY, localZ);

    if (!currentTile.hasTagAt(offset)) {
      String tag = "Tag";
      currentTile.addTag(tag, offset);
      PacketHandler.INSTANCE.sendToServer(new PacketAddRemoveTaggedLocation(currentTile, tag, offset, true));
    }

    if (world.isRemote) {
      player.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_TAG_EDITOR, world, localX, localY, localZ);
    }

    return true;
  }

  //code adapted from World#rayTraceBlocks to continue past the first hit
  public static List<Hit> getBlocksInPath(World world, Vec3 startVec, Vec3 endVec, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox,
      boolean returnLastUncollidableBlock, boolean includeAir) {

    List<Hit> result = new ArrayList<Hit>();

    if (!Double.isNaN(startVec.xCoord) && !Double.isNaN(startVec.yCoord) && !Double.isNaN(startVec.zCoord)) {
      if (!Double.isNaN(endVec.xCoord) && !Double.isNaN(endVec.yCoord) && !Double.isNaN(endVec.zCoord)) {

        int endX = MathHelper.floor_double(endVec.xCoord);
        int endY = MathHelper.floor_double(endVec.yCoord);
        int endZ = MathHelper.floor_double(endVec.zCoord);
        int startX = MathHelper.floor_double(startVec.xCoord);
        int startY = MathHelper.floor_double(startVec.yCoord);
        int startZ = MathHelper.floor_double(startVec.zCoord);
        BlockPos blockpos = new BlockPos(startX, startY, startZ);
        IBlockState iblockstate = world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (includeAir && block == Blocks.air) {
          result.add(new Hit(blockpos, false));
        } else if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(world, blockpos, iblockstate) != null)
            && block.canCollideCheck(iblockstate, stopOnLiquid)) {
          MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, blockpos, startVec, endVec);
          if (movingobjectposition != null) {
            result.add(new Hit(blockpos, true));
          }
        }

        int k1 = 200;

        while (k1-- >= 0) {
          if (Double.isNaN(startVec.xCoord) || Double.isNaN(startVec.yCoord) || Double.isNaN(startVec.zCoord)) {
            return result;
          }

          if (startX == endX && startY == endY && startZ == endZ) {
            if (returnLastUncollidableBlock) {
              result.add(new Hit(blockpos, true));
            }
            return result;
          }

          boolean flag2 = true;
          boolean flag = true;
          boolean flag1 = true;
          double d0 = 999.0D;
          double d1 = 999.0D;
          double d2 = 999.0D;

          if (endX > startX) {
            d0 = (double) startX + 1.0D;
          } else if (endX < startX) {
            d0 = (double) startX + 0.0D;
          } else {
            flag2 = false;
          }

          if (endY > startY) {
            d1 = (double) startY + 1.0D;
          } else if (endY < startY) {
            d1 = (double) startY + 0.0D;
          } else {
            flag = false;
          }

          if (endZ > startZ) {
            d2 = (double) startZ + 1.0D;
          } else if (endZ < startZ) {
            d2 = (double) startZ + 0.0D;
          } else {
            flag1 = false;
          }

          double d3 = 999.0D;
          double d4 = 999.0D;
          double d5 = 999.0D;
          double d6 = endVec.xCoord - startVec.xCoord;
          double d7 = endVec.yCoord - startVec.yCoord;
          double d8 = endVec.zCoord - startVec.zCoord;

          if (flag2) {
            d3 = (d0 - startVec.xCoord) / d6;
          }

          if (flag) {
            d4 = (d1 - startVec.yCoord) / d7;
          }

          if (flag1) {
            d5 = (d2 - startVec.zCoord) / d8;
          }

          if (d3 == -0.0D) {
            d3 = -1.0E-4D;
          }

          if (d4 == -0.0D) {
            d4 = -1.0E-4D;
          }

          if (d5 == -0.0D) {
            d5 = -1.0E-4D;
          }

          EnumFacing enumfacing;

          if (d3 < d4 && d3 < d5) {
            enumfacing = endX > startX ? EnumFacing.WEST : EnumFacing.EAST;
            startVec = new Vec3(d0, startVec.yCoord + d7 * d3, startVec.zCoord + d8 * d3);
          } else if (d4 < d5) {
            enumfacing = endY > startY ? EnumFacing.DOWN : EnumFacing.UP;
            startVec = new Vec3(startVec.xCoord + d6 * d4, d1, startVec.zCoord + d8 * d4);
          } else {
            enumfacing = endZ > startZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
            startVec = new Vec3(startVec.xCoord + d6 * d5, startVec.yCoord + d7 * d5, d2);
          }

          startX = MathHelper.floor_double(startVec.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
          startY = MathHelper.floor_double(startVec.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
          startZ = MathHelper.floor_double(startVec.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
          blockpos = new BlockPos(startX, startY, startZ);
          IBlockState iblockstate1 = world.getBlockState(blockpos);
          Block block1 = iblockstate1.getBlock();
          
          if (includeAir && block1 == Blocks.air) {
            result.add(new Hit(blockpos, false));
          } else if (!ignoreBlockWithoutBoundingBox || block1.getCollisionBoundingBox(world, blockpos, iblockstate1) != null) {            
            if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
              MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, blockpos, startVec, endVec);
              if (movingobjectposition1 != null) {
                result.add(new Hit(blockpos, true));                
              }
            } 
          }
        }

        if (returnLastUncollidableBlock) {
          result.add(new Hit(blockpos, true));
        }
        return result;        
      } else {
        return result;
      }
    } else {
      return result;
    }
  }
  
  public static class Hit {
    
    public final Point3i blockCoord = new Point3i();
    public final boolean isSolid;

    public Hit(Point3i blockCoord, boolean isSolid) {
      if (blockCoord != null) {
        this.blockCoord.set(blockCoord.x, blockCoord.y, blockCoord.z);
      }
      this.isSolid = isSolid;
    }

    public Hit(BlockPos pos, boolean isSolid) {
      if (pos != null) {
        this.blockCoord.set(pos.getX(), pos.getY(), pos.getZ());
      }
      this.isSolid = isSolid;
    }

    @Override
    public String toString() {
      return "Hit [blockCoord=" + blockCoord + ", isSolid=" + isSolid + "]";
    }

  }

}
