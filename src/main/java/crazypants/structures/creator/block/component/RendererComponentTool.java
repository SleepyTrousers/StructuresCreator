package crazypants.structures.creator.block.component;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.creator.EnderStructuresCreator;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class RendererComponentTool extends TileEntitySpecialRenderer {

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        
    TileComponentTool ct = (TileComponentTool)te;
//    System.out.println("RendererComponentTool.renderTileEntityAt: " + System.identityHashCode(ct));    
    
    RenderUtil.bindBlockTexture();
    
    BoundingBox bb = new BoundingBox(ct.getStructureBounds());
    bb = bb.translate(-te.xCoord, -te.yCoord, -te.zCoord);
    
    //draw one bigger
    //bb = new BoundingBox(bb.getMin(), bb.getMax().add(1,0,1));
    
    
    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    
    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.addTranslation((float)x, (float)y, (float)z);    
    
    CubeRenderer.render(bb, EnderStructuresCreator.blockComponentTool.getIcon(0, 0));
 
    Tessellator.instance.addTranslation((float)-x, (float)-y, (float)-z);
    Tessellator.instance.draw();
    
    renderGroundLevel(ct, x, y, z);
    
    renderTags(ct, x, y, z);
    
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    
    
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glEnable(GL11.GL_LIGHTING);
    
  }
  
  private void renderTags(TileComponentTool ct, double wldX, double wldY, double wldZ) {
    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.addTranslation((float)wldX, (float)wldY, (float)wldZ);    
    
    Map<String, List<Point3i>> tl = ct.getTaggedLocations();
    for (Entry<String, List<Point3i>> e : tl.entrySet()) {
      if(e.getValue() != null) {
        for(Point3i loc : e.getValue()) {
           Point3i bc = VecUtil.transformStructureCoodToWorld(0, 0, 0, Rotation.DEG_0, ct.getSize(), loc);
           renderTag(ct, e.getKey(), bc);
        }
      }
    }
    
    Tessellator.instance.addTranslation(-(float)wldX, -(float)wldY, -(float)wldZ);
    Tessellator.instance.draw();
    
  }

  private void renderTag(TileComponentTool te, String key, Point3i bc) {
    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(1.02, 1.02, 1.02);
    bb = bb.translate(te.getOffsetX() + bc.x, te.getOffsetY() + bc.y, te.getOffsetZ() + bc.z);        
    CubeRenderer.render(bb, EnderStructuresCreator.blockComponentTool.getIcon(0, 0));
    
    
    
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
    
    tessellator.addTranslation((float)wldX, (float)wldY, (float)wldZ);
    
    tessellator.startDrawingQuads();
    tessellator.setColorOpaque_I(colorRGB);
    tessellator.addVertex(x, y, z);
    tessellator.addVertex(x + width, y, z);
    tessellator.addVertex(x + width, y, z + length);
    tessellator.addVertex(x, y, z + length);
    tessellator.draw();
    
    tessellator.addTranslation((float)-wldX, (float)-wldY, (float)-wldZ);
    
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

}
