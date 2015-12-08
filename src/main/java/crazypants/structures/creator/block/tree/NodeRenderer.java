package crazypants.structures.creator.block.tree;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import crazypants.structures.api.ITyped;
import crazypants.structures.api.gen.IStructureComponent;

public class NodeRenderer extends DefaultTreeCellRenderer {

  private static final long serialVersionUID = 1L;

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    if(!(value instanceof StructuresTreeNode)) {
      return this;
    }

    StructuresTreeNode node = (StructuresTreeNode) value;
    NodeData nb = node.getData();

    String text = "";
    if(leaf) {
      if(nb.getAttributeAccessor() != null) {
        if(nb.getAttributeAccessor() instanceof FieldAccessor) {
          text = nb.getAttributeAccessor().getAttribuiteName();
        }
        if(nb.getValue() != null) {
          if(text.length() > 0) {
            text +=  ": ";
          }
          text = text + getValueString(nb.getValue());
        }
      } else {
        text = getValueString(nb.getValue());
      }
    } else {
      if(nb.getAttributeAccessor() != null) {
        if(nb.getAttributeAccessor() instanceof FieldAccessor) {
          text = nb.getAttributeAccessor().getAttribuiteName() + ": ";
        }
        if(nb.getValue() != null && !(nb.getValue() instanceof Collection)) {
          text += nb.getValue().getClass().getSimpleName();
        }
      } else if(nb.getValue() != null) {
        text = nb.getValue().getClass().getSimpleName();
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
      if(val instanceof ITyped) {
        return ((ITyped) val).getType();
      }
      return val.toString();
    }
  }

}