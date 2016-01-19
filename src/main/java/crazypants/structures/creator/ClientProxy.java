package crazypants.structures.creator;

import crazypants.structures.creator.block.BlockClearMarker;
import crazypants.structures.creator.block.BlockComponentEditor;
import crazypants.structures.creator.block.BlockGeneratorEditor;
import crazypants.structures.creator.block.BlockLootCategoryEditor;
import crazypants.structures.creator.block.BlockTemplateEditor;
import crazypants.structures.creator.block.BlockVillagerEditor;
import crazypants.structures.creator.block.component.RendererComponentEditor;
import crazypants.structures.creator.block.component.TileComponentEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

  @Override
  public void load() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileComponentEditor.class, new RendererComponentEditor());
    regRenderer(Item.getItemFromBlock(EnderStructuresCreator.blockComponentTool), BlockComponentEditor.NAME);
    regRenderer(Item.getItemFromBlock(EnderStructuresCreator.blockTemplateEditor), BlockTemplateEditor.NAME);
    regRenderer(Item.getItemFromBlock(EnderStructuresCreator.blockGeneratorEditor), BlockGeneratorEditor.NAME);
    regRenderer(Item.getItemFromBlock(EnderStructuresCreator.blockVillagerEditor), BlockVillagerEditor.NAME);
    regRenderer(Item.getItemFromBlock(EnderStructuresCreator.blockLootCategoryEditor), BlockLootCategoryEditor.NAME);
    regRenderer(Item.getItemFromBlock(EnderStructuresCreator.blockClearMarker), BlockClearMarker.NAME);
  }

  private void regRenderer(Item item, String name) {
    regRenderer(item, 0, name);
  }
  
  private void regRenderer(Item item, int meta, String name) {
    regRenderer(item, meta, EnderStructuresCreator.MODID, name);
  }

  private void regRenderer(Item item, int meta, String modId, String name) {
    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    // ItemMeshDefinition d;
    String resourceName;
    if (modId != null) {
      resourceName = modId + ":" + name;
    } else {
      resourceName = name;
    }
    renderItem.getItemModelMesher().register(item, meta, new ModelResourceLocation(resourceName, "inventory"));
  }

  
  
}
