package crazypants.structures.creator.block.tree;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ITyped;

public class StructuresTreeNode extends DefaultMutableTreeNode {

  private static final long serialVersionUID = 1L;
  private final NodeData data;
  private final DefaultTreeModel treeModel;

  public StructuresTreeNode(Object owner, AttributeAccessor aa, Object currentValue, DefaultTreeModel treeModel) {
    this.treeModel = treeModel;
    data = new NodeData(owner, aa, currentValue, this);
    setUserObject(data);
    if(currentValue != null) {
      addChildren(currentValue);
    }
  }

  public NodeData getData() {
    return data;
  }

  public void dataChanged(boolean structureChanged) {
    treeModel.nodeChanged(this);
    if(structureChanged) {
      treeModel.nodeStructureChanged(this);
    }
  }

  public void addChildren(Object obj) {
    if(obj == null) {
      return;
    }
    if(obj instanceof Collection<?>) {
      addChildren((Collection<?>) obj, treeModel);
      return;
    }
    Class<? extends Object> clz = obj.getClass();
    while(clz != null) {
      addFieldsFromClass(obj, treeModel, clz);
      clz = clz.getSuperclass();
    }
  }

  protected void addFieldsFromClass(Object obj, DefaultTreeModel treeModel, Class<? extends Object> clz) {
    Field[] fields = clz.getDeclaredFields();
    if(fields == null) {
      return;
    }
    boolean skipUid = false;
    if(ITyped.class.isAssignableFrom(clz)) {
      skipUid = true;
    }
    for (Field field : fields) {
      if(field.getAnnotation(Expose.class) != null) {
        AttributeAccessor aa = new AttributeAccessor(field);
        if(aa.isValid() && (!skipUid || !"type".equals(aa.getAttribuiteName()))) {
          add(new StructuresTreeNode(obj, aa, aa.get(obj), treeModel));
        }
      }
    }
  }

  private void addChildren(Collection<?> obj, DefaultTreeModel treeModel) {
    for (Object o : obj) {
      add(new StructuresTreeNode(obj, null, o, treeModel));
    }
  }

}