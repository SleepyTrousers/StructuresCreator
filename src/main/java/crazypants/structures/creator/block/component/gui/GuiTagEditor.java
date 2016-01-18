package crazypants.structures.creator.block.component.gui;

import java.util.ArrayList;
import java.util.List;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.creator.endercore.GuiContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

//TODO: 1.8
public class GuiTagEditor  extends GuiContainerBase {

  private TileComponentEditor te;
  private Point3i loc;
  
  private List<String> tags = new ArrayList<String>();
  
//  private List<IconButton> buts = new ArrayList<IconButton>();
  
  public GuiTagEditor(EntityPlayer player, InventoryPlayer inventory, TileComponentEditor te, Point3i loc) {
    //super(new EmptyContainer());
    this.te = te;
    this.loc = loc;  
    tags.addAll(te.getTagsAtLocation(loc));    
  }
  
  
  
//  @Override
//  public void initGui() {    
//    super.initGui();
//    addTextFields();
//    if(!textFields.isEmpty()) {
//      textFields.get(0).setFocused(true);
//    }
//  }
//
//  @Override
//  public void onGuiClosed() {
//    super.onGuiClosed();
//    List<String> res = new ArrayList<String>();
//    for(TextFieldEnder tf : textFields) {
//      String txt = tf.getText().trim();
//      if(txt.length() > 0) {
//        res.add(txt);
//      }
//    }
//    te.setTagsAtPosition(loc, tags);    
//    PacketHandler.INSTANCE.sendToServer(new PacketSetTaggedLocation(te, loc, tags));
//  }
//  
//  @Override
//  protected void actionPerformed(GuiButton button) {    
//    if(button.id == 9999) {
//      tags.add("");
//    } else {
//      int index = button.id;
//      if(index >= 0 && index < tags.size()) {
//        tags.remove(index);
//      }
//    }
//    addTextFields();
//  }
//
//  @Override
//  protected void onTextFieldChanged(TextFieldEnder changedField, String old) {        
//    tags.clear();
//    for(TextFieldEnder tf : textFields) {
//      tags.add(tf.getText());
//    }
//  }
//
//  private int addTextFields() {
//
//    for(IconButton but : buts) {
//      but.detach();
//    }
//    textFields.clear();
//    
//    int x = 10 + guiLeft;
//    int y = 10 + guiTop;
//    int tfWidth = 100;
//    int index = 0;
//    for(String str : tags) {
//      TextFieldEnder tf = new TextFieldEnder(fontRendererObj, x, y, tfWidth, fontRendererObj.FONT_HEIGHT + 4);      
//      tf.setText(str);
//      
//      textFields.add(tf);     
//      IconButton but = new IconButton(this, index, x + tfWidth + 5 - guiLeft, y - guiTop - 1, EnderWidget.MINUS_BUT);
//      but.onGuiInit();   
//      buts.add(but);
//      
//      y+=fontRendererObj.FONT_HEIGHT + 8;
//      index++;
//    }
//    
//    IconButton but = new IconButton(this, 9999, x + tfWidth + 5 - guiLeft, y - guiTop - 1, EnderWidget.ADD_BUT);
//    but.onGuiInit();
//    buts.add(but);
//    
//    return y + 4;
//  }
  
}
