package crazypants.structures.creator.block.component;

import com.enderio.core.common.TileEntityEnder;

import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileComponentTool extends TileEntityEnder {

  private int width = 9;
  private int height = 9;
  private int length = 9;
  private int surfaceOffset = 0;
  
  private int offsetX = 1;
  private int offsetY = 0;
  private int offsetZ = 1;
  

  private String name = "Component";
  private String exportDir;

  @Override
  protected boolean shouldUpdate() {
    return false;
  }
  
  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + width + offsetX, yCoord + height + offsetY, zCoord + length + offsetZ);
  }
  
  public AxisAlignedBB getStructureBounds() {
    return AxisAlignedBB.getBoundingBox(xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ, xCoord + width + offsetX, yCoord + height + offsetY, zCoord + length + offsetZ);
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    if(name != null && name.length() > 0) {
      root.setString("name", name);
    }
    if(exportDir != null && exportDir.length() > 0) {
      root.setString("exportDir", exportDir);
    }
    root.setInteger("width", width);
    root.setInteger("height", height);
    root.setInteger("length", length);
    root.setInteger("surfaceOffset", surfaceOffset);
    
    root.setInteger("offsetX", offsetX);
    root.setInteger("offsetY", offsetY);
    root.setInteger("offsetZ", offsetZ);
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    name = root.getString("name");
    exportDir = root.getString("exportDir");
    if(exportDir != null && exportDir.length() == 0) {
      exportDir = null;
    }
    width = root.getInteger("width");
    height = root.getInteger("height");
    length = root.getInteger("length");
    surfaceOffset = root.getInteger("surfaceOffset");
    
    offsetX = root.getInteger("offsetX");
    offsetY = root.getInteger("offsetY");
    offsetZ = root.getInteger("offsetZ");
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getSurfaceOffset() {
    return surfaceOffset;
  }

  public void setSurfaceOffset(int surfaceOffset) {
    this.surfaceOffset = surfaceOffset;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExportDir() {
    return exportDir;
  }

  public void setExportDir(String exportDir) {
    this.exportDir = exportDir;
  }  
  
  public int getOffsetX() {
    return offsetX;
  }

  public void setOffsetX(int offsetX) {
    this.offsetX = offsetX;
  }

  public int getOffsetY() {
    return offsetY;
  }

  public void setOffsetY(int offsetY) {
    this.offsetY = offsetY;
  }

  public int getOffsetZ() {
    return offsetZ;
  }

  public void setOffsetZ(int offsetZ) {
    this.offsetZ = offsetZ;
  }

  public void setComponent(String name, IStructureComponent component) {
    setName(name);
    setSurfaceOffset(component.getSurfaceOffset());
    
    Point3i size = component.getSize();    
    setWidth(size.x);
    setHeight(size.y);
    setLength(size.z);
    
        
  }

 

}
