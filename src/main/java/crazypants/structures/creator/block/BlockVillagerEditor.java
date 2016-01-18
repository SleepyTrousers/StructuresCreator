package crazypants.structures.creator.block;

import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.block.villager.DialogVillagerEditor;
import crazypants.structures.creator.block.villager.GuiVillagerEditor;
import crazypants.structures.creator.block.villager.TileVillagerEditor;
import crazypants.structures.creator.endercore.BlockEnder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class BlockVillagerEditor extends BlockEnder<TileVillagerEditor> implements IGuiHandler {

  public static final String NAME = "blockVillagerEditor";

  public static BlockVillagerEditor create() {

    BlockVillagerEditor res = new BlockVillagerEditor();
    res.init();
    return res;
  }

  protected BlockVillagerEditor() {
    super(NAME, TileVillagerEditor.class);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setLightOpacity(0);
    setResistance(2000);
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if(!world.isRemote) {
      entityPlayer.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_VILLAGER_EDITOR, world, pos.getX(), pos.getY(), pos.getZ());
    }
    if(world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileVillagerEditor) {
        DialogVillagerEditor.openDialog((TileVillagerEditor) te);
      }
    }
    return true;
  }

  @Override
  protected void init() {
    super.init();
    EnderStructuresCreator.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_VILLAGER_EDITOR, this);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {    
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileVillagerEditor) {
      return new EmptyContainer();
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileVillagerEditor) {
      return new GuiVillagerEditor();      
    }
    return null;
  }


}
