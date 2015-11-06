package crazypants.structures.creator.block.component.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class EmptyContainer extends Container  {

  public EmptyContainer() {
  }

  @Override
  public boolean canInteractWith(EntityPlayer p_75145_1_) {
    return true;
  }

}
