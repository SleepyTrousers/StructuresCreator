
package crazypants.structures.creator.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public abstract class TileEntityBase extends TileEntity implements ITickable {

  public TileEntityBase() {
  }

  @Override
  public void update() {

  }

  @Override
  public final void readFromNBT(NBTTagCompound root) {
    super.readFromNBT(root);
    readCustomNBT(root);
  }

  @Override
  public final void writeToNBT(NBTTagCompound root) {
    super.writeToNBT(root);
    writeCustomNBT(root);
  }

  @Override
  public Packet<?> getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeCustomNBT(tag);
    return new S35PacketUpdateTileEntity(getPos(), 1, tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    readCustomNBT(pkt.getNbtCompound());
  }

  public boolean canPlayerAccess(EntityPlayer player) {
    return !isInvalid() && player.getDistanceSqToCenter(getPos().add(0.5, 0.5, 0.5)) <= 64D;
  }

  protected abstract void writeCustomNBT(NBTTagCompound root);

  protected abstract void readCustomNBT(NBTTagCompound root);

  protected void updateBlock() {
    if (worldObj != null) {
      worldObj.markBlockForUpdate(getPos());
    }
  }

  protected boolean isPoweredRedstone() {
    return worldObj.isBlockLoaded(getPos()) ? worldObj.getStrongPower(getPos()) > 0 : false;
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
    return oldState.getBlock() != newSate.getBlock();
  }
}
