package crazypants.structures.creator.block.component;

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
import crazypants.structures.creator.endercore.BoundingBox;
import crazypants.structures.creator.endercore.RenderUtil;
import crazypants.structures.creator.endercore.Util;
import crazypants.structures.creator.endercore.Vector3d;
import crazypants.structures.creator.endercore.Vector3f;
import crazypants.structures.creator.item.ItemTagTool;
import crazypants.structures.creator.item.ItemTagTool.Hit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;

public class RendererComponentEditor extends TileEntitySpecialRenderer<TileComponentEditor> {

  @Override
  public void renderTileEntityAt(TileComponentEditor te, double x, double y, double z, float partialTicks, int destroyStage) {

    Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + te.getOffsetX(), y + te.getOffsetY(), z + te.getOffsetZ());

    WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();

    // Main bounds
    BoundingBox bb = new BoundingBox(te.getStructureBounds());
    bb = bb.translate(-bb.minX, -bb.minY, -bb.minZ);
    GlStateManager.color(1, 1, 0, 1);
        
    RenderUtil.renderBoundingBox(bb, EnderStructuresCreator.blockComponentTool.getDefaultState());

    double width = (bb.maxX - bb.minX);
    double height = bb.maxY - bb.minY;
    double depth = (bb.maxZ - bb.minZ);

    GL11.glDisable(GL11.GL_TEXTURE_2D);
    
    // Ground Level
    GlStateManager.color(0, 1, 0, 1);
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
    double yGrn = te.getSurfaceOffset() + 1;
    tes.pos(0, yGrn, 0).endVertex();
    tes.pos(width, yGrn, 0).endVertex();
    tes.pos(width, yGrn, depth).endVertex();
    tes.pos(0, yGrn, depth).endVertex();
    Tessellator.getInstance().draw();

    // North Level
    GlStateManager.color(1, 0, 0, 1);
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
    double in = 0.2;
    tes.pos(in, in, 0).endVertex();
    tes.pos(width - in, in, 0).endVertex();
    tes.pos(width - in, height - in, 0).endVertex();
    tes.pos(in, height - in, 0).endVertex();
    Tessellator.getInstance().draw();

    GlStateManager.popMatrix();
    
    // TAGS
    renderTags(te, x + te.getOffsetX(), y + te.getOffsetY(), z + te.getOffsetZ());    

    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

  }

  private void renderTags(TileComponentEditor ct, double wldX, double wldY, double wldZ) {

    GlStateManager.pushMatrix();
    GlStateManager.translate(wldX, wldY,wldZ);
    
    List<TagsAtLoc> taggedLocations = new ArrayList<TagsAtLoc>();
    Multimap<Point3i, String> tags = ct.getTagsAtLocations();
    for (Entry<Point3i, Collection<String>> e : tags.asMap().entrySet()) {
      Point3i bc = VecUtil.transformLocationToWorld(0, 0, 0, Rotation.DEG_0, ct.getSize(), e.getKey());
      TagsAtLoc tag = renderTag(ct, bc, e.getValue());
      if (tag != null) {
        taggedLocations.add(tag);
      }
    }
    GlStateManager.popMatrix();

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    for (TagsAtLoc tagsAtLoc : taggedLocations) {
      String tag = "";
      for (String txt : tagsAtLoc.tags) {
        tag += txt + ", ";
      }
      tag = tag.substring(0, tag.length() - 2);
      RenderUtil.drawBillboardedText(new Vector3f(wldX + tagsAtLoc.pos.x + 0.5, wldY + tagsAtLoc.pos.y + 0.65, wldZ + tagsAtLoc.pos.z + 0.5), tag, 0.25f);
    }
    GL11.glEnable(GL11.GL_DEPTH_TEST);

  }

  private TagsAtLoc renderTag(TileComponentEditor te, Point3i tagOffset, Collection<String> tags) {

    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(1.02, 1.02, 1.02);
    bb = bb.translate(tagOffset.x, tagOffset.y, tagOffset.z);
    GlStateManager.color(1, 1, 1, 1);

    RenderUtil.renderBoundingBox(bb);

    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    Vec3 start = Util.getEyePosition(player);
    
    Vector3d end3d = Util.getLookVecEio(player);
    end3d.scale(6);
    end3d.add(start.xCoord, start.yCoord, start.zCoord);

    // update to world pos for vis testing
    Point3i worldPos = new Point3i(tagOffset);
    worldPos.add(te.getPos().getX() + te.getOffsetX(), te.getPos().getY() + te.getOffsetY(), te.getPos().getZ() + te.getOffsetZ());

    List<Hit> hits = ItemTagTool.getBlocksInPath(te.getWorld(), start, new Vec3(end3d.x, end3d.y, end3d.z), false, false, true, true);    
    if (hits != null) {
      for (Hit hit : hits) {
        if (hit != null && (hit.blockCoord.x == worldPos.x && hit.blockCoord.y == worldPos.y && hit.blockCoord.z == worldPos.z)) {
          return new TagsAtLoc(tags, tagOffset);
        } else if (hit != null && hit.isSolid) {
          // there is a block in the way
          break;
        }
      }
    }
    return null;

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
