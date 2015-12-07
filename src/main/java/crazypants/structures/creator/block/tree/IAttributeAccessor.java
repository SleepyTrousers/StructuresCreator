package crazypants.structures.creator.block.tree;

public interface IAttributeAccessor {

  Object get(Object obj);

  void set(Object obj, Object val);

  boolean isValid();

  Class<?> getType();

  String getAttribuiteName();

}
