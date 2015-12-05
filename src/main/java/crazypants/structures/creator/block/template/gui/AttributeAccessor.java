package crazypants.structures.creator.block.template.gui;

import java.lang.reflect.Field;

public class AttributeAccessor {

  private final String attribuiteName;
  private final Field field;
  private final Class<?> typeClass;

  public AttributeAccessor(Class<?> objClass, Class<?> typeClass, String attribuiteName) {
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

  public AttributeAccessor(Field field) {
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

  public String getAttribuiteName() {
    return attribuiteName;
  }

  public Class<?> getType() {
    return typeClass;
  }
  
  public boolean isValid() {
    return field != null;
  }

  public void set(Object obj, Object val) {
    try {
      field.set(obj, val);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Object get(Object obj) {
    try {
      return field.get(obj);
    } catch (Exception e) {

      e.printStackTrace();
    }
    return null;
  }

}
