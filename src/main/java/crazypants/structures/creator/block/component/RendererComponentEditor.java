package crazypants.structures.creator.block.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Multimap;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.item.ItemTagTool;
import crazypants.structures.creator.item.ItemTagTool.Hit;
import javafx.geometry.BoundingBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class RendererComponentEditor extends TileEntitySpecialRenderer<TileComponentEditor> {

  @Override
  public void renderTileEntityAt(TileComponentEditor ct, double x, double y, double z, float partialTicks, int destroyStage) {

//    RenderUtil.bindBlockTexture();
//
//    BoundingBox bb = new BoundingBox(ct.getStructureBounds());
//    bb = bb.translate(-te.xCoord, -te.yCoord, -te.zCoord);
//
//    GL11.glDisable(GL11.GL_CULL_FACE);
//    GL11.glDisable(GL11.GL_LIGHTING);
//    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
//
//    Tessellator.instance.startDrawingQuads();
//    Tessellator.instance.addTranslation((float) x, (float) y, (float) z);
//
//    CubeRenderer.render(bb, EnderStructuresCreator.blockComponentTool.getIcon(0, 0));
//
//    Tessellator.instance.addTranslation((float) -x, (float) -y, (float) -z);
//    Tessellator.instance.draw();
//
//    renderGroundLevel(ct, x, y, z);
//    
//    renderNorthMarker(ct, x, y, z);
//
//    renderTags(ct, x, y, z);
//
//    GL11.glEnable(GL11.GL_CULL_FACE);
//    GL11.glEnable(GL11.GL_LIGHTING);

  }

  private void renderTags(TileComponentEditor ct, double wldX, double wldY, double wldZ) {
//    Tessellator.instance.startDrawingQuads();
//    Tessellator.instance.addTranslation((float) wldX, (float) wldY, (float) wldZ);
//
//    List<TagsAtLoc> taggedLocations = new ArrayList<TagsAtLoc>();
//    Multimap<Point3i, String> tags = ct.getTagsAtLocations();
//    for (Entry<Point3i, Collection<String>> e : tags.asMap().entrySet()) {
//      Point3i bc = VecUtil.transformLocationToWorld(0, 0, 0, Rotation.DEG_0, ct.getSize(), e.getKey());
//      TagsAtLoc tag = renderTag(ct, bc, e.getValue());
//      if(tag != null) {
//        taggedLocations.add(tag);
//      }
//    }
//
//    Tessellator.instance.addTranslation(-(float) wldX, -(float) wldY, -(float) wldZ);
//    Tessellator.instance.draw();
//
//    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//    GL11.glDisable(GL11.GL_DEPTH_TEST);
//    for (TagsAtLoc tagsAtLoc : taggedLocations) {
//      String tag = "";
//      for(String txt : tagsAtLoc.tags) {
//         tag += txt + ", ";
//      }
//      tag = tag.substring(0, tag.length() - 2);
//      RenderUtil.drawBillboardedText(new Vector3f(wldX + tagsAtLoc.pos.x + 0.5, wldY + tagsAtLoc.pos.y + 0.5, wldZ + tagsAtLoc.pos.z + 0.5), tag, 0.25f);
//    }
//    GL11.glEnable(GL11.GL_DEPTH_TEST);

  }

  private TagsAtLoc renderTag(TileComponentEditor te, Point3i tagOffset, Collection<String> tags) {

//    //Local pos
//    Point3i tagPos = new Point3i(te.getOffsetX() + tagOffset.x, te.getOffsetY() + tagOffset.y, te.getOffsetZ() + tagOffset.z);
//    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(1.02, 1.02, 1.02);
//    bb = bb.translate(tagPos.x, tagPos.y, tagPos.z);
//    CubeRenderer.render(bb, EnderStructuresCreator.blockComponentTool.getIcon(0, 0));
//
//    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
//    Vec3 start = Util.getEyePosition(player);
//    Vector3d end3d = Util.getLookVecEio(player);
//    end3d.scale(6);
//    end3d.add(start.xCoord, start.yCoord, start.zCoord);
//
//    //update to world pos for vis testing
//    Point3i worldPos = new Point3i(tagPos);
//    worldPos.add(te.xCoord, te.yCoord, te.zCoord);
//
//    List<Hit> hits = ItemTagTool.getBlocksInPath(te.getWorldObj(), start, Vec3.createVectorHelper(end3d.x, end3d.y, end3d.z), false, true);
//    if(hits != null) {
//      for (Hit hit : hits) {
//        if(hit != null && (hit.blockCoord.x == worldPos.x && hit.blockCoord.y == worldPos.y && hit.blockCoord.z == worldPos.z)) {
//          return new TagsAtLoc(tags, tagPos);
//        } else if(hit != null && hit.isSolid) {
//          //there is a block in the way
//          break;
//        }
//      }
//    }
    return null;

  }

  private void renderGroundLevel(TileComponentEditor ct, double wldX, double wldY, double wldZ) {
//    AxisAlignedBB bb = ct.getStructureBounds();
//
//    double x = ct.getOffsetX();
//    double y = ct.getOffsetY() + ct.getSurfaceOffset() + 1;
//    double z = ct.getOffsetZ();
//    double width = Math.abs(bb.maxX - bb.minX);
//    double length = Math.abs(bb.maxZ - bb.minZ);
//    int colorRGB = ColorUtil.getRGB(Color.GREEN);
//    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//    GL11.glDisable(GL11.GL_TEXTURE_2D);
//    Tessellator tessellator = Tessellator.instance;
//
//    tessellator.addTranslation((float) wldX, (float) wldY, (float) wldZ);
//
//    tessellator.startDrawingQuads();
//    tessellator.setColorOpaque_I(colorRGB);
//    tessellator.addVertex(x, y, z);
//    tessellator.addVertex(x + width, y, z);
//    tessellator.addVertex(x + width, y, z + length);
//    tessellator.addVertex(x, y, z + length);
//    tessellator.draw();
//
//    tessellator.addTranslation((float) -wldX, (float) -wldY, (float) -wldZ);
//
//    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  
  private void renderNorthMarker(TileComponentEditor ct, double wldX, double wldY, double wldZ) {
//    AxisAlignedBB bb = ct.getStructureBounds();
//
//    double x = ct.getOffsetX();
//    double y = ct.getOffsetY();
//    double z = ct.getOffsetZ();
//    double width = Math.abs(bb.maxX - bb.minX);
//    double height = Math.abs(bb.maxY - bb.minY);
//    int colorRGB = ColorUtil.getRGB(Color.RED);
//    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//    GL11.glDisable(GL11.GL_TEXTURE_2D);
//    Tessellator tessellator = Tessellator.instance;
//
//    tessellator.addTranslation((float) wldX, (float) wldY, (float) wldZ);
//
//    double i = 0.25;
//    tessellator.startDrawingQuads();
//    tessellator.setColorOpaque_I(colorRGB);
//    tessellator.addVertex(x + i, y + i, z);
//    tessellator.addVertex(x + width - i, y + i, z);
//    tessellator.addVertex(x + width - i, y+ height - i, z);
//    tessellator.addVertex(x + i, y+ height - i, z);
//    tessellator.draw();
//
//    tessellator.addTranslation((float) -wldX, (float) -wldY, (float) -wldZ);
//
//    GL11.glEnable(GL11.GL_TEXTURE_2D);    
  }
  

  private static class TagsAtLoc {

    final Collection<String> tags;
    final Point3i pos;

    public TagsAtLoc(Collection<String> tags, Point3i pos) {
      this.tags = tags;
      this.pos = pos;
    }

  }


 

}
