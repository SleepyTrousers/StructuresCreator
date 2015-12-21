package crazypants.structures.creator.block.tree;

public class NodeData {

  private final EditorTreeNode treeNode;

  private final Object owner;
  final IAttributeAccessor aa;
  Object currentValue;    

  public NodeData(Object owner, IAttributeAccessor aa, Object attributeVal, EditorTreeNode treeNode) {
    this.treeNode = treeNode;
    this.owner = owner;
    this.aa = aa;
    this.currentValue = attributeVal;
  }

  public Class<?> getType() {
    if(aa != null) {
      return aa.getType();
    }
    if(currentValue != null) {
      return currentValue.getClass();
    }
    return null;
  }

  public Object getValue() {
    return currentValue;
  }

  public void setValue(Object value) {    
    currentValue = value;
    if(aa != null && owner != null) {
      aa.set(owner, value);
    }
    boolean structureChanged = false;
    if(treeNode.getChildCount() > 0) {
      structureChanged = true;
      treeNode.removeAllChildren();      
    }
    treeNode.addChildren(value);
    structureChanged |= treeNode.getChildCount() > 0;
    treeNode.dataChanged(structureChanged);
  }

  public String getLabel() {
    if(aa != null) {
      return aa.getAttribuiteName();
    } else if(currentValue != null) {
      return currentValue.getClass().getSimpleName();
    }
    return "";
  }

  
  
  public EditorTreeNode getNode() {
    return treeNode;
  }
  
  public IAttributeAccessor getAttributeAccessor() {
    return aa;
  }

  public Object getOwner() {
    return owner;
  }

  @Override
  public String toString() {
    if(aa != null) {
      return "NodeData: " + aa.getAttribuiteName();
    }
    if(currentValue != null) {
      return "NodeData: " + currentValue.getClass().getSimpleName();
    }
    return "NodeData: ";
  }

}