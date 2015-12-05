package crazypants.structures.creator.block.template.gui;

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
