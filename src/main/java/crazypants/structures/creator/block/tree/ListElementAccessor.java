package crazypants.structures.creator.block.tree;

import java.util.List;

public class ListElementAccessor implements IAttributeAccessor {

  private final int index;
  private final Class<?> type;
  private final String attributeName;

  public ListElementAccessor(int index, Class<?> type, String attributeName) {
    this.index = index;
    this.type = type;
    this.attributeName = attributeName;
  }

  @Override
  public Object get(Object obj) {
    if(!(obj instanceof List) || index < 0) {
      return null;
    }
    List<?> list = (List<?>) obj;
    if(list.size() >= index) {
      return null;
    }
    return list.get(index);
  }

  @Override
  public void set(Object obj, Object val) {
    if(!(obj instanceof List) || index < 0) {
      return;
    }
    @SuppressWarnings("unchecked")
    List<Object> list = (List<Object>) obj;
    if(list.size() >= index) {
      return;
    }
    list.set(index, val);
  }
    
  public void remove(Object obj) {
    if(!(obj instanceof List) || index < 0) {
      return;
    }
    @SuppressWarnings("unchecked")
    List<Object> list = (List<Object>) obj;
    if(list.size() <= index) {
      return;
    }
    list.remove(index);
  }

  @Override
  public boolean isValid() {    
    return index > 0;
  }

  @Override
  public Class<?> getType() {
    return type;
  }

  @Override
  public String getAttribuiteName() {
    return attributeName;
  }

}
