package crazypants.structures.creator.block.template.gui;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ITyped;
import crazypants.structures.api.gen.IStructureComponent;

public class MyTreeNode extends DefaultMutableTreeNode {

  private static final long serialVersionUID = 1L;
  private final NodeData data;
  private final DefaultTreeModel treeModel;

  public MyTreeNode(Object owner, AttributeAccessor aa, Object currentValue, DefaultTreeModel treeModel) {
    this.treeModel = treeModel;
    data = new NodeData(owner, aa, currentValue, this);
    setUserObject(data);
    if(currentValue != null) {
      addChildren(currentValue, treeModel);
    }

  }

  public NodeData getData() {
    return data;
  }

  public void dataChanged() {
    treeModel.nodeChanged(this);
  }

  private void addChildren(Object obj, DefaultTreeModel treeModel) {

    if(obj instanceof Collection<?>) {
      addChildren((Collection<?>) obj, treeModel);
      return;
    }

    Field[] fields = obj.getClass().getDeclaredFields();
    if(fields == null) {
      return;
    }
    for (Field field : fields) {
      if(field.getAnnotation(Expose.class) != null) {
        AttributeAccessor aa = new AttributeAccessor(field);
        if(aa.isValid()) {
          add(new MyTreeNode(obj, aa, aa.get(obj), treeModel));
        }
      }
    }
  }

  private void addChildren(Collection<?> obj, DefaultTreeModel treeModel) {
    for (Object o : obj) {
      add(new MyTreeNode(obj, null, o, treeModel));
    }
  }

  public static class NodeData {

    private MyTreeNode treeNode;

    private AttributeAccessor aa;
    private Object attributeVal;
    private Object owner;

    public NodeData(Object owner, AttributeAccessor aa, Object attributeVal, MyTreeNode treeNode) {
      this.treeNode = treeNode;
      this.owner = owner;
      this.aa = aa;
      this.attributeVal = attributeVal;
    }

    public Class<?> getType() {
      if(aa != null) {
        return aa.getType();
      }
      if(attributeVal != null) {
        return attributeVal.getClass();
      }
      return null;
    }

    public Object getValue() {
      return attributeVal;
    }

    public void setValue(Object value) {
      attributeVal = value;
      if(aa != null && owner != null) {
        aa.set(owner, value);
      }
      treeNode.dataChanged();
    }

    public String getLabel() {
      if(aa != null) {
        return aa.getAttribuiteName();
      } else if(attributeVal != null) {
        return attributeVal.getClass().getSimpleName();
      }
      return "";
    }

    @Override
    public String toString() {
      if(aa != null) {
        return "NodeData: " + aa.getAttribuiteName();
      }
      if(attributeVal != null) {
        return "NodeData: " + attributeVal.getClass().getSimpleName();
      }
      return "NodeData: ";
    }

  }

  public static class MyRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      if(!(value instanceof MyTreeNode)) {
        return this;
      }

      MyTreeNode node = (MyTreeNode) value;
      NodeData nb = node.getData();

      String text = "";
      if(leaf) {
        if(nb.aa != null) {
          text = nb.aa.getAttribuiteName() + ":";
          if(nb.attributeVal != null) {
            text = text + " " + getValueString(nb.attributeVal);
          }
        } else {
          text = getValueString(nb.attributeVal);
        }
      } else {
        if(nb.aa != null) {
          text = nb.aa.getAttribuiteName();
          if(nb.attributeVal != null && !(nb.attributeVal instanceof Collection)) {
            text += ": " + nb.attributeVal.getClass().getSimpleName();
          }
        } else if(nb.attributeVal != null) {
          text = nb.attributeVal.getClass().getSimpleName();
//          if(nb.attributeVal instanceof IStructureTemplate) {
//            text = text + ": " + ((IStructureTemplate) nb.attributeVal).getUid();
//          }
        }
      }
      setText(text);
      return this;
    }

    private String getValueString(Object val) {
      if(val == null) {
        return "";
      } else {
        if(val instanceof String) {
          return val.toString();
        }
        if(val instanceof IStructureComponent) {
          return ((IStructureComponent) val).getUid();
        }
//        if(val instanceof IStructureTemplate) {
//          return ((IStructureTemplate) val).getUid();
//        }
        if(val instanceof ITyped) {
          return ((ITyped) val).getType();
        }
        return val.toString();
      }
    }

  }

}
