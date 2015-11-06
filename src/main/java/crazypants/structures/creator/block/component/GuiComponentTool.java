package crazypants.structures.creator.block.component;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.gui.widget.TextFieldEnder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class GuiComponentTool extends GuiContainerBase {

  private TextFieldEnder nameTF;
  private String nameLabel = "Name:";
  
  private TextFieldEnder boundsTF;
  private String boundsLabel = "Bounds(w h l):";
  
  private final int MARGIN = 5;
  
  public GuiComponentTool(EntityPlayer player, InventoryPlayer inventory, TileEntity te) {
    super(new EmptyContainer());
    
//    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
//    int x = MARGIN + fr.getStringWidth(nameLabel) + 6;
//    int y = 10;
//    int width = 100;    
//    
//    
//    nameTF = new TextFieldEnder(fr, x, y, width, fr.FONT_HEIGHT + 3);
//    textFields.add(nameTF);
//    
//    x = MARGIN + fr.getStringWidth(boundsLabel) + 6;
//    y += fr.FONT_HEIGHT + 2;    
//    boundsTF= new TextFieldEnder(fr,x, y, 60, fr.FONT_HEIGHT + 3);
//    textFields.add(boundsTF);
    
  }
  
  
  
  @Override
  public void initGui() { 
    super.initGui();
    
//    int x = 10;
//    int y = 10;
//    int width = 100;    
//    
//    nameTF = new TextFieldEnder(fontRendererObj, guiLeft + x, guiTop + y, width, fontRendererObj.FONT_HEIGHT);
//    textFields.add(e)
    //nameTF.setMaxStringLength(15);
//    this.searchField.setEnableBackgroundDrawing(false);
//    this.searchField.setVisible(false);
    //nameTF.setTextColor(16777215);
  }



  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
//    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//    RenderUtil.bindTexture("enderstructurescreator:textures/gui/componentTool.png");
//    int sx = (width - xSize) / 2;
//    int sy = (height - ySize) / 2;

    //drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    
    
    
//    int left = guiLeft + MARGIN;
//    int x =  left;// + fontRendererObj.getStringWidth(txt) + SPACING + startTF.getWidth() + 12;
//    
//    int y = guiTop + 10;
//    
//    fontRendererObj.drawString(nameLabel, x, y, ColorUtil.getRGB(Color.black), false);
//    
//    y += fontRendererObj.FONT_HEIGHT + 2;   
//    fontRendererObj.drawString(boundsLabel, x, y, ColorUtil.getRGB(Color.black), false);
//    
//    
////    nameTF.drawTextBox();
//    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  
 

}
