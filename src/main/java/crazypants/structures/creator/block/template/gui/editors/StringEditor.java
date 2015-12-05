package crazypants.structures.creator.block.template.gui.editors;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import crazypants.structures.creator.block.template.gui.AbstractAttributeEditor;
import crazypants.structures.creator.block.template.gui.MyTreeNode.NodeData;

public class StringEditor extends AbstractAttributeEditor {
  
  private JTextField tf = new JTextField(); 
  private JLabel label = new JLabel();
  private JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT));
  private NodeData nd;
  
  public StringEditor() {
    super(String.class);
    DocumentListener updateListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent e) {
        setVal();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        setVal();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        setVal();
      }
    };
    tf.getDocument().addDocumentListener(updateListener);
    tf.setColumns(15);
    pan.add(label);
    pan.add(tf);
  }
  
  private void setVal() {  
    if(nd == null) {
      return;
    }
    Object curVal = nd.getValue();
    String curTxt = tf.getText();
    if(curVal == null && curTxt == null) {
      return;
    }
    if(curTxt == null || !curTxt.equals(curVal)) {
      nd.setValue(curTxt);              
    }
  }

  @Override
  public Component getComponent(NodeData data) {
    nd = data;
    tf.setColumns(15);
    if(data == null) {
      return null;
    }    
    label.setText(nd.getLabel());
    tf.setText(getVal());      
    return pan;
  }
  
  private String getVal() {
    return (String)nd.getValue();
  }

}