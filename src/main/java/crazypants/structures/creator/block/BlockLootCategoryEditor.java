package crazypants.structures.creator.block;

import com.enderio.core.common.BlockEnder;

import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.block.generator.GuiGeneratorEditor;
import crazypants.structures.creator.block.loot.DialogLootCategeoryEditor;
import crazypants.structures.creator.block.loot.TileLootCategory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class BlockLootCategoryEditor extends BlockEnder<TileLootCategory> implements IGuiHandler {

  public static final String NAME = "blockLootCategoryEditor";

  public static BlockLootCategoryEditor create() {

    BlockLootCategoryEditor res = new BlockLootCategoryEditor();
    res.init();
    return res;
  }

  protected BlockLootCategoryEditor() {
    super(NAME, TileLootCategory.class);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setLightOpacity(0);
    setResistance(2000);
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {    
    return null;
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if(!world.isRemote) {
      entityPlayer.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_LOOT_EDITOR, world, pos.getX(), pos.getY(), pos.getZ());
    }
    if(world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileLootCategory) {
        DialogLootCategeoryEditor.openDialog((TileLootCategory) te);
      }
    }
    return true;
  }

  @Override
  protected void init() {
    super.init();
    EnderStructuresCreator.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_LOOT_EDITOR, this);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileLootCategory) {
      return new EmptyContainer();
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileLootCategory) {
      return new GuiGeneratorEditor();
    }
    return null;
  }

}