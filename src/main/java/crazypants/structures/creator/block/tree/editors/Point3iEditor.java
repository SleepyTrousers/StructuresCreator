package crazypants.structures.creator.block.tree.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import crazypants.structures.api.gen.IResource;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.tree.NodeData;

public class Point3iEditor extends AbstractAttributeEditor {

  private IntField xf = new IntField();
  private IntField yf = new IntField();
  private IntField zf = new IntField();
  
  private JPanel pan = new JPanel();
  
  private boolean ignoreUpdates = false;
  
  private NodeData data;
  
  public Point3iEditor() {
    super(Point3i.class);
    
    pan.setLayout(new GridBagLayout());
    Insets insets = new Insets(5,5,1,1);
    pan.add(new JLabel("x:"), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(xf, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    
    pan.add(new JLabel("y:"), new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(yf, new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    
    pan.add(new JLabel("z:"), new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(zf, new GridBagConstraints(5,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    
    insets.set(0, 0, 0, 0);
    pan.add(new JPanel(), new GridBagConstraints(6,0,1,1,1,1,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,insets,0,0));
    
    DocumentListener updateListener = new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent e) {
        inputChanged();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        inputChanged();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        inputChanged();
      }
    };
    xf.getDocument().addDocumentListener(updateListener);
    yf.getDocument().addDocumentListener(updateListener);
    zf.getDocument().addDocumentListener(updateListener);
  }

  @Override
  public Component getComponent(AbstractResourceTile tile, IResource resource, NodeData data) {      
    this.data = data;
    
    ignoreUpdates = true;
    xf.setValue(null);
    yf.setValue(null);
    zf.setValue(null);
    xf.setBackground(IntegerEditor.ERROR_COL);
    yf.setBackground(IntegerEditor.ERROR_COL);
    zf.setBackground(IntegerEditor.ERROR_COL);
    
    
    Object val = data.getValue();
    if(val instanceof Point3i) {
      Point3i p = (Point3i)val;
      xf.setText(p.x + "");
      yf.setText(p.y + "");
      zf.setText(p.z + "");
      xf.setBackground(Color.white);
      yf.setBackground(Color.white);
      zf.setBackground(Color.white);
    }
    
    ignoreUpdates = false;
    return pan;
  }
  
  public Point3i getValue() {
    Integer x = xf.getValue();
    Integer y = yf.getValue();
    Integer z = zf.getValue();
    if(x == null || y == null || z == null) {
      return null;
    }
    return new Point3i(x,y,z);
  }
  
  private void inputChanged() {
    if(ignoreUpdates) {
      return;
    }
    Point3i inputVal = getValue();
    Object curVal = data.getValue();
    if(curVal == null && inputVal == null) {
      return;
    }
    if(inputVal == null || !inputVal.equals(curVal)) {
      data.setValue(inputVal);              
    }
  }

}
