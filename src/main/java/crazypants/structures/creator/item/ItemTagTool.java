package crazypants.structures.creator.item;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.component.PacketTaggedLocation;
import crazypants.structures.creator.block.component.TileComponentTool;
import crazypants.structures.creator.block.component.ToolRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTagTool  extends Item {

  private static final String NAME = "itemTagTool";

  public static ItemTagTool create() {
    ItemTagTool res = new ItemTagTool();
    res.init();
    return res;
  }

  private ItemTagTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setTextureName(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    if (world.isRemote) {
      return true;
    }
    return true;
  }

  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    
    
    TileComponentTool tile = ToolRegister.getTooleRegister(world).getClosestTileInBounds(world, x, y, z);
    if(tile == null) {
      return true;
    }
    
    int localX = x - tile.xCoord - tile.getOffsetX();
    int localY = y - tile.yCoord - tile.getOffsetY();
    int localZ = z - tile.zCoord - tile.getOffsetZ();
        
    Point3i offset = new Point3i(localX,localY,localZ);    
           
    if(tile.hasTagAt(offset)) {
      System.out.println("ItemTagTool.onItemUseFirst: Edit tag");
 
    } else {
      String tag = "Tag";
      tile.addTag(tag, offset);
      PacketHandler.INSTANCE.sendToServer(new PacketTaggedLocation(tile, tag, offset, true));
    }
  
    return true;
  }

  
  
}
