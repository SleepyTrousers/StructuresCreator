package crazypants.structures.creator.block;

import com.enderio.core.common.BlockEnder;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.block.generator.GuiGeneratorEditor;
import crazypants.structures.creator.block.loot.DialogLootCategeoryEditor;
import crazypants.structures.creator.block.loot.TileLootCategory;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockLootCategoryEditor extends BlockEnder implements IGuiHandler {

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
  public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
    return null;
  }

  @Override
  public int getRenderBlockPass() {
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
  }

  @Override
  protected boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
    if(!world.isRemote) {
      entityPlayer.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_LOOT_EDITOR, world, x, y, z);
    }
    if(world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
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
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileLootCategory) {
      return new EmptyContainer();
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileLootCategory) {
      return new GuiGeneratorEditor();
    }
    return null;
  }

}