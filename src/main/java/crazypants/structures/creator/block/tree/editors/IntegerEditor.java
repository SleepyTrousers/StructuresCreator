package crazypants.structures.creator.block.tree.editors;

import java.awt.Color;
import java.awt.Component;

import crazypants.structures.api.gen.IResource;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.tree.NodeData;

public class IntegerEditor extends StringEditor {

  public static final Color ERROR_COL = new Color(1f,0.7f,0.7f);
  private Color defCol;

  public IntegerEditor() {
    super(int.class);
    defCol = getTf().getBackground();
  }

  @Override
  public Component getComponent(AbstractResourceTile tile, IResource resource, NodeData data) {
    Component res = super.getComponent(tile, resource, data);
    getTf().setBackground(defCol);
    return res;
  }

  @Override
  protected void setVal(String tfTxt) {    
    if(getNodeData() == null) {
      return;
    }
    if(tfTxt != null && tfTxt.trim().length() == 0) {
      tfTxt = null;
    }
    Object curVal = getNodeData().getValue();
    if(curVal == null && tfTxt == null) {
      return;
    }

    try {
      int tfVal = Integer.parseInt(tfTxt);
      getNodeData().setValue(tfVal);
      getTf().setBackground(defCol);
    } catch (Exception e) {
      getTf().setBackground(ERROR_COL);      
    }    
  }
  
  @Override
  protected String getVal() {
    if(getNodeData() == null || getNodeData().getValue() == null) {
      return "";
    }
    return getNodeData().getValue().toString();
  }
  
}
