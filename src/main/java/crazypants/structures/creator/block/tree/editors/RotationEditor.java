package crazypants.structures.creator.block.tree.editors;

import crazypants.structures.api.util.Rotation;

public class RotationEditor extends ComboEditor<Rotation> {

  public RotationEditor() {
    super(Rotation.class);
  }

  @Override
  protected Rotation[] getValues() {    
    return Rotation.values();
  }

}
