package crazypants.structures.creator.block;

import com.enderio.core.common.BlockEnder;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.component.ContainerComponentTool;
import crazypants.structures.creator.block.component.DialogComponentTool;
import crazypants.structures.creator.block.component.GuiComponentTool;
import crazypants.structures.creator.block.component.PacketBuildComponent;
import crazypants.structures.creator.block.component.PacketComponentToolGui;
import crazypants.structures.creator.block.component.PacketTaggedLocation;
import crazypants.structures.creator.block.component.TileComponentTool;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockComponentTool extends BlockEnder implements IGuiHandler {

  public static final String NAME = "blockComponentTool";

  public static BlockComponentTool create() {
    
    PacketHandler.INSTANCE.registerMessage(PacketComponentToolGui.class, PacketComponentToolGui.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketBuildComponent.class, PacketBuildComponent.class, PacketHandler.nextID(), Side.SERVER);   
    PacketHandler.INSTANCE.registerMessage(PacketTaggedLocation.class, PacketTaggedLocation.class, PacketHandler.nextID(), Side.SERVER);
    
    BlockComponentTool res = new BlockComponentTool();
    res.init();
    return res;
  }

  protected BlockComponentTool() {
    super(NAME, TileComponentTool.class);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setLightOpacity(0);
    //setBlockTextureName(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
  }
  
  @Override
  public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
    return null;
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
  }

  @Override
  protected boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
        if(!world.isRemote) {
          entityPlayer.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_COMPONENT_TOOL, world, x, y, z);
        }
    if(world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileComponentTool) {
        DialogComponentTool.openDialog((TileComponentTool) te);                
      }
    }
    return true;
  }

  @Override
  protected void init() {
    super.init();
    EnderStructuresCreator.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_COMPONENT_TOOL, this);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileComponentTool) {
      return new ContainerComponentTool(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileComponentTool) {
      return new GuiComponentTool(player, player.inventory, te);
    }
    return null;
  }

}
