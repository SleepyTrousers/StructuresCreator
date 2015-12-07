package crazypants.structures.creator.block.tree;

import java.lang.reflect.Field;

public class FieldAccessor implements IAttributeAccessor {

  protected final String attribuiteName;
  protected final Field field;
  protected final Class<?> typeClass;

  public FieldAccessor(Class<?> objClass, Class<?> typeClass, String attribuiteName) {
    this.attribuiteName = attribuiteName;
    this.typeClass = typeClass;
    Field f = null;
    try {
      f = objClass.getDeclaredField(attribuiteName);
      if(!f.getType().isAssignableFrom(typeClass)) {
        f = null;
        System.out.println("AttributeAccessor.AttributeAccessor: wrong attribute type");
      } else {
        f.setAccessible(true);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    field = f;
  }

  public FieldAccessor(Field field) {
    this.field = field;
    if(field != null) {
      field.setAccessible(true);
      attribuiteName = field.getName();
      typeClass = field.getType();
    } else {
      attribuiteName = null;
      typeClass = null;
    }
  }

  @Override
  public String getAttribuiteName() {
    return attribuiteName;
  }

  @Override
  public Class<?> getType() {
    return typeClass;
  }
  
  @Override
  public boolean isValid() {
    return field != null;
  }

  @Override
  public void set(Object obj, Object val) {
    try {
      field.set(obj, val);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public Object get(Object obj) {
    try {
      return field.get(obj);
    } catch (Exception e) {

      e.printStackTrace();
    }
    return null;
  }

}
