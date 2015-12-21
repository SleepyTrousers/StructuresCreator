package crazypants.structures.creator.block.tree;


import java.awt.Component;

import crazypants.structures.api.gen.IResource;
import crazypants.structures.creator.block.AbstractResourceTile;

public interface IAttributeEditor {

  Class<?> getType();
  
  Component getComponent(AbstractResourceTile tile, IResource resource, NodeData data);
  
}
