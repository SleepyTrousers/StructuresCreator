package crazypants.structures.creator.block.template.gui;


import java.awt.Component;

import crazypants.structures.creator.block.template.gui.MyTreeNode.NodeData;

public interface IAttributeEditor {

  Class<?> getType();
  
  Component getComponent(NodeData data);
  
}
