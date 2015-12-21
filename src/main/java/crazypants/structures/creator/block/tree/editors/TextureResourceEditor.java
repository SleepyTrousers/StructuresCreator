package crazypants.structures.creator.block.tree.editors;

import java.io.File;
import java.io.FilenameFilter;

import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.gen.io.resource.ResourceModContainer;

public class TextureResourceEditor extends ComboEditor<String> {

  public TextureResourceEditor() {
    super(String.class);
    getComboBox().setEditable(true);
  }

  @Override
  protected String[] getValues() {    
    
    String resourcePrefix = ResourceModContainer.MODID + ":" ;
    String[] res = new String[] { resourcePrefix };
    AbstractResourceTile tile = getTile();
    if(tile == null || tile.getExportDir() == null) {
      return res;
    }
    
    
    File f = new File(tile.getExportDir());
    if(!f.exists() || !f.isDirectory()) {
      return res;
    }
    File[] pngs = f.listFiles(new FilenameFilter() {
      
      @Override
      public boolean accept(File dir, String name) { 
        return name.toLowerCase().endsWith(".png");
      }
    });
    
    if(pngs == null || pngs.length == 0) {
      return res;
    }
    
    res = new String[pngs.length + 1];
    res[0] = resourcePrefix;
    for(int i=0;i<pngs.length;i++) {
      String name = pngs[i].getName();
      //name = name.substring(0, name.length() - ".png".length());
      res[i+1] = resourcePrefix + name;
    }
    return res;    
  }

}
