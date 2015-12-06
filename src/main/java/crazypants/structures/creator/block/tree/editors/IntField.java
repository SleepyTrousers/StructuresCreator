package crazypants.structures.creator.block.tree.editors;

import java.awt.Color;

import javax.swing.JTextField;

public class IntField extends JTextField {

  private static final long serialVersionUID = 1L;

  public IntField() {
    this(5);      
  }
  
  public IntField(int columns) {
    setColumns(columns);
  }

  public void setValue(Object val) {
    if(!(val instanceof Integer)) {
      val = null;        
    } 
    setText(val == null ? "" : val.toString());
  }
  
  public Integer getValue() {      
    try {
      String txt = getText();
      int val = Integer.parseInt(txt);        
      setBackground(Color.white);
      return val;
    } catch (Exception e) {        
      setBackground(IntegerEditor.ERROR_COL);
      return null;
    }
  }
}