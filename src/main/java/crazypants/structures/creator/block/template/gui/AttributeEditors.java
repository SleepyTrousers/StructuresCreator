package crazypants.structures.creator.block.template.gui;

import java.util.HashMap;
import java.util.Map;

import crazypants.structures.creator.block.template.gui.editors.BooleanEditor;
import crazypants.structures.creator.block.template.gui.editors.ComponentEditor;
import crazypants.structures.creator.block.template.gui.editors.StringEditor;

public class AttributeEditors {

  public static final AttributeEditors INSTANCE = new AttributeEditors();
  
  private final Map<Class<?>, IAttributeEditor> editors = new HashMap<Class<?>, IAttributeEditor>();
  
  public void registerEditor(IAttributeEditor ed) {
    if(ed == null) {
      return;
    }
    editors.put(ed.getType(), ed);
  }
  
  public IAttributeEditor getEditor(Class<?> type) {
    return editors.get(type);
  }
  
  private AttributeEditors() {
    registerEditor(new BooleanEditor());
    registerEditor(new StringEditor());
    registerEditor(new ComponentEditor());    
  }
  
  
}
