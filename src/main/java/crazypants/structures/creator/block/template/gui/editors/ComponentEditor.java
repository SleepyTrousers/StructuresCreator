package crazypants.structures.creator.block.template.gui.editors;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.creator.block.template.gui.AbstractAttributeEditor;
import crazypants.structures.creator.block.template.gui.MyTreeNode.NodeData;
import crazypants.structures.gen.StructureGenRegister;

public class ComponentEditor extends AbstractAttributeEditor {

  private final JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT));
  private final JLabel label = new JLabel();
  private final JComboBox<IStructureComponent> cb = new JComboBox<IStructureComponent>();
  private NodeData data;
  
  public ComponentEditor() {
    super(IStructureComponent.class);
    cb.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        setVal();        
      }

      
    });
    cb.setRenderer(new Renderer());
    pan.add(label);
    pan.add(cb);
  }

  private void setVal() {
    data.setValue(cb.getSelectedItem());
  }
  
  @Override
  public Component getComponent(NodeData data) {
    this.data = data;
        
    Collection<IStructureComponent> curComps = StructureGenRegister.instance.getStructureComponents();
    if(curComps == null) {
      return null;
    }
    DefaultComboBoxModel<IStructureComponent> model = new DefaultComboBoxModel<IStructureComponent>(curComps.toArray(new IStructureComponent[curComps.size()]));
    cb.setModel(model);
    cb.setSelectedItem(data.getValue());
    
    label.setText(data.getLabel());
    
    return pan;
  }
  
  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);      
      if(value instanceof IStructureComponent) {
        setText(((IStructureComponent)value).getUid());
      }      
      return this;
    }
    
  }
  
}
