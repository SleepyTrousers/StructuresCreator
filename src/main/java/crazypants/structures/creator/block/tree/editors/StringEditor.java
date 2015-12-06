package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import crazypants.structures.creator.block.tree.NodeData;

public class StringEditor extends AbstractAttributeEditor {
  
  private JTextField tf = new JTextField(); 
  private JLabel label = new JLabel();
  private JPanel pan = new JPanel();
  private NodeData nd;
  
  public StringEditor() {
    this(String.class);    
  }
  
  protected StringEditor(Class<?> type) {
    super(type);
    DocumentListener updateListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent e) {
        setVal(tf.getText());
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        setVal(tf.getText());
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        setVal(tf.getText());
      }
    };
    tf.getDocument().addDocumentListener(updateListener);
    pan.setLayout(new GridBagLayout());
    pan.add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,6,1,0), 0, 0));
    pan.add(tf, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1,5,1,6), 0, 0));
  }
  
  @Override
  public Component getComponent(NodeData data) {
    nd = data;
    if(data == null) {
      return null;
    }    
    label.setText(nd.getLabel());
    tf.setText(getVal());      
    return pan;
  }
  
  protected void setVal(String tfTxt) {  
    if(nd == null) {
      return;
    }       
    if(tfTxt != null && tfTxt.trim().length() == 0) {
      tfTxt = null;
    }
    Object curVal = nd.getValue();
    if(curVal == null && tfTxt == null) {
      return;
    }
    if(tfTxt == null || !tfTxt.equals(curVal)) {
      nd.setValue(tfTxt);              
    }
  }

  protected String getVal() {
    return (String)nd.getValue();
  }

  public JTextField getTf() {
    return tf;
  }

  public void setTf(JTextField tf) {
    this.tf = tf;
  }

  public JLabel getLabel() {
    return label;
  }

  public void setLabel(JLabel label) {
    this.label = label;
  }

  public NodeData getNodeData() {
    return nd;
  }

  public void setNodeData(NodeData nd) {
    this.nd = nd;
  }
         
}