package crazypants.structures.creator.block.template;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.gen.structure.Structure;
import net.minecraft.nbt.NBTTagCompound;

public class TileTemplateEditor extends AbstractResourceTile {

  private int offsetX = 1;
  private int offsetY = 0;
  private int offsetZ = 1;

  private IStructure structure;

  private boolean doneInit = false;  

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1;
  }

  @Override
  public void doUpdate() {
    if(!doneInit) {
      doneInit = true;
      if(structure != null) {        
        structure.onLoaded(worldObj, new NBTTagCompound());
      }
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    onStructureOnload();
    doneInit = false;
  }

  protected void onStructureOnload() {
    if(structure != null) {      
      structure.onUnloaded(worldObj);
    }
  }

  //  @Override
  //  public AxisAlignedBB getRenderBoundingBox() {
  //    return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + width + offsetX, yCoord + height + offsetY, zCoord + length + offsetZ);
  //  }

  //  public AxisAlignedBB getStructureBounds() {
  //    return AxisAlignedBB.getBoundingBox(xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ, xCoord + width + offsetX, yCoord + height + offsetY,
  //        zCoord + length + offsetZ);
  //  }

  @Override
  public void writeCustomNBT(NBTTagCompound root) {
    super.writeCustomNBT(root);

    root.setInteger("offsetX", offsetX);
    root.setInteger("offsetY", offsetY);
    root.setInteger("offsetZ", offsetZ);

    if(structure != null) {
      NBTTagCompound strRoot = new NBTTagCompound();
      structure.writeToNBT(strRoot);
      root.setTag("structure", strRoot);
    }

  }

  @Override
  public void readCustomNBT(NBTTagCompound root) {
    super.readCustomNBT(root);

    offsetX = root.getInteger("offsetX");
    offsetY = root.getInteger("offsetY");
    offsetZ = root.getInteger("offsetZ");
    
    if(root.hasKey("structure")) {
      structure = new Structure(root.getCompoundTag("structure"));
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
    markDirty();
  }

  @Override
  public String getExportDir() {
    return exportDir;
  }

  @Override
  public void setExportDir(String exportDir) {
    this.exportDir = exportDir;
    markDirty();
  }

  public int getOffsetX() {
    return offsetX;
  }

  public void setOffsetX(int offsetX) {
    this.offsetX = offsetX;
    markDirty();
  }

  public int getOffsetY() {
    return offsetY;
  }

  public void setOffsetY(int offsetY) {
    this.offsetY = offsetY;
    markDirty();
  }

  public int getOffsetZ() {
    return offsetZ;
  }

  public void setOffsetZ(int offsetZ) {
    this.offsetZ = offsetZ;
    markDirty();
  }

  public IStructure getStructure() {
    return structure;
  }

  public void setStructure(IStructure structure) {
    onStructureOnload();
    this.structure = structure;
    doneInit = false;    
    markDirty();
  }
  
  

}
