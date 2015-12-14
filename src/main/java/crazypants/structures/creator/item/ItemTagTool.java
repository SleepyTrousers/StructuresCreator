package crazypants.structures.creator.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.creator.block.EmptyContainer;
import crazypants.structures.creator.block.component.EditorRegister;
import crazypants.structures.creator.block.component.gui.GuiTagEditor;
import crazypants.structures.creator.block.component.packet.PacketAddRemoveTaggedLocation;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

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
    setTextureName(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
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
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    if(world.isRemote) {
      return true;
    }
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
    Vec3 endVec = Vec3.createVectorHelper(end3d.x, end3d.y, end3d.z);

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
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    currentTile = EditorRegister.getTooleRegister(world).getClosestTileInBounds(world, x, y, z);
    if(currentTile == null) {
      return true;
    }

    int localX = x - currentTile.xCoord - currentTile.getOffsetX();
    int localY = y - currentTile.yCoord - currentTile.getOffsetY();
    int localZ = z - currentTile.zCoord - currentTile.getOffsetZ();

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

        int i = MathHelper.floor_double(endVec.xCoord);
        int j = MathHelper.floor_double(endVec.yCoord);
        int k = MathHelper.floor_double(endVec.zCoord);
        int l = MathHelper.floor_double(startVec.xCoord);
        int i1 = MathHelper.floor_double(startVec.yCoord);
        int j1 = MathHelper.floor_double(startVec.zCoord);
        Block block = world.getBlock(l, i1, j1);
        int k1 = world.getBlockMetadata(l, i1, j1);
        Point3i point = new Point3i(l, i1, j1);

        if(includeAir && block == Blocks.air) {
          result.add(new Hit(point, false));
        } else if((!p_147447_4_ || block.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) && block.canCollideCheck(k1, includeLiquids)) {
          MovingObjectPosition movingobjectposition1 = block.collisionRayTrace(world, l, i1, j1, startVec, endVec);
          if(movingobjectposition1 != null) {
            result.add(new Hit(point, true));
          }
        }

        Point3i movingobjectposition2 = null;
        k1 = 200;

        while (k1-- >= 0) {
          if(Double.isNaN(startVec.xCoord) || Double.isNaN(startVec.yCoord) || Double.isNaN(startVec.zCoord)) {
            return null;
          }

          if(l == i && i1 == j && j1 == k) {
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

          if(i > l) {
            d0 = l + 1.0D;
          } else if(i < l) {
            d0 = l + 0.0D;
          } else {
            flag6 = false;
          }

          if(j > i1) {
            d1 = i1 + 1.0D;
          } else if(j < i1) {
            d1 = i1 + 0.0D;
          } else {
            flag3 = false;
          }

          if(k > j1) {
            d2 = j1 + 1.0D;
          } else if(k < j1) {
            d2 = j1 + 0.0D;
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
            if(i > l) {
              b0 = 4;
            } else {
              b0 = 5;
            }

            startVec.xCoord = d0;
            startVec.yCoord += d7 * d3;
            startVec.zCoord += d8 * d3;
          } else if(d4 < d5) {
            if(j > i1) {
              b0 = 0;
            } else {
              b0 = 1;
            }

            startVec.xCoord += d6 * d4;
            startVec.yCoord = d1;
            startVec.zCoord += d8 * d4;
          } else {
            if(k > j1) {
              b0 = 2;
            } else {
              b0 = 3;
            }

            startVec.xCoord += d6 * d5;
            startVec.yCoord += d7 * d5;
            startVec.zCoord = d2;
          }

          Vec3 vec32 = Vec3.createVectorHelper(startVec.xCoord, startVec.yCoord, startVec.zCoord);
          l = (int) (vec32.xCoord = MathHelper.floor_double(startVec.xCoord));
          if(b0 == 5) {
            --l;
            ++vec32.xCoord;
          }

          i1 = (int) (vec32.yCoord = MathHelper.floor_double(startVec.yCoord));

          if(b0 == 1) {
            --i1;
            ++vec32.yCoord;
          }

          j1 = (int) (vec32.zCoord = MathHelper.floor_double(startVec.zCoord));

          if(b0 == 3) {
            --j1;
            ++vec32.zCoord;
          }

          Block block1 = world.getBlock(l, i1, j1);
          int l1 = world.getBlockMetadata(l, i1, j1);
          point.set(l, i1, j1);

          if(includeAir && block == Blocks.air) {
            result.add(new Hit(point, false));
          } else if(!p_147447_4_ || block1.getCollisionBoundingBoxFromPool(world, l, i1, j1) != null) {
            if(block1.canCollideCheck(l1, includeLiquids)) {
              MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, l, i1, j1, startVec, endVec);
              if(movingobjectposition1 != null) {
                result.add(new Hit(point, true));
              }
            } else {
              movingobjectposition2 = new Point3i(l, i1, j1);
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

  }

}
