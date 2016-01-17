package crazypants.structures.creator;

import crazypants.structures.creator.block.component.RendererComponentEditor;
import crazypants.structures.creator.block.component.TileComponentEditor;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

  @Override
  public void load() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileComponentEditor.class, new RendererComponentEditor());
  }

}
