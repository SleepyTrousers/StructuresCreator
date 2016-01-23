package crazypants.structures.creator.block.tree;

import java.awt.Component;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import crazypants.structures.api.ITyped;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.IResource;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;

public class NodeRenderer extends DefaultTreeCellRenderer {

  private static final long serialVersionUID = 1L;

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

    if(!(value instanceof EditorTreeNode)) {
      return this;
    }

    EditorTreeNode node = (EditorTreeNode) value;
    NodeData nb = node.getData();
    IAttributeAccessor aa = nb.getAttributeAccessor();

    String text = "";
    if(leaf) {
      if(aa != null) {
        if(aa instanceof FieldAccessor) {
          text = aa.getAttribuiteName();
        }
        if(nb.getValue() != null) {
          if(text.length() > 0) {
            text += ": ";
          }
          text = text + getValueString(nb.getValue());
        }
      } else {
        text = getValueString(nb.getValue());
      }
    } else {
      if(aa != null) {
        if(aa instanceof FieldAccessor) {
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
    
    Icon icon = getIcon(nb);
    if(icon != null) {
      setIcon(icon);
    } else if(leaf) {
      setIcon(Icons.DOT);
    }
    
    if(aa != null && aa.getDocumentation() != null) {      
      setToolTipText(aa.getDocumentation());
    } else {
      setToolTipText(null);
    }
    
    return this;
  }
  
  private Icon getIcon(NodeData nd) {
    if(nd == null || nd.getType() == null) {
      return null;
    }
    Class<?> type = nd.getType();
    if(IBehaviour.class.isAssignableFrom(type)) {
      return Icons.BEHAVIOUR;
    }
    if(ISitePreperation.class.isAssignableFrom(type)) {
      return Icons.PREPERATION;
    }
    if(ILocationSampler.class.isAssignableFrom(type)) {
      return Icons.LOCATION_SAMPLER;
    }    
    if(ISiteValidator.class.isAssignableFrom(type) || IChunkValidator.class.isAssignableFrom(type)) {
      return Icons.VALIDATOR;
    }    
    if(IDecorator.class.isAssignableFrom(type)) {
      return Icons.DECORATOR;
    }
    if(IStructureComponent.class.isAssignableFrom(type)) {
      return Icons.COMPONENT;
    }
    if(IStructureTemplate.class.isAssignableFrom(type)) {
      return Icons.TEMPLATE;
    }
    if(ICondition.class.isAssignableFrom(type)) {
      return Icons.CONDITION;
    }
    if(IAction.class.isAssignableFrom(type)) {
      return Icons.ACTION;
    }
    if(Rotation.class.isAssignableFrom(type)) {
      return Icons.ROTATION;
    }
    if(Point3i.class.isAssignableFrom(type) || "taggedPosition".equals(nd.getLabel())) {
      return Icons.LOCATION;
    }
//    if(boolean.class.isAssignableFrom(type)) {
//      return Icons.CHECK_BOX;
//    }
    return null;
  }

  private String getValueString(Object val) {
    if(val == null) {
      return "";
    } else {
      if(val instanceof String) {
        return val.toString();
      }
      if(val instanceof IBlockState) {
        IBlockState blockState = (IBlockState)val;
        Block blk = blockState.getBlock();
        Item item = GameData.getBlockItemMap().get(blk);
        if(item == null) {
          return "Unknown";
        }    
        ItemStack is = new ItemStack(blk, 1, blk.getMetaFromState(blockState));    
        return is.getDisplayName();                   
      }
      if(val instanceof IStructureComponent) {
        return ((IStructureComponent) val).getUid();
      }
      if(val instanceof ITyped) {
        return ((ITyped) val).getType();
      }
      if(val instanceof IResource) {
        return ((IResource)val).getUid();
      }
      if(val instanceof ItemStack) {
        ItemStack is = (ItemStack)val;
        return is.stackSize + " " + is.getDisplayName();
      }
      return val.toString();
    }
  }

}