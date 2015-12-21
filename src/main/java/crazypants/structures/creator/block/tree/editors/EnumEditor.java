package crazypants.structures.creator.block.tree.editors;

import crazypants.structures.creator.block.tree.IAttributeAccessor;
import crazypants.structures.creator.block.tree.NodeData;

public class EnumEditor extends ComboEditor<Object> {

  public EnumEditor() {
    super(Object.class);
  }
  
  @Override
  public Class<?> getType() {
    return Enum.class;
  }

  @Override
  protected Object[] getValues() {
    NodeData nd = getNodeData();    
    IAttributeAccessor aa = nd.getAttributeAccessor();    
    return aa.getType().getEnumConstants();
  }

}
