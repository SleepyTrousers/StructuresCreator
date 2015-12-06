package crazypants.structures.creator.block.tree;


import java.awt.Component;

public interface IAttributeEditor {

  Class<?> getType();
  
  Component getComponent(NodeData data);
  
}
