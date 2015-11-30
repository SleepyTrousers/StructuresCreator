package crazypants.structures.creator.block;

import com.enderio.core.common.BlockEnder;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.block.component.gui.EmptyContainer;
import crazypants.structures.creator.block.template.TileTemplateEditor;
import crazypants.structures.creator.block.template.gui.DialogTemplateEditor;
import crazypants.structures.creator.block.template.gui.GuiTemplateEditor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockTemplateEditor extends BlockEnder implements IGuiHandler {

  public static final String NAME = "blockTemplateEditor";

  public static BlockTemplateEditor create() {

//    PacketHandler.INSTANCE.registerMessage(PacketComponentToolGui.class, PacketComponentToolGui.class, PacketHandler.nextID(), Side.SERVER);
//    PacketHandler.INSTANCE.registerMessage(PacketBuildComponent.class, PacketBuildComponent.class, PacketHandler.nextID(), Side.SERVER);
//    PacketHandler.INSTANCE.registerMessage(PacketAddRemoveTaggedLocation.class, PacketAddRemoveTaggedLocation.class, PacketHandler.nextID(), Side.SERVER);
//    PacketHandler.INSTANCE.registerMessage(PacketSetTaggedLocation.class, PacketSetTaggedLocation.class, PacketHandler.nextID(), Side.SERVER);

    BlockTemplateEditor res = new BlockTemplateEditor();
    res.init();
    return res;
  }

  protected BlockTemplateEditor() {
    super(NAME, TileTemplateEditor.class);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setLightOpacity(0);
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
      entityPlayer.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_TEMPLATE_EDITOR, world, x, y, z);
    }
    if(world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileTemplateEditor) {
        DialogTemplateEditor.openDialog((TileTemplateEditor) te);
      }
    }
    return true;
  }

  @Override
  protected void init() {
    super.init();
    EnderStructuresCreator.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TEMPLATE_EDITOR, this);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {    
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileTemplateEditor) {
      return new EmptyContainer();
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileTemplateEditor) {
      return new GuiTemplateEditor(player, player.inventory, te);      
    }
    return null;
  }

}