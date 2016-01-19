package crazypants.structures.creator.block;

import crazypants.structures.creator.EnderStructuresCreatorTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockClearMarker extends Block {

  public static final String NAME = "blockClearMarker";

  public static BlockClearMarker create() {
    BlockClearMarker res = new BlockClearMarker();
    res.init();
    return res;
  }

  protected BlockClearMarker() {
    super(Material.rock);
    setHardness(0.5F);
    setStepSound(Block.soundTypeStone);
    setHarvestLevel("pickaxe", 0);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setLightOpacity(0);

    setUnlocalizedName(NAME);
  }

  protected void init() {
    GameRegistry.registerBlock(this, NAME);
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
    return null;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.CUTOUT;
  }

  public boolean isFullCube() {
    return false;
  }

  // public boolean renderAsNormalBlock() {
  // return false;
  // }

}
