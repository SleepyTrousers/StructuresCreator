package crazypants.structures.creator.block;

import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.block.generator.DialogGeneratorEditor;
import crazypants.structures.creator.block.generator.GuiGeneratorEditor;
import crazypants.structures.creator.block.generator.TileGeneratorEditor;
import crazypants.structures.creator.endercore.common.BlockEnder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class BlockGeneratorEditor extends BlockEnder<TileGeneratorEditor> implements IGuiHandler {

  public static final String NAME = "blockGeneratorEditor";

  public static BlockGeneratorEditor create() {

    BlockGeneratorEditor res = new BlockGeneratorEditor();
    res.init();
    return res;
  }

  protected BlockGeneratorEditor() {
    super(NAME, TileGeneratorEditor.class);
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
      entityPlayer.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_GENERATOR_EDITOR, world, pos.getX(), pos.getY(), pos.getZ());
    }
    if(world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileGeneratorEditor) {
        DialogGeneratorEditor.openDialog((TileGeneratorEditor) te);
      }
    }
    return true;
  }

  @Override
  protected void init() {
    super.init();
    EnderStructuresCreator.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_GENERATOR_EDITOR, this);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {    
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileGeneratorEditor) {
      return new EmptyContainer();
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileGeneratorEditor) {
      return new GuiGeneratorEditor();      
    }
    return null;
  }

}