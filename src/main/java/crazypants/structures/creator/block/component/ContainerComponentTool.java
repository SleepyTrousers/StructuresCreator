package crazypants.structures.creator.block.component;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

public class ContainerComponentTool extends Container  {

  public ContainerComponentTool(InventoryPlayer inventory, TileEntity te) {
  }

  @Override
  public boolean canInteractWith(EntityPlayer p_75145_1_) {
    return true;
  }

}
