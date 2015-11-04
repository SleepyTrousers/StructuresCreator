package crazypants.structures.creator.block.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enderio.core.common.TileEntityEnder;

import crazypants.structures.StructureUtils;
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

  private final Map<String, List<Point3i>> taggedLocations = new HashMap<String, List<Point3i>>();
  
  private boolean doneInit = false;

  @Override
  protected boolean shouldUpdate() {
    return !doneInit;
  }

  @Override
  protected void doUpdate() {
    if(!doneInit) {
      doneInit = true;
      ToolRegister.onLoad(this);            
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();    
    ToolRegister.onUnload(this);
    doneInit = false;
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + width + offsetX, yCoord + height + offsetY, zCoord + length + offsetZ);
  }

  public AxisAlignedBB getStructureBounds() {
    return AxisAlignedBB.getBoundingBox(xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ, xCoord + width + offsetX, yCoord + height + offsetY,
        zCoord + length + offsetZ);
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

    if(!taggedLocations.isEmpty()) {
      StructureUtils.writeTaggedLocationToNBT(taggedLocations, root);
    }
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

    taggedLocations.clear();
    StructureUtils.readTaggedLocations(taggedLocations, root);
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

  public void addTag(String tag, Point3i loc) {    
    if(tag == null || loc == null) {
      return;
    }
    List<Point3i> res = taggedLocations.get(tag);
    if(res == null) {
      res = new ArrayList<Point3i>();
      taggedLocations.put(tag, res);
    }
    res.add(loc);
  }

  public void removeTag(String tag, Point3i loc) {
    if(tag == null) {
      return;
    }
    List<Point3i> locs = taggedLocations.get(tag);
    if(locs != null) {
      locs.remove(loc);
    }
  }

  public void removeTags(String tag) {
    if(tag != null && taggedLocations.containsKey(tag)) {
      taggedLocations.put(tag, null);
    }
  }

  public List<Point3i> getTaggedLocations(String tag) {
    List<Point3i> res = taggedLocations.get(tag);
    if(res == null) {
      return Collections.emptyList();
    }
    return res;
  }

  public Map<String, List<Point3i>> getTaggedLocations() {
    return taggedLocations;
  }

  public void setComponent(String name, IStructureComponent component) {
    setName(name);
    setSurfaceOffset(component.getSurfaceOffset());

    Point3i size = component.getSize();
    setWidth(size.x);
    setHeight(size.y);
    setLength(size.z);
    
    taggedLocations.clear();
    taggedLocations.putAll(component.getTaggedLocations());    
  }

  public boolean hasTagAt(Point3i loc) {    
    return getTagAt(loc) != null;
  }

  public String getTagAt(Point3i loc) {
    if(loc == null) {
      return null;
    }
    for (Entry<String, List<Point3i>> e : taggedLocations.entrySet()) {
      List<Point3i> locs = e.getValue();
      if(locs != null && locs.contains(loc)) {
        return e.getKey();
      }
    }
    return null;
  }

  public Point3i getSize() {
    return new Point3i(width,height,length);
  }

  public int getTaggedLocationsCount() {
    int res = 0;
    for(List<Point3i> coords : taggedLocations.values()) {
      if(coords != null) {
        res += coords.size();
      }
    }
    return res;
  }
  
}
