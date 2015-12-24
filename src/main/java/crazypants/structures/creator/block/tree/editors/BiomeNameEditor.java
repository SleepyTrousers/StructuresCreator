package crazypants.structures.creator.block.tree.editors;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeNameEditor extends ComboEditor<String> {

  private String[] biomeNames;

  public BiomeNameEditor() {
    super(String.class);

    Set<String> names = new HashSet<String>();
    names.add(null);    
    for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
      if(biome != null && biome.biomeName != null) {
        names.add(biome.biomeName);
      }
    }
    biomeNames = names.toArray(new String[names.size()]);

  }

  @Override
  protected String[] getValues() {
    return biomeNames;
  }

}
