package crazypants.structures.creator.block.tree.editors;

import crazypants.structures.creator.block.tree.IAttributeEditor;

public abstract class AbstractAttributeEditor implements IAttributeEditor {

  private final Class<?> type;

  protected AbstractAttributeEditor(Class<?> type) {  
    this.type = type;
  }

  @Override
  public Class<?> getType() {  
    return type;
  }
  
}
