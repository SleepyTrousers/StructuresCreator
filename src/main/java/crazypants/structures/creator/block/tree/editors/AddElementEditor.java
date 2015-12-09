package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import crazypants.structures.api.ITyped;
import crazypants.structures.creator.block.tree.IAttributeAccessor;
import crazypants.structures.creator.block.tree.ListAccessor;
import crazypants.structures.creator.block.tree.NodeData;
import crazypants.structures.gen.structure.TypeRegister;

public class AddElementEditor extends AbstractAttributeEditor {

  private final JButton but;
  private final JPanel pan;
  private final JComboBox<Object> cb;
  private final Renderer renderer;
  
  private NodeData nodeData;
  private ListAccessor listAcc;

  private List<Object> options = new ArrayList<Object>();

  public AddElementEditor() {
    super(List.class);
    but = new JButton("Add");
    but.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Object newItem = createNewInstance();
        if(nodeData == null || listAcc == null || newItem == null) {
          return;
        }        
        listAcc.add(nodeData.getOwner(), newItem);

        nodeData.getNode().removeAllChildren();
        nodeData.getNode().addChildren(nodeData.getValue());
        nodeData.getNode().dataChanged(true);
      }

    });

    cb = new JComboBox<Object>();
    renderer = new Renderer();
    cb.setRenderer(renderer);

    pan = new JPanel(new FlowLayout(FlowLayout.RIGHT));

  }

  @Override
  public Component getComponent(NodeData nodeData) {
    this.nodeData = nodeData;
    IAttributeAccessor aa = nodeData.getAttributeAccessor();
    if(!(aa instanceof ListAccessor)) {
      listAcc = null;
      return null;
    }
    listAcc = (ListAccessor) aa;

    updateOptions();

    pan.removeAll();
    if(options.size() > 1) {
      pan.add(cb);
    }
    pan.add(but);

    return pan;
  }

  private void updateOptions() {
    options.clear();

    Class<?> type = listAcc.getElementType();
    if(type == null) {
      return;
    }
    if(ITyped.class.isAssignableFrom(type)) {    //IType   
      @SuppressWarnings("unchecked")
      List<ITyped> vals = TypeRegister.INSTANCE.getTypesOfType((Class<ITyped>) type);
      if(vals != null) {
        options.addAll(vals);
      }
    } else if(type.isEnum()) {   //Enum
      if(type.getEnumConstants() != null) {
        options.addAll(Arrays.asList(type.getEnumConstants()));
      }
    } else {   //Primitives / Basic types
      try { 
        options.add(type.newInstance());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if(options.size() > 1) {
      cb.setModel(new DefaultComboBoxModel<Object>(options.toArray(new Object[options.size()])));
    }

  }

  private Object createNewInstance() {
    Object template = null;
    if(options.size() == 1) {
      template = options.get(0);
    } else if(options.size() > 1) {
      template = cb.getSelectedItem();
    }
    if(template == null) {
      return null;
    }
    updateOptions();
    return template;
  }
  
  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {      
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);      
      if(value instanceof ITyped) {
        setText(((ITyped)value).getType());
      }      
      return this; 
    }
    
  }

}
