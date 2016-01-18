package crazypants.structures.creator.item;

import crazypants.structures.creator.EnderStructuresCreatorTab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

public class ItemDebugTool extends Item {

  private static final String NAME = "itemDebugTool";

  public static ItemDebugTool create() {
    ItemDebugTool res = new ItemDebugTool();
    res.init();
    return res;
  }

  private ItemDebugTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);    
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {

    if (world.isRemote) {
      return true;
    }

    String name;
    IBlockState blk = world.getBlockState(pos);
    if(blk == null) {
      name = "none";
    } else {
      UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(blk.getBlock());
      if(uid == null) {
        name = blk.getBlock().getUnlocalizedName();
      } else {
        name = uid.modId + ":" + uid.name;
      }
    }
    
    int meta = 0;
    if(blk != null) {
      meta = blk.getBlock().getMetaFromState(blk);
    }
        
    player.addChatComponentMessage(new ChatComponentText("Block: " + name + " meta=" + meta + " metaBits=" + toBitString(meta)));
    
    return true;
  }
  
  public static String toBitString(int meta) {
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < 4; k++) {
      if (((meta >> k) & 1) == 1) {
        sb.append(1);
      } else {
        sb.append(0);
      }
    }
    return sb.toString();
  }

  

}