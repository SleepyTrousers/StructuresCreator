package crazypants.structures.creator;

import cpw.mods.fml.client.registry.ClientRegistry;
import crazypants.structures.creator.block.component.RendererComponentEditor;
import crazypants.structures.creator.block.component.TileComponentEditor;

public class ClientProxy extends CommonProxy {

  @Override
  public void load() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileComponentEditor.class, new RendererComponentEditor());
  }

}
