package crazypants.structures.creator.block.tree.editors;

import java.util.ArrayList;
import java.util.List;

import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.StructureGenRegister;

public class TemplateNameEditor extends ComboEditor<String> {

  public TemplateNameEditor() {
    super(String.class);
  }

  @Override
  protected String[] getValues() {
    List<String> res = new ArrayList<String>();
    for(IStructureTemplate template : StructureGenRegister.instance.getStructureTemplates()) {
      if(template != null && template.getUid() != null) {
        res.add(template.getUid());
      }
    }
    return res.toArray(new String[res.size()]);
  }
  
}
