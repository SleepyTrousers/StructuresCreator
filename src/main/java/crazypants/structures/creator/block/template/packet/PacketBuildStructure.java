package crazypants.structures.creator.block.template.packet;

import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.creator.block.template.TileTemplateEditor;
import crazypants.structures.creator.endercore.common.network.MessageTileEntity;
import crazypants.structures.gen.StructureGenRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBuildStructure extends MessageTileEntity<TileTemplateEditor> implements IMessageHandler<PacketBuildStructure, IMessage> {

  private Rotation rotation;
  
  public PacketBuildStructure() {
    super();
  }

  public PacketBuildStructure(TileTemplateEditor tile, Rotation rotation) {
    super(tile);
    this.rotation = rotation != null ? rotation : Rotation.DEG_0; 
  }

  @Override
  public void toBytes(ByteBuf buf) {  
    super.toBytes(buf);
    buf.writeShort(rotation.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ord = buf.readShort();
    rotation = Rotation.values()[ord];
  }

  //@Override
  @Override
  public IMessage onMessage(PacketBuildStructure message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileTemplateEditor tile = message.getTileEntity(player.worldObj);
    if(tile == null) {
      return null;
    }

    if(tile.getName() == null) {
      return null;
    }
    IStructure str = createStructure(player.worldObj, tile, message.rotation);
    tile.setStructure(str);
    
    if(str != null) {


      AxisAlignedBB effectedBounds = str.getBounds();      
      ISitePreperation prep = str.getTemplate().getSitePreperation();
      if(prep != null) {    
        StructureBoundingBox prepBounds = prep.getEffectedBounds(str);      
        if(prepBounds != null) {        
          effectedBounds = StructureUtils.growBounds(effectedBounds, prepBounds);
        }
      }
      
      
      int xOffset = 1;
      if(effectedBounds.minX < 0) {
        xOffset += -(effectedBounds.minX);
      }
      int yOffset = 0;
      int zOffset = 1;
      if(effectedBounds.minZ < 0) {
        zOffset += -(effectedBounds.minZ);
      }
      tile.setOffsetX(xOffset);
      tile.setOffsetY(yOffset);
      tile.setOffsetZ(zOffset);
      
      BlockPos p = tile.getPos();
      str.setOrigin(new Point3i(p.getX() + xOffset, p.getY() + yOffset, p.getZ() + zOffset));
      str.getTemplate().build(str, player.worldObj, player.worldObj.rand, null); 
      str.onGenerated( player.worldObj);     
    }

    return null;
  }

  protected IStructure createStructure(World world, TileTemplateEditor tile, Rotation rot) {
    IStructureTemplate tmpl = StructureGenRegister.instance.getStructureTemplate(tile.getName(), true);
    if(tmpl != null) {
      IStructure res = tmpl.createInstance(rot);
      res.setOrigin(new Point3i());
      return res;
    }
    return null;
  }

}
