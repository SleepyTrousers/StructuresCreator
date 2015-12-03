package crazypants.structures.creator.block.template.gui;

import java.awt.Component;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ITyped;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureTemplate;

public class MyTreeNode extends DefaultMutableTreeNode {

  private NodeData data;

  public MyTreeNode(AttributeAccessor<?> aa, Object val) {
    data = new NodeData(aa, val);
    setUserObject(data);
    if(val != null) {
      addChildren(val);
    }

  }

  public NodeData getData() {
    return data;
  }

  public void setData(NodeData data) {
    this.data = data;
  }

  private void addChildren(Object obj) {

    if(obj instanceof Collection<?>) {
      addChildren((Collection<?>) obj);
      return;
    }

    Field[] fields = obj.getClass().getDeclaredFields();
    if(fields == null) {
      return;
    }
    for (Field field : fields) {
      if(field.getAnnotation(Expose.class) != null) {
        AttributeAccessor<?> aa = new AttributeAccessor<Object>(field);
        if(aa.isValid()) {
          add(new MyTreeNode(aa, aa.get(obj)));
        }
      }
    }
  }

  private void addChildren(Collection<?> obj) {
    for (Object o : obj) {
      add(new MyTreeNode(null, o));
    }
  }

  public static class NodeData {

    private AttributeAccessor<?> aa;
    private Object attributeVal;

    public NodeData(AttributeAccessor<?> aa, Object attributeVal) {
      this.aa = aa;
      this.attributeVal = attributeVal;
    }

    @Override
    public String toString() {
      if(aa != null) {
        return aa.getAttribuiteName();
      }
      if(attributeVal != null) {
        //        if(attributeVal instanceof String || attributeVal instanceof Number || attributeVal instanceof Boolean) {
        //          return attributeVal.toString();
        //        }
        return attributeVal.getClass().getSimpleName();
      }
      return "";
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
          if(nb.attributeVal != null && !(nb.attributeVal instanceof Collection)){
            text += ": " + nb.attributeVal.getClass().getSimpleName();
          }
        } else if(nb.attributeVal != null){
          //text = getValueString(nb.attributeVal);
          text = nb.attributeVal.getClass().getSimpleName();
        }
        
//        if(nb.attributeVal instanceof Collection<?>) {
//          text = nb.aa.getAttribuiteName();
//        } else {
//          
//        }
      }

      setText(text);

      //      if(leaf && isTutorialBook(value)) {
      //        setIcon(tutorialIcon);
      //        setToolTipText("This book is in the Tutorial series.");
      //      } else {
      //        setToolTipText(null); //no tool tip
      //      }

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
        if(val instanceof IStructureTemplate) {
          return ((IStructureTemplate) val).getUid();
        } 
        if(val instanceof ITyped) {
          return ((ITyped)val).getType();
        }
        //return val.getClass().getSimpleName();
        return val.toString();
      }
    }

  }

}
