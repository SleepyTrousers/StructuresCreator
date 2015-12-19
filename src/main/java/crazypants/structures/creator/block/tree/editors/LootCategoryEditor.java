package crazypants.structures.creator.block.tree.editors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.ChestGenHooks;

public class LootCategoryEditor extends ComboEditor<String> {

  private final Field field;

  public LootCategoryEditor() {
    super(String.class);
    getComboBox().setEditable(true);

    Field f;
    try {
      f = ChestGenHooks.class.getDeclaredField("chestInfo");
      f.setAccessible(true);
    } catch (Exception e) {
      e.printStackTrace();
      f = null;
    }
    field = f;

  }

  @Override
  protected String[] getValues() {

    if(field == null) {
      return new String[0];
    }
    List<String> res = new ArrayList<String>();
    try {

      Object val = field.get(null);
      if(val instanceof Map<?, ?>) {
        Set<?> cats = ((Map<?, ?>) val).keySet();
        for (Object o : cats) {
          if(o != null) {
            res.add(o.toString());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return res.toArray(new String[res.size()]);
  }

}
