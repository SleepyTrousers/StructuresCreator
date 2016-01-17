package crazypants.structures.creator.block;

import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.template.DialogTemplateEditor;
import crazypants.structures.creator.block.template.GuiTemplateEditor;
import crazypants.structures.creator.block.template.TileTemplateEditor;
import crazypants.structures.creator.block.template.packet.PacketBuildStructure;
import crazypants.structures.creator.block.template.packet.PacketClearStructure;
import crazypants.structures.creator.block.template.packet.PacketResourceTileGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTemplateEditor extends BlockEnder implements IGuiHandler {

  public static final String NAME = "blockTemplateEditor";

  public static BlockTemplateEditor create() {

    PacketHandler.INSTANCE.registerMessage(PacketResourceTileGui.class, PacketResourceTileGui.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketBuildStructure.class, PacketBuildStructure.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketClearStructure.class, PacketClearStructure.class, PacketHandler.nextID(), Side.SERVER);
    
    BlockTemplateEditor res = new BlockTemplateEditor();
    res.init();
    return res;
  }

  protected BlockTemplateEditor() {
    super(NAME, TileTemplateEditor.class);
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
      return new GuiTemplateEditor();      
    }
    return null;
  }

}