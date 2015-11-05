package crazypants.structures.creator.block.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.google.common.collect.Multimap;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.creator.EnderStructuresCreator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RendererComponentTool extends TileEntitySpecialRenderer {

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {

    TileComponentTool ct = (TileComponentTool) te;

    RenderUtil.bindBlockTexture();

    BoundingBox bb = new BoundingBox(ct.getStructureBounds());
    bb = bb.translate(-te.xCoord, -te.yCoord, -te.zCoord);

    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.addTranslation((float) x, (float) y, (float) z);

    CubeRenderer.render(bb, EnderStructuresCreator.blockComponentTool.getIcon(0, 0));

    Tessellator.instance.addTranslation((float) -x, (float) -y, (float) -z);
    Tessellator.instance.draw();

    renderGroundLevel(ct, x, y, z);

    renderTags(ct, x, y, z);

    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glEnable(GL11.GL_LIGHTING);

  }

  private void renderTags(TileComponentTool ct, double wldX, double wldY, double wldZ) {
    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.addTranslation((float) wldX, (float) wldY, (float) wldZ);

    List<TagLocs> locs = new ArrayList<TagLocs>();
    Multimap<Point3i, String> tags = ct.getTagsAtLocations();
    for (Entry<Point3i, Collection<String>> e : tags.asMap().entrySet()) {
      Point3i bc = VecUtil.transformStructureCoodToWorld(0, 0, 0, Rotation.DEG_0, ct.getSize(), e.getKey());
      TagLocs tag = renderTag(ct, bc, e.getValue());
      if(tag != null) {
        locs.add(tag);
      }
    }

    Tessellator.instance.addTranslation(-(float) wldX, -(float) wldY, -(float) wldZ);
    Tessellator.instance.draw();

    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    for (TagLocs t : locs) {
      String tag = "";
      for(String txt : t.tags) {
         tag += txt + ", ";
      }
      tag = tag.substring(0, tag.length() - 2);
      RenderUtil.drawBillboardedText(new Vector3f(wldX + t.pos.x + 0.5, wldY + t.pos.y + 0.5, wldZ + t.pos.z + 0.5), tag, 0.25f);
    }
    GL11.glEnable(GL11.GL_DEPTH_TEST);

  }

  private TagLocs renderTag(TileComponentTool te, Point3i tagOffset, Collection<String> tags) {

    //Local pos
    Point3i tagPos = new Point3i(te.getOffsetX() + tagOffset.x, te.getOffsetY() + tagOffset.y, te.getOffsetZ() + tagOffset.z);
    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(1.02, 1.02, 1.02);
    bb = bb.translate(tagPos.x, tagPos.y, tagPos.z);
    CubeRenderer.render(bb, EnderStructuresCreator.blockComponentTool.getIcon(0, 0));

    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    Vec3 start = Util.getEyePosition(player);
    Vector3d end3d = Util.getLookVecEio(player);
    end3d.scale(6);
    end3d.add(start.xCoord, start.yCoord, start.zCoord);

    //update to world pos for vis testing
    Point3i worldPos = new Point3i(tagPos);
    worldPos.add(te.xCoord, te.yCoord, te.zCoord);

    List<Hit> hits = raytraceAll(te.getWorldObj(), start, Vec3.createVectorHelper(end3d.x, end3d.y, end3d.z), false, true);
    if(hits != null) {
      for (Hit hit : hits) {
        if(hit != null && (hit.blockCoord.x == worldPos.x && hit.blockCoord.y == worldPos.y && hit.blockCoord.z == worldPos.z)) {
          return new TagLocs(tags, tagPos);
        } else if(hit != null && hit.isSolid) {
          //there is a block in the way
          break;
        }
      }
    }
    return null;

  }

  private void renderGroundLevel(TileComponentTool ct, double wldX, double wldY, double wldZ) {
    AxisAlignedBB bb = ct.getStructureBounds();

    double x = ct.getOffsetX();
    double y = ct.getOffsetY() + ct.getSurfaceOffset() + 1;
    double z = ct.getOffsetZ();
    double width = Math.abs(bb.maxX - bb.minX);
    double length = Math.abs(bb.maxZ - bb.minZ);
    int colorRGB = ColorUtil.getRGB(Color.GREEN);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    Tessellator tessellator = Tessellator.instance;

    tessellator.addTranslation((float) wldX, (float) wldY, (float) wldZ);

    tessellator.startDrawingQuads();
    tessellator.setColorOpaque_I(colorRGB);
    tessellator.addVertex(x, y, z);
    tessellator.addVertex(x + width, y, z);
    tessellator.addVertex(x + width, y, z + length);
    tessellator.addVertex(x, y, z + length);
    tessellator.draw();

    tessellator.addTranslation((float) -wldX, (float) -wldY, (float) -wldZ);

    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

  //Code adapted from World.func_147447_a (rayTraceBlocks) to return all collided blocks
  public static List<Hit> raytraceAll(World world, Vec3 startVec, Vec3 endVec, boolean includeLiquids, boolean includeAir) {

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
            result.add(new Hit(point,false));
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

  private static class Hit {
    final Point3i blockCoord = new Point3i();
    final boolean isSolid;

    public Hit(Point3i blockCoord, boolean isSolid) {
      if(blockCoord != null) {
        this.blockCoord.set(blockCoord.x, blockCoord.y, blockCoord.z);
      }
      this.isSolid = isSolid;
    }

  }

  private static class TagLocs {

    final Collection<String> tags;
    final Point3i pos;

    public TagLocs(Collection<String> tags, Point3i pos) {
      this.tags = tags;
      this.pos = pos;
    }

  }

}
