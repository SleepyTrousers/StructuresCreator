package crazypants.structures.creator.block.template.packet;

import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.creator.block.MessageTileEntity;
import crazypants.structures.creator.block.template.TileTemplateEditor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketClearStructure extends MessageTileEntity<TileTemplateEditor> implements IMessageHandler<PacketClearStructure, IMessage> {

  public PacketClearStructure() {
  }

  public PacketClearStructure(TileTemplateEditor tile) {
    super(tile);
  }

  //@Override
  @Override
  public IMessage onMessage(PacketClearStructure message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileTemplateEditor tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }

    IStructure structure = tile.getStructure();
    if(structure == null) {
      return null;
    }

    AxisAlignedBB clearBounds = structure.getBounds();
    
    ISitePreperation prep = structure.getTemplate().getSitePreperation();
    if(prep != null) {    
      StructureBoundingBox prepBounds = prep.getEffectedBounds(structure);      
      if(prepBounds != null) {        
        clearBounds = StructureUtils.growBounds(clearBounds, prepBounds);
      }
    }
    
    StructureUtils.clearBounds(clearBounds,player.worldObj);
    tile.setStructure(null);
    return null;
  }

}
