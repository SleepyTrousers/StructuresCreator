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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    ListAccessor other = (ListAccessor) obj;
    if(elementType == null) {
      if(other.elementType != null)
        return false;
    } else if(!elementType.equals(other.elementType))
      return false;
    return true;
  }

}
