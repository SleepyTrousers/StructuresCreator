package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;

import crazypants.structures.creator.block.tree.ListElementAccessor;
import crazypants.structures.creator.block.tree.NodeData;
import crazypants.structures.creator.block.tree.StructuresTreeNode;

public class RemoveEditor {

  
  private final JButton but;
  private final JPanel pan;
  
  private ListElementAccessor lea;
  private NodeData nodeData;
  
  public RemoveEditor() {
    but = new JButton("Delete");
    but.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        if(lea == null || nodeData == null) {
          return;
        }
        lea.remove(nodeData.getOwner());
        //nodeData.getNode().dataChanged(true);
        TreeNode par = nodeData.getNode().getParent();
        if(par instanceof StructuresTreeNode) {
          StructuresTreeNode parNode = (StructuresTreeNode)par;
          parNode.removeAllChildren();          
          parNode.addChildren(nodeData.getOwner());          
          parNode.dataChanged(true);
        }
      }
    });
    
    pan = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    pan.add(but);
  }
  
  public Component getComponent(NodeData nodeData, ListElementAccessor lea) {
    this.nodeData = nodeData;    
    this.lea = lea;
    return pan;
  }
  
}
