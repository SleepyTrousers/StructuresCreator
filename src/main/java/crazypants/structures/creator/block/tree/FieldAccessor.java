package crazypants.structures.creator.block.tree;

import java.lang.reflect.Field;

public class FieldAccessor implements IAttributeAccessor {

  protected final String attribuiteName;  
  protected final Class<?> attributeType;
  protected final Field field;

  public FieldAccessor(Class<?> ownersClass, Class<?> attributeClass, String attribuiteName) {
    this.attribuiteName = attribuiteName;
    this.attributeType = attributeClass;
    Field f = null;
    try {
      f = ownersClass.getDeclaredField(attribuiteName);
      if(!f.getType().isAssignableFrom(attributeClass)) {
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
      attributeType = field.getType();
    } else {
      attribuiteName = null;
      attributeType = null;
    }
  }

  @Override
  public String getAttribuiteName() {
    return attribuiteName;
  }

  @Override
  public Class<?> getType() {
    return attributeType;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((attribuiteName == null) ? 0 : attribuiteName.hashCode());
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    result = prime * result + ((attributeType == null) ? 0 : attributeType.hashCode());
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
    FieldAccessor other = (FieldAccessor) obj;
    if(attribuiteName == null) {
      if(other.attribuiteName != null)
        return false;
    } else if(!attribuiteName.equals(other.attribuiteName))
      return false;
    if(field == null) {
      if(other.field != null)
        return false;
    } else if(!field.equals(other.field))
      return false;
    if(attributeType == null) {
      if(other.attributeType != null)
        return false;
    } else if(!attributeType.equals(other.attributeType))
      return false;
    return true;
  }
  
  

}
