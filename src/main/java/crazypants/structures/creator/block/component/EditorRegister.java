package crazypants.structures.creator.block.component;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class EditorRegister {

  private static EditorRegister serverReg = new EditorRegister(false);
  private static EditorRegister clientReg = new EditorRegister(true);
  
  public static void onLoad(TileComponentEditor tile) {
    if(tile == null) {
      return;
    }
    getTooleRegister(tile.getWorld()).addTile(tile);
  }

  public static void onUnload(TileComponentEditor tile) {
    if(tile == null) {
      return;
    }
    getTooleRegister(tile.getWorld()).removeTile(tile);
  }
  
  public static void reset() {
    System.out.println("ToolRegister.reset: ");
    serverReg.tiles.clear();
    clientReg.tiles.clear();    
  }
  
  public static EditorRegister getTooleRegister(World wld) {
    if(wld.isRemote) {
      return clientReg;
    }
    return serverReg;
  }

  private Set<TileComponentEditor> tiles = new HashSet<TileComponentEditor>();

  public EditorRegister(boolean isClient) {
    if(isClient) {      
      MinecraftForge.EVENT_BUS.register(this);
    }
  }

  public void addTile(TileComponentEditor tile) {    
    tiles.add(tile);
  }

  public void removeTile(TileComponentEditor tile) {
    tiles.remove(tile);
  }
  
  public TileComponentEditor getClosestTileInBounds(World world, int x, int y, int z) {   
    TileComponentEditor res = null;
    for(TileComponentEditor tile: tiles) {
      if(tile.hasWorldObj() && world.provider.getDimensionId() == tile.getWorld().provider.getDimensionId()) {                
        if(tile.getStructureBounds().isVecInside(new Vec3(x + 0.5, y + 0.5, z + 0.5))) {               
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
