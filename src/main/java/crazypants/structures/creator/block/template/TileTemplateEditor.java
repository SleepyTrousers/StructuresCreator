package crazypants.structures.creator.block.template;

import com.enderio.core.common.TileEntityEnder;

import crazypants.structures.api.util.Point3i;
import net.minecraft.nbt.NBTTagCompound;

public class TileTemplateEditor extends TileEntityEnder {

  private int offsetX = 1;
  private int offsetY = 0;
  private int offsetZ = 1;

  private String name = "Template";
  private String exportDir;

  private boolean doneInit = false;

  @Override
  public boolean shouldRenderInPass(int pass) {
    return pass == 1;
  }

  @Override
  protected boolean shouldUpdate() {
    return !doneInit;
  }

  @Override
  protected void doUpdate() {
    if(!doneInit) {
      doneInit = true;
//      ToolRegister.onLoad(this);
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
//    ToolRegister.onUnload(this);
    doneInit = false;
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
    if(name != null && name.length() > 0) {
      root.setString("name", name);
    }
    if(exportDir != null && exportDir.length() > 0) {
      root.setString("exportDir", exportDir);
    }
//    root.setInteger("width", width);
//    root.setInteger("height", height);
//    root.setInteger("length", length);
//    root.setInteger("surfaceOffset", surfaceOffset);
//
//    root.setInteger("offsetX", offsetX);
//    root.setInteger("offsetY", offsetY);
//    root.setInteger("offsetZ", offsetZ);
//
//    if(!taggedLocations.isEmpty()) {
//      StructureUtils.writeTaggedLocationToNBT(taggedLocations, root);
//    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound root) {
    name = root.getString("name");
    exportDir = root.getString("exportDir");
    if(exportDir != null && exportDir.length() == 0) {
      exportDir = null;
    }
//    width = root.getInteger("width");
//    height = root.getInteger("height");
//    length = root.getInteger("length");
//    surfaceOffset = root.getInteger("surfaceOffset");
//
//    offsetX = root.getInteger("offsetX");
//    offsetY = root.getInteger("offsetY");
//    offsetZ = root.getInteger("offsetZ");
//
//    taggedLocations.clear();
//    StructureUtils.readTaggedLocations(taggedLocations, root);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    markDirty();
  }

  public String getExportDir() {
    return exportDir;
  }

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

//  public void setComponent(String name, IStructureComponent component) {
//    setName(name);
//    setSurfaceOffset(component.getSurfaceOffset());
//
//    Point3i size = component.getSize();
//    setWidth(size.x);
//    setHeight(size.y);
//    setLength(size.z);
//
//    taggedLocations.clear();
//    taggedLocations.putAll(component.getTaggedLocations());
//    
//  }

  public Point3i getStructureLocalPosition(Point3i blockCoord) {
    int localX = blockCoord.x - xCoord - getOffsetX();
    int localY = blockCoord.y - yCoord - getOffsetY();
    int localZ = blockCoord.z - zCoord - getOffsetZ();
    return new Point3i(localX, localY, localZ);
  }

}
