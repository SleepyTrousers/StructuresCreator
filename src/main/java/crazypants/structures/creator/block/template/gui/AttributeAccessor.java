package crazypants.structures.creator.block.template.gui;

import java.lang.reflect.Field;

public class AttributeAccessor<T> {

  //private final Class<?> clazz;
  private final String attribuiteName;
  private final Field field;
  private final Class<T> typeClass;

  public AttributeAccessor(Class<?> objClass, Class<T> typeClass, String attribuiteName) {
    //this.clazz = clazz;
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
      typeClass = (Class<T>) field.getType();
    } else {
      attribuiteName = null;
      typeClass = null;
    }
  }

  public String getAttribuiteName() {
    return attribuiteName;
  }

  public Class<T> getType() {
    return typeClass;
  }
  
  public boolean isValid() {
    return field != null;
  }

  public void set(Object obj, T val) {
    try {
      field.set(obj, val);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public T get(Object obj) {
    try {
      return (T) field.get(obj);
    } catch (Exception e) {

      e.printStackTrace();
    }
    return null;
  }

}
