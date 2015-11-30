package crazypants.structures.creator.block.template.gui;

import com.enderio.core.client.gui.GuiContainerBase;

import crazypants.structures.creator.block.component.gui.EmptyContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class GuiTemplateEditor extends GuiContainerBase {

  public GuiTemplateEditor(EntityPlayer player, InventoryPlayer inventory, TileEntity te) {
    super(new EmptyContainer());    
  }
  
  @Override
  public void initGui() { 
    super.initGui();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
