package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import crazypants.structures.api.gen.IResource;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.WeightedTemplate;
import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.tree.NodeData;
import crazypants.structures.gen.structure.StructureGenerator;
import crazypants.structures.gen.villager.VillagerTemplate;

public class TaggedLocationEditor extends ComboEditor<String> {

  public TaggedLocationEditor() {
    super(String.class);
  }

  @Override
  public Component getComponent(AbstractResourceTile tile, IResource resource, NodeData data) {      
    return super.getComponent(tile, resource, data);
    //return new JLabel("Wtoota");
  }
  
  @Override
  protected String[] getValues() {
    
    if(resource == null) {
      return new String[] {null};
    }
    Set<String> res = new HashSet<String>();
    res.add(null);
    if(resource instanceof IStructureTemplate) {
      IStructureTemplate tmp = (IStructureTemplate)resource;
      addsTagsForTemplate(res, tmp);      
    } else if(resource instanceof StructureGenerator) {
      StructureGenerator gen = (StructureGenerator)resource;            
      addsTagsForTemplates(res, gen.getTemplates());
    } else if(resource instanceof VillagerTemplate) {
      VillagerTemplate vt = (VillagerTemplate)resource;
      addsTagsForTemplates(res, vt.getPlainsTemplates());
      addsTagsForTemplates(res, vt.getDesertTemplates());      
    }
    return res.toArray(new String[res.size()]);
  }

  private void addsTagsForTemplates(Set<String> res, List<WeightedTemplate> templates) {
    if(templates != null) {
      for(WeightedTemplate tmpl : templates) {
        addsTagsForTemplate(res, tmpl);
      }
    }
  }

  private void addsTagsForTemplate(Set<String> res, WeightedTemplate tmp) {
    if(tmp == null) {
      return;
    }
    addsTagsForTemplate(res, tmp.getTemplate());    
  }
  
  private void addsTagsForTemplate(Set<String> res, IStructureTemplate tmp) {
    if(tmp == null) {
      return;
    }
    List<String> tags = tmp.getLocationTags();
    if(tags != null) {
      res.addAll(tags);
    }
  }

}
