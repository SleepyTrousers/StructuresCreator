package crazypants.structures.creator.item;

import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemClearTool extends Item {

  private static final String NAME = "itemClearTool";

  public static ItemClearTool create() {
    ItemClearTool res = new ItemClearTool();
    res.init();
    return res;
  }

  private ItemClearTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
    if (world.isRemote) {
      return true;
    }

    BlockPos bc = pos.offset(side);
    floodFill(world, bc, 0);
    return true;
  }

  private int floodFill(World world, BlockPos bc, int filled) {
    if (!world.isAirBlock(bc)) {
      return filled;
    }
    if (filled >= 100) {
      return filled;
    }

    world.setBlockState(bc, EnderStructuresCreator.blockClearMarker.getDefaultState());
    filled++;
    if (filled >= 100) {
      return filled;
    }

    for (EnumFacing dir : EnumFacing.VALUES) {
      if (dir != EnumFacing.UP) {
        filled = floodFill(world, bc.offset(dir), filled);
      }
    }
    return filled;
  }

}
