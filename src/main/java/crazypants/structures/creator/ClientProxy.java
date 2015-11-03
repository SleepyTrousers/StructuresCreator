package crazypants.structures.creator;

import cpw.mods.fml.client.registry.ClientRegistry;
import crazypants.structures.creator.block.component.ComponentToolRenderer;
import crazypants.structures.creator.block.component.TileComponentTool;

public class ClientProxy extends CommonProxy {

  @Override
  public void load() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileComponentTool.class, new ComponentToolRenderer());
  }

}
