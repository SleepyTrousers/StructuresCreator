package crazypants.structures.creator.block.component.gui;

import java.util.ArrayList;
import java.util.List;

import com.sun.java.swing.plaf.motif.MotifDesktopIconUI.IconButton;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.EmptyContainer;
import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.creator.block.component.packet.PacketSetTaggedLocation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiTagEditor  extends GuiContainerBase {

  private TileComponentEditor te;
  private Point3i loc;
  
  private List<String> tags = new ArrayList<String>();
  
  private List<IconButton> buts = new ArrayList<IconButton>();
  
  public GuiTagEditor(EntityPlayer player, InventoryPlayer inventory, TileComponentEditor te, Point3i loc) {
    super(new EmptyContainer());
    this.te = te;
    this.loc = loc;  
    tags.addAll(te.getTagsAtLocation(loc));    
  }
  
  
  
  @Override
  public void initGui() {    
    super.initGui();
    addTextFields();
    if(!textFields.isEmpty()) {
      textFields.get(0).setFocused(true);
    }
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    List<String> res = new ArrayList<String>();
    for(TextFieldEnder tf : textFields) {
      String txt = tf.getText().trim();
      if(txt.length() > 0) {
        res.add(txt);
      }
    }
    te.setTagsAtPosition(loc, tags);    
    PacketHandler.INSTANCE.sendToServer(new PacketSetTaggedLocation(te, loc, tags));
  }
  
  @Override
  protected void actionPerformed(GuiButton button) {    
    if(button.id == 9999) {
      tags.add("");
    } else {
      int index = button.id;
      if(index >= 0 && index < tags.size()) {
        tags.remove(index);
      }
    }
    addTextFields();
  }

  @Override
  protected void onTextFieldChanged(TextFieldEnder changedField, String old) {        
    tags.clear();
    for(TextFieldEnder tf : textFields) {
      tags.add(tf.getText());
    }
  }

  private int addTextFields() {

    for(IconButton but : buts) {
      but.detach();
    }
    textFields.clear();
    
    int x = 10 + guiLeft;
    int y = 10 + guiTop;
    int tfWidth = 100;
    int index = 0;
    for(String str : tags) {
      TextFieldEnder tf = new TextFieldEnder(fontRendererObj, x, y, tfWidth, fontRendererObj.FONT_HEIGHT + 4);      
      tf.setText(str);
      
      textFields.add(tf);     
      IconButton but = new IconButton(this, index, x + tfWidth + 5 - guiLeft, y - guiTop - 1, EnderWidget.MINUS_BUT);
      but.onGuiInit();   
      buts.add(but);
      
      y+=fontRendererObj.FONT_HEIGHT + 8;
      index++;
    }
    //y+= 4;
    
    IconButton but = new IconButton(this, 9999, x + tfWidth + 5 - guiLeft, y - guiTop - 1, EnderWidget.ADD_BUT);
    but.onGuiInit();
    buts.add(but);
    
    return y + 4;
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
