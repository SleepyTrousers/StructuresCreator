package crazypants.structures.creator.endercore;

import crazypants.structures.creator.block.EmptyContainer;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiContainerBase extends GuiContainer {

  public GuiContainerBase() {
    super(new EmptyContainer());
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
  }
}
