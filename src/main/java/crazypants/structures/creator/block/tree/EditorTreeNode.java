package crazypants.structures.creator.block.tree;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ITyped;
import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.IResource;
import crazypants.structures.gen.structure.loot.LootCategories;

public class EditorTreeNode extends DefaultMutableTreeNode {

  private static final long serialVersionUID = 1L;
  private final NodeData data;
  private final DefaultTreeModel treeModel;

  public EditorTreeNode(Object owner, IAttributeAccessor aa, Object currentValue, DefaultTreeModel treeModel) {
    this(owner,aa, currentValue, treeModel, true);
  }
  
  public EditorTreeNode(Object owner, IAttributeAccessor aa, Object currentValue, DefaultTreeModel treeModel, boolean addChildrenOfResources) {
    this.treeModel = treeModel;
    data = new NodeData(owner, aa, currentValue, this);
    setUserObject(data);    
    if( addChildrenOfResources || !(currentValue instanceof IResource) || (currentValue instanceof LootCategories)) {
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
    List<Class<? extends Object>> toScan = new ArrayList<Class<? extends Object>>();
    Class<? extends Object> clz = obj.getClass();
    while (clz != null) {
      toScan.add(clz);
      clz = clz.getSuperclass();
    }
    Collections.reverse(toScan);
    for (Class<? extends Object> scan : toScan) {
      addFieldsFromClass(obj, treeModel, scan);
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
        add(new EditorTreeNode(obj, aa, aa.get(obj), treeModel, false));
      }
    }
  }

  private void addChildren(List<?> obj, DefaultTreeModel treeModel) {
    int index = 0;
    for (Object o : obj) {
      if(o != null) {
        IAttributeAccessor listAt = data.getAttributeAccessor();
        ListElementAccessor aa = new ListElementAccessor(listAt.getDeclaringClass(), listAt.getAttribuiteName(), index, o.getClass());
        add(new EditorTreeNode(obj, aa, o, treeModel, false));
        index++;
      }
    }
  }

}
