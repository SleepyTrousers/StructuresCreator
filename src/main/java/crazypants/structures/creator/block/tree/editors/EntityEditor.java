package crazypants.structures.creator.block.tree.editors;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;

public class EntityEditor extends ComboEditor<String> {

  public EntityEditor() {
    super(String.class);
    getComboBox().setEditable(true);   
  }

  @Override
  protected String[] getValues() {    
    List<String> res = new ArrayList<String>();
    for(Object str : EntityList.stringToClassMapping.keySet()) {
      if(EntityLiving.class.isAssignableFrom(EntityList.stringToClassMapping.get(str).getClass())) {
        res.add(str.toString());
      }
    }
    return res.toArray(new String[res.size()]);
  }

}
