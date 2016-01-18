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

  private static final String NAME = "itemTagTool";

  //dont like this, but need to pass info from onItemFirstUsed to getClientGui
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
    if(currentTile == null) {
      return null;
    }
    return new EmptyContainer();
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if(currentTile == null) {
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

    if(world.isRemote) {
      player.swingItem();
    }
    
    //Catch the editing of tagged 'air' blocks
    Vec3 startVec = Util.getEyePosition(player);
    Vector3d end3d = Util.getLookVecEio(player);
    end3d.scale(6);
    end3d.add(startVec.xCoord, startVec.yCoord, startVec.zCoord);
    Vec3 endVec = new Vec3(end3d.x, end3d.y, end3d.z);

    List<Hit> coordsInSight = getBlocksInPath(world, startVec, endVec, true, true);
    for (Hit hit : coordsInSight) {
      currentTile = EditorRegister.getTooleRegister(world).getClosestTileInBounds(world, hit.blockCoord.x, hit.blockCoord.y, hit.blockCoord.z);
      if(currentTile != null) {
        if(hit.isSolid) {
          return stack;
        }
        Point3i localPos = currentTile.getStructureLocalPosition(hit.blockCoord);
        Collection<String> tags = currentTile.getTagsAtLocation(localPos);
        if(!tags.isEmpty()) {          
          if(world.isRemote) {
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
    if(currentTile == null) {
      return true;
    }

    BlockPos tp = currentTile.getPos();
    int localX = pos.getX() - tp.getX() - currentTile.getOffsetX();
    int localY = pos.getY() - tp.getY() - currentTile.getOffsetY();
    int localZ = pos.getZ() - tp.getZ() - currentTile.getOffsetZ();

    Point3i offset = new Point3i(localX, localY, localZ);

    if(!currentTile.hasTagAt(offset)) {      
      String tag = "Tag";
      currentTile.addTag(tag, offset);
      PacketHandler.INSTANCE.sendToServer(new PacketAddRemoveTaggedLocation(currentTile, tag, offset, true));
    }

    if(world.isRemote) {
      player.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_TAG_EDITOR, world, localX, localY, localZ);
    }

    return true;
  }

  //Code adapted from World.func_147447_a (rayTraceBlocks) to return all collided blocks
  public static List<Hit> getBlocksInPath(World world, Vec3 startVec, Vec3 endVec, boolean includeLiquids, boolean includeAir) {

    List<Hit> result = new ArrayList<Hit>();
    boolean p_147447_4_ = false;
    boolean p_147447_5_ = false;

    if(!Double.isNaN(startVec.xCoord) && !Double.isNaN(startVec.yCoord) && !Double.isNaN(startVec.zCoord)) {
      if(!Double.isNaN(endVec.xCoord) && !Double.isNaN(endVec.yCoord) && !Double.isNaN(endVec.zCoord)) {

        int endX = MathHelper.floor_double(endVec.xCoord);
        int endY = MathHelper.floor_double(endVec.yCoord);
        int endZ = MathHelper.floor_double(endVec.zCoord);
        int startX = MathHelper.floor_double(startVec.xCoord);
        int startY = MathHelper.floor_double(startVec.yCoord);
        int startZ = MathHelper.floor_double(startVec.zCoord);
        BlockPos startPos = new BlockPos(startX, startY, startZ);
        IBlockState blockSt = world.getBlockState(startPos);        
       
        if(includeAir && blockSt.getBlock() == Blocks.air) {
          result.add(new Hit(startPos, false));
        } else if((!p_147447_4_ || blockSt.getBlock().getCollisionBoundingBox(world, startPos, blockSt) != null) && blockSt.getBlock().canCollideCheck(blockSt, includeLiquids)) {          
          MovingObjectPosition movingobjectposition1 = blockSt.getBlock().collisionRayTrace(world, startPos, startVec, endVec);
          if(movingobjectposition1 != null) {
            result.add(new Hit(startPos, true));
          }
        }

        Point3i movingobjectposition2 = null;
        int k1 = 200;

        while (k1-- >= 0) {
          if(Double.isNaN(startVec.xCoord) || Double.isNaN(startVec.yCoord) || Double.isNaN(startVec.zCoord)) {
            return null;
          }

          if(startX == endX && startY == endY && startZ == endZ) {
            if(p_147447_5_) {
              result.add(new Hit(movingobjectposition2, true));
            } else {
              return result;
            }
          }

          boolean flag6 = true;
          boolean flag3 = true;
          boolean flag4 = true;
          double d0 = 999.0D;
          double d1 = 999.0D;
          double d2 = 999.0D;

          if(endX > startX) {
            d0 = startX + 1.0D;
          } else if(endX < startX) {
            d0 = startX + 0.0D;
          } else {
            flag6 = false;
          }

          if(endY > startY) {
            d1 = startY + 1.0D;
          } else if(endY < startY) {
            d1 = startY + 0.0D;
          } else {
            flag3 = false;
          }

          if(endZ > startZ) {
            d2 = startZ + 1.0D;
          } else if(endZ < startZ) {
            d2 = startZ + 0.0D;
          } else {
            flag4 = false;
          }

          double d3 = 999.0D;
          double d4 = 999.0D;
          double d5 = 999.0D;
          double d6 = endVec.xCoord - startVec.xCoord;
          double d7 = endVec.yCoord - startVec.yCoord;
          double d8 = endVec.zCoord - startVec.zCoord;

          if(flag6) {
            d3 = (d0 - startVec.xCoord) / d6;
          }
          if(flag3) {
            d4 = (d1 - startVec.yCoord) / d7;
          }
          if(flag4) {
            d5 = (d2 - startVec.zCoord) / d8;
          }

          byte b0;
          if(d3 < d4 && d3 < d5) {
            if(endX > startX) {
              b0 = 4;
            } else {
              b0 = 5;
            }
            startVec = new Vec3(d0, startVec.yCoord + d7 * d3, startVec.zCoord + d8 * d3);
                        
          } else if(d4 < d5) {
            if(endY > startY) {
              b0 = 0;
            } else {
              b0 = 1;
            }
            startVec = new Vec3(startVec.xCoord + d6 * d4, d1, startVec.zCoord + d8 * d4);
            
          } else {
            if(endZ > startZ) {
              b0 = 2;
            } else {
              b0 = 3;
            }
            startVec = new Vec3(startVec.xCoord + d6 * d5, startVec.yCoord + d7 * d5, d2);            
          }

          Vec3 vec32 = new Vec3(MathHelper.floor_double(startVec.xCoord), MathHelper.floor_double(startVec.yCoord), MathHelper.floor_double(startVec.zCoord));
          startX = (int) vec32.xCoord;
          if(b0 == 5) {
            --startX;            
          }

          startY = (int)vec32.yCoord;
          if(b0 == 1) {
            --startY;            
          }

          startZ = (int)vec32.zCoord;
          if(b0 == 3) {
            --startZ;
          }

          startPos = new BlockPos(startX, startY, startZ);
          IBlockState bs1 = world.getBlockState(startPos);                    

          if(includeAir && blockSt == Blocks.air) {
            result.add(new Hit(startPos, false));
          } else if(!p_147447_4_ || bs1.getBlock().getCollisionBoundingBox(world, startPos,bs1) != null) {
            if(bs1.getBlock().canCollideCheck(bs1, includeLiquids)) {
              MovingObjectPosition movingobjectposition1 = bs1.getBlock().collisionRayTrace(world, startPos, startVec, endVec);
              if(movingobjectposition1 != null) {
                result.add(new Hit(startPos, true));
              }
            } else {
              movingobjectposition2 = new Point3i(startX, startY, startZ);
            }
          }
        }
        if(p_147447_5_) {
          result.add(new Hit(movingobjectposition2, true));
        } else {
          return result;
        }
      } else {
        return result;
      }
    } else {
      return result;
    }
    return result;
  }
  
  


  public static class Hit {
    public final Point3i blockCoord = new Point3i();
    public final boolean isSolid;

    public Hit(Point3i blockCoord, boolean isSolid) {
      if(blockCoord != null) {
        this.blockCoord.set(blockCoord.x, blockCoord.y, blockCoord.z);
      }
      this.isSolid = isSolid;
    }

    public Hit(BlockPos pos, boolean isSolid) {
      if(pos != null) {
        this.blockCoord.set(pos.getX(), pos.getY(), pos.getZ());
      }
      this.isSolid = isSolid;
    }

  }

}
