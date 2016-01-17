package crazypants.structures.creator;

import static crazypants.structures.creator.EnderStructuresCreator.MODID;
import static crazypants.structures.creator.EnderStructuresCreator.MOD_NAME;
import static crazypants.structures.creator.EnderStructuresCreator.VERSION;

import crazypants.structures.creator.block.BlockClearMarker;
import crazypants.structures.creator.block.BlockComponentEditor;
import crazypants.structures.creator.block.BlockGeneratorEditor;
import crazypants.structures.creator.block.BlockLootCategoryEditor;
import crazypants.structures.creator.block.BlockTemplateEditor;
import crazypants.structures.creator.block.BlockVillagerEditor;
import crazypants.structures.creator.block.component.EditorRegister;
import crazypants.structures.creator.item.ItemClearTool;
import crazypants.structures.creator.item.ItemComponentTool;
import crazypants.structures.creator.item.ItemDebugTool;
import crazypants.structures.creator.item.ItemTagTool;
import crazypants.structures.creator.item.ItemTemplateTool;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:Forge@10.13.0.1150,);required-after:EnderStructures")
public class EnderStructuresCreator {
  
  public static final String MODID = "EnderStructuresCreator";
  public static final String MOD_NAME = "Ender Structures Creator";
  public static final String VERSION = "@VERSION@";
  
  @Instance(MODID)
  public static EnderStructuresCreator instance;

  @SidedProxy(clientSide = "crazypants.structures.creator.ClientProxy", serverSide = "crazypants.structures.creator.CommonProxy")
  public static CommonProxy proxy;
    
  public static BlockClearMarker blockClearMarker;  
  
  public static BlockComponentEditor blockComponentTool;
  public static BlockTemplateEditor blockTemplateEditor;
  public static BlockGeneratorEditor blockGeneratorEditor;
  public static BlockVillagerEditor blockVillagerEditor;
  public static BlockLootCategoryEditor blockLootCategoryEditor;
  
  public static ItemComponentTool itemComponentTool;
  public static ItemTemplateTool itemTemplateTool;
  public static ItemClearTool itemClearTool;
  public static ItemDebugTool itemDebugTool;
  
  public static ItemTagTool itemTagTool; 
  public static GuiHandler guiHandler = new GuiHandler();

  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    //Config.load(event);   
    
    blockComponentTool = BlockComponentEditor.create();    
    blockTemplateEditor = BlockTemplateEditor.create();   
    blockGeneratorEditor = BlockGeneratorEditor.create();
    blockVillagerEditor = BlockVillagerEditor.create(); 
    blockClearMarker = BlockClearMarker.create();
    blockLootCategoryEditor = BlockLootCategoryEditor.create();
    
    itemTagTool = ItemTagTool.create();
    itemComponentTool = ItemComponentTool.create();
    itemTemplateTool = ItemTemplateTool.create();
    itemClearTool = ItemClearTool.create();
    itemDebugTool = ItemDebugTool.create();
    
  }
  
  @EventHandler
  public void load(FMLInitializationEvent event) {
    instance = this;        
    NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);    
    proxy.load();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {  
    addRecipes();        
  }
  
  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    EditorRegister.reset();
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    EditorRegister.reset();
  }
  
  private void addRecipes() {  
  }

  
}
