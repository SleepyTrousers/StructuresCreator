package crazypants.structures.creator.block;

import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.GuiHandler;
import crazypants.structures.creator.PacketHandler;
import crazypants.structures.creator.block.component.TileComponentEditor;
import crazypants.structures.creator.block.component.gui.DialogComponentEditor;
import crazypants.structures.creator.block.component.gui.GuiComponentEditor;
import crazypants.structures.creator.block.component.packet.PacketAddRemoveTaggedLocation;
import crazypants.structures.creator.block.component.packet.PacketBuildComponent;
import crazypants.structures.creator.block.component.packet.PacketComponentEditorGui;
import crazypants.structures.creator.block.component.packet.PacketSetTaggedLocation;
import crazypants.structures.creator.endercore.BlockEnder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;

public class BlockComponentEditor extends BlockEnder<TileComponentEditor> implements IGuiHandler {

  public static final String NAME = "blockComponentEditor";

  public static BlockComponentEditor create() {

    PacketHandler.INSTANCE.registerMessage(PacketComponentEditorGui.class, PacketComponentEditorGui.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketBuildComponent.class, PacketBuildComponent.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketAddRemoveTaggedLocation.class, PacketAddRemoveTaggedLocation.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketSetTaggedLocation.class, PacketSetTaggedLocation.class, PacketHandler.nextID(), Side.SERVER);

    BlockComponentEditor res = new BlockComponentEditor();
    res.init();
    return res;
  }

  protected BlockComponentEditor() {
    super(NAME, TileComponentEditor.class);
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
      entityPlayer.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_COMPONENT_EDITOR, world, pos.getX(), pos.getY(), pos.getZ());
    }
    if(world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileComponentEditor) {
        DialogComponentEditor.openDialog((TileComponentEditor) te);
      }
    }
    return true;
  }

  @Override
  protected void init() {
    super.init();
    EnderStructuresCreator.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_COMPONENT_EDITOR, this);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {    
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileComponentEditor) {
      return new EmptyContainer();
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileComponentEditor) {
      return new GuiComponentEditor();
    }
    return null;
  }

}
