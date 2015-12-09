package crazypants.structures.creator.block.tree;

import java.lang.reflect.Field;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ITyped;
import crazypants.structures.api.ListElementType;

public class StructuresTreeNode extends DefaultMutableTreeNode {

  private static final long serialVersionUID = 1L;
  private final NodeData data;
  private final DefaultTreeModel treeModel;

  public StructuresTreeNode(Object owner, IAttributeAccessor aa, Object currentValue, DefaultTreeModel treeModel) {
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
    if(obj instanceof List<?>) {
      addChildren((List<?>) obj, treeModel);
      return;
    }
    Class<? extends Object> clz = obj.getClass();
    while (clz != null) {
      addFieldsFromClass(obj, treeModel, clz);
      clz = clz.getSuperclass();
    }
  }

  protected void addFieldsFromClass(Object obj, DefaultTreeModel treeModel, Class<? extends Object> clz) {
    Field[] fields = clz.getDeclaredFields();
    if(fields == null) {
      return;
    }
    boolean skipType = false;
    if(ITyped.class.isAssignableFrom(clz)) {
      skipType = true;
    }
    for (Field field : fields) {
      IAttributeAccessor aa = null;
      ListElementType lt = field.getAnnotation(ListElementType.class);
      if(lt != null) {
        aa = new ListAccessor(field, lt.elementType());
      } else if(field.getAnnotation(Expose.class) != null) {
        aa = new FieldAccessor(field);
      }
      if(aa != null && aa.isValid() && (!skipType || !"type".equals(aa.getAttribuiteName()))) {
        add(new StructuresTreeNode(obj, aa, aa.get(obj), treeModel));
      }
    }
  }

  private void addChildren(List<?> obj, DefaultTreeModel treeModel) {
    int index = 0;
    for (Object o : obj) {
      if(o != null) {
        ListElementAccessor aa = new ListElementAccessor(index, o.getClass(), data.getAttributeAccessor().getAttribuiteName());
        add(new StructuresTreeNode(obj, aa, o, treeModel));
        index++;
      }
    }
  }

}
