package crazypants.structures.creator;

import static crazypants.structures.creator.EnderStructuresCreator.MODID;
import static crazypants.structures.creator.EnderStructuresCreator.MOD_NAME;
import static crazypants.structures.creator.EnderStructuresCreator.VERSION;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import crazypants.structures.creator.block.BlockClearMarker;
import crazypants.structures.creator.block.BlockComponentTool;
import crazypants.structures.creator.block.BlockTemplateEditor;
import crazypants.structures.creator.block.component.ToolRegister;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.creator.item.ItemClearTool;
import crazypants.structures.creator.item.ItemComponentTool;
import crazypants.structures.creator.item.ItemDebugTool;
import crazypants.structures.creator.item.ItemTagTool;
import crazypants.structures.creator.item.ItemTemplateTool;

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
  
  public static BlockComponentTool blockComponentTool;
  public static BlockTemplateEditor blockTemplateEditor;
  
  public static ItemComponentTool itemComponentTool;
  public static ItemTemplateTool itemTemplateTool;
  public static ItemClearTool itemClearTool;
  public static ItemDebugTool itemDebugTool;
  
  public static ItemTagTool itemTagTool; 
  public static GuiHandler guiHandler = new GuiHandler();

  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    //Config.load(event);   
    
    blockComponentTool = BlockComponentTool.create();    
    blockTemplateEditor = BlockTemplateEditor.create();   
    blockClearMarker = BlockClearMarker.create();
    
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
    ExportManager.instance.loadExportFolder();
  }
  
  @EventHandler
  public void serverStarted(FMLServerStartedEvent event) {
    ToolRegister.reset();
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    ToolRegister.reset();
  }
  
  private void addRecipes() {  
  }

  
}
