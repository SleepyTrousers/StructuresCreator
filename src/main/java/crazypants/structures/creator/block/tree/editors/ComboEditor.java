package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;

import crazypants.structures.creator.block.tree.NodeData;

public abstract class ComboEditor<T> extends AbstractAttributeEditor {

  private final JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT));
  private final JLabel label = new JLabel();
  private final JComboBox<T> cb = new JComboBox<T>();
  private NodeData data;
  
  private boolean ignoreUpdate = false;

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
    updateComboModel(data);    
    
    ignoreUpdate = true;    
    //cb.setSelectedItem(data.getValue());
    ignoreUpdate = false;
    
    label.setText(data.getLabel());    
    return pan;
  }

  protected void updateComboModel(NodeData data) {
    DefaultComboBoxModel<T> model;    
    T[] values = getValues();
    if(values == null) {
      model = new DefaultComboBoxModel<T>();
    } else {
      model = new DefaultComboBoxModel<T>(values);
    }    
    @SuppressWarnings("unchecked")
    T val = (T)data.getValue();
    if(!ArrayUtils.contains(values, val)) {
      model.insertElementAt(val, 0);
    }
    ignoreUpdate = true;
    cb.setModel(model);
    cb.setSelectedItem(data.getValue());
    ignoreUpdate = false;
  }

  protected abstract T[] getValues();

  protected void onValueChanged() {
    if(ignoreUpdate) {
      return;
    }
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
