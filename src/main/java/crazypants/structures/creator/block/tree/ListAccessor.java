package crazypants.structures.creator.block.tree;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ListAccessor extends FieldAccessor {

  private final Class<?> elementType;
  
  public ListAccessor(Field field, Class<?> elementType) {
    super(field);  
    this.elementType = elementType;
  }
  
  public void add(Object owner, Object toAdd) {
    @SuppressWarnings("unchecked")
    List<Object> list = (List<Object>)get(owner);
    if(list == null) {
      list = new ArrayList<Object>();
      
    }
    list.add(toAdd);    
    set(owner, list);
  }

  public Class<?> getElementType() {
    return elementType;
  }

}
