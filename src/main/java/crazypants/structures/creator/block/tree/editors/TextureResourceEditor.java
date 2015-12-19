package crazypants.structures.creator.block.tree.editors;

import crazypants.structures.gen.io.resource.ResourceModContainer;

public class TextureResourceEditor extends ComboEditor<String> {

  public TextureResourceEditor() {
    super(String.class);
    getComboBox().setEditable(true);   
  }

  @Override
  protected String[] getValues() {        
    return new String[] { ResourceModContainer.MODID + ":" };     
  }

}
