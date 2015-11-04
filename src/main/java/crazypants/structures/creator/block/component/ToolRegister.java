package crazypants.structures.creator.block.component;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ToolRegister {

  private static ToolRegister serverReg = new ToolRegister(false);
  private static ToolRegister clientReg = new ToolRegister(true);
  
  public static void onLoad(TileComponentTool tile) {
    if(tile == null) {
      return;
    }
    getTooleRegister(tile.getWorldObj()).addTile(tile);
  }

  public static void onUnload(TileComponentTool tile) {
    if(tile == null) {
      return;
    }
    getTooleRegister(tile.getWorldObj()).removeTile(tile);
  }
  
  public static void reset() {
    System.out.println("ToolRegister.reset: ");
    serverReg.tiles.clear();
    clientReg.tiles.clear();    
  }
  
  public static ToolRegister getTooleRegister(World wld) {
    if(wld.isRemote) {
      return clientReg;
    }
    return serverReg;
  }

  private Set<TileComponentTool> tiles = new HashSet<TileComponentTool>();

  public ToolRegister(boolean isClient) {
    if(isClient) {
      FMLCommonHandler.instance().bus().register(this);
//      MinecraftForge.EVENT_BUS.register(this);
    }
  }

  public void addTile(TileComponentTool tile) {    
    tiles.add(tile);
  }

  public void removeTile(TileComponentTool tile) {
    tiles.remove(tile);
  }
  
  public TileComponentTool getClosestTileInBounds(World world, int x, int y, int z) {   
    TileComponentTool res = null;
    for(TileComponentTool tile: tiles) {
      if(tile.hasWorldObj() && world.provider.dimensionId == tile.getWorldObj().provider.dimensionId) {                
        if(tile.getStructureBounds().isVecInside(Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5))) {               
          return tile;
        }
      }
    }    
    return res;
  }
  
  @SubscribeEvent
  public void onDisconnectedFromServer(ClientDisconnectionFromServerEvent evt) { 
    System.out.println("ToolRegister.onDisconnectedFromServer: ");
    reset();
  }

}
