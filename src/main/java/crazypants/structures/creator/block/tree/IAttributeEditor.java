package crazypants.structures.creator.block.tree;


import java.awt.Component;

import crazypants.structures.creator.block.AbstractResourceTile;

public interface IAttributeEditor {

  Class<?> getType();
  
  Component getComponent(AbstractResourceTile tile, NodeData data);
  
}
