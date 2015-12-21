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
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.tree.NodeData;
import crazypants.structures.gen.structure.Border;
import net.minecraftforge.common.util.ForgeDirection;

public class BorderEditor extends AbstractAttributeEditor {
  
  private IntField nf = new IntField(3);
  private IntField sf = new IntField(3);
  private IntField ef = new IntField(3);
  private IntField wf = new IntField(3);
  private IntField uf = new IntField(3);
  private IntField df = new IntField(3);
  
  private JPanel pan = new JPanel();
  
  private boolean ignoreUpdates = false;
  
  private NodeData data;
  
  public BorderEditor() {
    super(Border.class);
        
    pan.setLayout(new GridBagLayout());
    Insets insets = new Insets(1,1,1,1);
    pan.add(new JLabel("n:"), new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(nf, new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.HORIZONTAL,insets,0,0));
    
    pan.add(new JLabel("s:"), new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(sf, new GridBagConstraints(3,1,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.HORIZONTAL,insets,0,0));
    
    pan.add(new JLabel("e:"), new GridBagConstraints(4,0,1,2,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(ef, new GridBagConstraints(5,0,1,2,0,0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,insets,0,0));
    
    pan.add(new JLabel("w:"), new GridBagConstraints(0,0,1,2,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(wf, new GridBagConstraints(1,0,1,2,0,0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,insets,0,0));
        
    pan.add(new JLabel("u:"), new GridBagConstraints(6,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(uf, new GridBagConstraints(7,0,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.HORIZONTAL,insets,0,0));
    
    pan.add(new JLabel("d:"), new GridBagConstraints(6,1,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,insets,0,0));
    pan.add(df, new GridBagConstraints(7,1,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.HORIZONTAL,insets,0,0));
    
    
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
    nf.getDocument().addDocumentListener(updateListener);
    sf.getDocument().addDocumentListener(updateListener);
    ef.getDocument().addDocumentListener(updateListener);
    wf.getDocument().addDocumentListener(updateListener);
    uf.getDocument().addDocumentListener(updateListener);
    df.getDocument().addDocumentListener(updateListener);
  }

  @Override
  public Component getComponent(AbstractResourceTile tile, IResource resource, NodeData data) {
    this.data = data;
    
    ignoreUpdates = true;
    nf.setValue(null);
    sf.setValue(null);
    ef.setValue(null);
    wf.setValue(null);
    uf.setValue(null);
    df.setValue(null);
    nf.setBackground(IntegerEditor.ERROR_COL);
    sf.setBackground(IntegerEditor.ERROR_COL);
    ef.setBackground(IntegerEditor.ERROR_COL);
    wf.setBackground(IntegerEditor.ERROR_COL);
    uf.setBackground(IntegerEditor.ERROR_COL);
    df.setBackground(IntegerEditor.ERROR_COL);
        
    Object val = data.getValue();
    if(val instanceof Border) {
      Border b = (Border)val;
      nf.setText(b.get(ForgeDirection.NORTH) + "");
      sf.setText(b.get(ForgeDirection.SOUTH) + "");
      ef.setText(b.get(ForgeDirection.EAST) + "");
      wf.setText(b.get(ForgeDirection.WEST) + "");
      uf.setText(b.get(ForgeDirection.UP) + "");
      df.setText(b.get(ForgeDirection.DOWN) + "");
      nf.setBackground(Color.white);
      sf.setBackground(Color.white);
      ef.setBackground(Color.white);
      wf.setBackground(Color.white);
      uf.setBackground(Color.white);
      df.setBackground(Color.white);
    }
    
    ignoreUpdates = false;
    
    return pan;
  }
  
  public Border getValue() {
    Integer n = nf.getValue();
    Integer s = sf.getValue();
    Integer e = ef.getValue();
    Integer w = wf.getValue();
    Integer u = uf.getValue();
    Integer d = df.getValue();
    if(n == null || s == null || e == null || w == null || u == null || d == null) {
      return null;
    }    
    return new Border(n,s,e,w,u,d);
  }
  
  private void inputChanged() {
    if(ignoreUpdates) {
      return;
    }
    Border inputVal = getValue();
    Object curVal = data.getValue();
    if(curVal == null && inputVal == null) {
      return;
    }
    if(inputVal == null || !inputVal.equals(curVal)) {
      data.setValue(inputVal);              
    }
  }

}
