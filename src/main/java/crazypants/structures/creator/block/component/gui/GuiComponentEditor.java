package crazypants.structures.creator.block.component.gui;

import com.enderio.core.client.gui.GuiContainerBase;

import crazypants.structures.creator.block.EmptyContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class GuiComponentEditor extends GuiContainerBase {

  public GuiComponentEditor(EntityPlayer player, InventoryPlayer inventory, TileEntity te) {
    super(new EmptyContainer());    
  }

}
