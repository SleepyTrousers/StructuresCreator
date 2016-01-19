package crazypants.structures.creator.endercore;

import static org.lwjgl.opengl.GL11.GL_ALL_ATTRIB_BITS;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glShadeModel;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderUtil {

  public static final Vector4f DEFAULT_TEXT_SHADOW_COL = new Vector4f(0.33f, 0.33f, 0.33f, 0.33f);

  public static final Vector4f DEFAULT_TXT_COL = new Vector4f(1, 1, 1, 1);

  public static final Vector4f DEFAULT_TEXT_BG_COL = new Vector4f(0.275f, 0.08f, 0.4f, 0.75f);
  
  public static final Vector3d UP_V = new Vector3d(0, 1, 0);

  public static final Vector3d ZERO_V = new Vector3d(0, 0, 0);

  public static final ResourceLocation BLOCK_TEX = TextureMap.locationBlocksTexture;

  public static final ResourceLocation GLINT_TEX = new ResourceLocation("textures/misc/enchanted_item_glint.png");

  public static int BRIGHTNESS_MAX = 15 << 20 | 15 << 4;
  
  public static TextureManager engine() {
    return Minecraft.getMinecraft().renderEngine;
  }
  
  public static void bindBlockTexture() {
    engine().bindTexture(BLOCK_TEX);
  }

  public static void bindGlintTexture() {
    engine().bindTexture(BLOCK_TEX);
  }

  public static void bindTexture(String string) {
    engine().bindTexture(new ResourceLocation(string));
  }

  public static void bindTexture(ResourceLocation tex) {
    engine().bindTexture(tex);
  }

  
  public static void drawBillboardedText(Vector3f pos, String text, float size) {
    drawBillboardedText(pos, text, size, DEFAULT_TXT_COL, true, DEFAULT_TEXT_SHADOW_COL, true, DEFAULT_TEXT_BG_COL);
  }

  public static void drawBillboardedText(Vector3f pos, String text, float size, Vector4f bgCol) {
    drawBillboardedText(pos, text, size, DEFAULT_TXT_COL, true, DEFAULT_TEXT_SHADOW_COL, true, bgCol);
  }

  public static void drawBillboardedText(Vector3f pos, String text, float size, Vector4f txtCol, boolean drawShadow, Vector4f shadowCol,
      boolean drawBackground, Vector4f bgCol) {
    
    
    GL11.glPushMatrix();
    GL11.glTranslatef(pos.x, pos.y, pos.z);
    GL11.glRotatef(180, 1, 0, 0);

    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fnt = mc.fontRendererObj;
    float scale = size / fnt.FONT_HEIGHT;
    GL11.glScalef(scale, scale, scale);
    GL11.glRotatef(mc.getRenderManager().playerViewY + 180, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(-mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

    GL11.glTranslatef(-fnt.getStringWidth(text) / 2, 0, 0);
    if (drawBackground) {
      renderBackground(fnt, text, bgCol);
    }
    fnt.drawString(text, 0, 0, ColorUtil.getRGBA(txtCol));
    if (drawShadow) {
      GL11.glTranslatef(0.5f, 0.5f, 0.1f);
      fnt.drawString(text, 0, 0, ColorUtil.getRGBA(shadowCol));
    }
    GL11.glPopMatrix();

    RenderUtil.bindBlockTexture();
  }

  public static void renderBackground(FontRenderer fnt, String toRender, Vector4f color) {
    glPushAttrib(GL_ALL_ATTRIB_BITS);
    glDisable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glShadeModel(GL_SMOOTH);
    glDisable(GL_ALPHA_TEST);
    glDisable(GL_CULL_FACE);
    glDepthMask(false);
    RenderHelper.disableStandardItemLighting();
    OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO); // stop random disappearing

    float width = fnt.getStringWidth(toRender);
    float height = fnt.FONT_HEIGHT;
    float padding = 2f;
    
//    Tessellator tessellator = Tessellator.instance;
//    tessellator.startDrawingQuads();
//    tessellator.setColorRGBA_F(color.x, color.y, color.z, color.w);
//    tessellator.addVertex(-padding, -padding, 0);
//    tessellator.addVertex(-padding, height + padding, 0);
//    tessellator.addVertex(width + padding, height + padding, 0);
//    tessellator.addVertex(width + padding, -padding, 0);
//    tessellator.draw();

    GlStateManager.color(color.x, color.y, color.z, color.w);
    
    WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

    tes.pos(-padding, -padding, 0).endVertex();
    tes.pos(-padding, height + padding, 0).endVertex();
    tes.pos(width + padding, height + padding, 0).endVertex();
    tes.pos(width + padding, -padding, 0).endVertex();
    
    Tessellator.getInstance().draw();
    
    glPopAttrib();
  }
  
  public static void renderBoundingBox(BoundingBox bb) {

    double x = bb.minX;
    double y = bb.minY;
    double z = bb.minZ;
    double width = (bb.maxX - bb.minX);
    double height = bb.maxY - bb.minY;
    double depth = (bb.maxZ - bb.minZ);

    WorldRenderer tes = Tessellator.getInstance().getWorldRenderer();
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

    tes.pos(x, y, z).endVertex();
    tes.pos(x + width, y, z).endVertex();
    tes.pos(x + width, y + height, z).endVertex();
    tes.pos(x, y + height, z).endVertex();

    tes.pos(x, y, z + depth).endVertex();
    tes.pos(x + width, y, z + depth).endVertex();
    tes.pos(x + width, y + height, z + depth).endVertex();
    tes.pos(x, y + height, z + depth).endVertex();

    tes.pos(x, y, z).endVertex();
    tes.pos(x, y, z + depth).endVertex();
    tes.pos(x, y + height, z + depth).endVertex();
    tes.pos(x, y + height, z).endVertex();

    tes.pos(x + width, y, z).endVertex();
    tes.pos(x + width, y, z + depth).endVertex();
    tes.pos(x + width, y + height, z + depth).endVertex();
    tes.pos(x + width, y + height, z).endVertex();

    Tessellator.getInstance().draw();
  }

  
}
