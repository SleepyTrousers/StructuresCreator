package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.lang.reflect.Array;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import crazypants.structures.api.ITyped;
import crazypants.structures.creator.block.tree.NodeData;
import crazypants.structures.gen.structure.TypeRegister;

public class TypedEditor<T extends ITyped> extends ComboEditor<T> {

  private Class<T> type;
  
  public TypedEditor(Class<T> type) {
    super(type);
    this.type = type;
    getComboBox().setRenderer(new Renderer());
  }

  @SuppressWarnings("unchecked")
  @Override
  protected T[] getValues() {
    List<T> res = TypeRegister.INSTANCE.getTypesOfType(type);
    if(res == null) {
      return null;
    }
        
    T[] result = (T[]) Array.newInstance(type, res.size() + 1);
    result[0] = null;
    for(int i=1;i<=res.size();i++) {
      T proto = res.get(i - 1);
      try {
        result[i] = (T) proto.getClass().newInstance();
      } catch (Exception e) {      
        e.printStackTrace();
      }
    }    
    NodeData data = getNodeData();
    if(data != null && data.getValue() != null) {
      
      T currentValue = (T)getNodeData().getValue();
      for(int i=0;i<result.length;i++) {
        T item = result[i];
        if(item != null && currentValue.getType().equals(item.getType())) {
          result[i] = currentValue;
          return result;
        }
      }
    }    
    return result;
  }
  
  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if(value instanceof ITyped) {
        setText(((ITyped) value).getType());
      } else {
        setText(" ");
      }
      return this;
    }

  }
}
