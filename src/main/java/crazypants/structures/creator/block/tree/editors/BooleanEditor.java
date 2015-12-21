package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import crazypants.structures.api.gen.IResource;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.tree.NodeData;

public class BooleanEditor extends AbstractAttributeEditor {
  
  private JCheckBox cb = new JCheckBox(); 
  private NodeData nd;
  
  public BooleanEditor() {
    super(boolean.class);
    cb.setHorizontalTextPosition(SwingConstants.LEADING);
    cb.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        if(nd == null) {
          return;
        }
        if(cb.isSelected() != getVal()) {
          nd.setValue(cb.isSelected());
        }          
      }
    });
  }

  @Override
  public Component getComponent(AbstractResourceTile tile, IResource resource, NodeData data) {
    nd = data;
    if(data == null) {
      return null;
    }    
    cb.setText(nd.getLabel());
    cb.setSelected(getVal());      
    return cb;
  }
  
  private boolean getVal() {
    return ((Boolean)nd.getValue()).booleanValue();
  }
  
  
}