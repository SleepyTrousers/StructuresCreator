package crazypants.structures.creator.block;

import com.enderio.core.common.BlockEnder;

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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;

public class BlockTemplateEditor extends BlockEnder<TileTemplateEditor> implements IGuiHandler {

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
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {    
    return null;
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if(!world.isRemote) {
      entityPlayer.openGui(EnderStructuresCreator.instance, GuiHandler.GUI_ID_TEMPLATE_EDITOR, world, pos.getX(), pos.getY(), pos.getZ());
    }
    if(world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
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
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileTemplateEditor) {
      return new EmptyContainer();
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileTemplateEditor) {
      return new GuiTemplateEditor();      
    }
    return null;
  }

}