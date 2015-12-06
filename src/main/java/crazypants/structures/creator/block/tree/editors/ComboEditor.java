package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import crazypants.structures.creator.block.tree.NodeData;

public abstract class ComboEditor<T> extends AbstractAttributeEditor {

  private final JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT));
  private final JLabel label = new JLabel();
  private final JComboBox<T> cb = new JComboBox<T>();
  private NodeData data;

  protected ComboEditor(Class<T> type) {
    super(type);

    cb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onValueChanged();
      }
    });
    pan.add(label);
    pan.add(cb);
  }

  @Override
  public Component getComponent(NodeData data) {
    this.data = data;
    
    DefaultComboBoxModel<T> model;    
    T[] values = getValues();
    if(values == null) {
      model = new DefaultComboBoxModel<T>();
    } else {
      model = new DefaultComboBoxModel<T>(values);
    }    
    cb.setModel(model);
    cb.setSelectedItem(data.getValue());
    
    label.setText(data.getLabel());
    
    return pan;
  }

  protected abstract T[] getValues();

  protected void onValueChanged() {
    data.setValue(cb.getSelectedItem());
  }
  
  public JComboBox<T> getComboBox() {
    return cb;
  }
  
  public JLabel getLabel() {
    return label;
  }
  
  public NodeData getNodeData() {
    return data;
  }
  
  public void setNodeData(NodeData data) {
    this.data = data;    
  }

  public Component getRootComponent() {   
    return pan;
  }


}
