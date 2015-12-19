package crazypants.structures.creator.block.tree;

import java.util.HashMap;
import java.util.Map;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.creator.block.tree.editors.AddElementEditor;
import crazypants.structures.creator.block.tree.editors.BlockEditor;
import crazypants.structures.creator.block.tree.editors.BooleanEditor;
import crazypants.structures.creator.block.tree.editors.BorderEditor;
import crazypants.structures.creator.block.tree.editors.ComponentEditor;
import crazypants.structures.creator.block.tree.editors.EntityEditor;
import crazypants.structures.creator.block.tree.editors.IntegerEditor;
import crazypants.structures.creator.block.tree.editors.ItemStackEditor;
import crazypants.structures.creator.block.tree.editors.LootCategoryEditor;
import crazypants.structures.creator.block.tree.editors.Point3iEditor;
import crazypants.structures.creator.block.tree.editors.RotationEditor;
import crazypants.structures.creator.block.tree.editors.StringEditor;
import crazypants.structures.creator.block.tree.editors.TemplateEditor;
import crazypants.structures.creator.block.tree.editors.TextureResourceEditor;
import crazypants.structures.creator.block.tree.editors.TypedEditor;
import crazypants.structures.gen.structure.decorator.LootTableDecorator;
import crazypants.structures.gen.structure.loot.LootCategory;
import crazypants.structures.gen.villager.VillagerTemplate;
import crazypants.structures.runtime.behaviour.ResidentSpawner;
import crazypants.structures.runtime.behaviour.vspawner.VirtualSpawnerBehaviour;

public class AttributeEditors {

  public static final AttributeEditors INSTANCE = new AttributeEditors();

  private final Map<Class<?>, IAttributeEditor> editors = new HashMap<Class<?>, IAttributeEditor>();
  private final Map<IAttributeAccessor, IAttributeEditor> atrAditors = new HashMap<IAttributeAccessor, IAttributeEditor>();

  public void registerEditor(IAttributeEditor ed) {
    if(ed == null) {
      return;
    }
    editors.put(ed.getType(), ed);
  }
  
  public void registerEditor(IAttributeAccessor attribue, IAttributeEditor ed) {
    if(attribue == null) {
      return;
    }
    atrAditors.put(attribue, ed);
  }

  public IAttributeEditor getEditor(IAttributeAccessor attribue) {
    IAttributeEditor res = atrAditors.get(attribue);
    if(res == null) {
      res = getEditor(attribue.getType());
    }
    return res;
  }
  
  public IAttributeEditor getEditor(Class<?> type) {
    IAttributeEditor res = editors.get(type);
    if(res == null) {
      for (Class<?> cl : editors.keySet()) {
        if(cl.isAssignableFrom(type)) {
          //if(type.isInstance(cl)) {
          res = editors.get(cl);
          break;
        }
      }
      editors.put(type, res);
    }
    return res;
  }

  private AttributeEditors() {
    //Basic types
    registerEditor(new BooleanEditor());
    registerEditor(new IntegerEditor());
    registerEditor(new StringEditor());
    registerEditor(new Point3iEditor());
    registerEditor(new BorderEditor());
    registerEditor(new RotationEditor());
    registerEditor(new AddElementEditor());
    registerEditor(new BlockEditor());
    registerEditor(new ItemStackEditor());
    
    //Resources
    registerEditor(new ComponentEditor());
    registerEditor(new TemplateEditor());

    //Types
    registerEditor(new TypedEditor<ISitePreperation>(ISitePreperation.class));
    registerEditor(new TypedEditor<ISiteValidator>(ISiteValidator.class));
    registerEditor(new TypedEditor<IDecorator>(IDecorator.class));
    registerEditor(new TypedEditor<IBehaviour>(IBehaviour.class));
    registerEditor(new TypedEditor<IAction>(IAction.class));
    registerEditor(new TypedEditor<ICondition>(ICondition.class));
    registerEditor(new TypedEditor<IChunkValidator>(IChunkValidator.class));
    registerEditor(new TypedEditor<ILocationSampler>(ILocationSampler.class));
    
    //Specials
    IAttributeEditor ed = new LootCategoryEditor();
    FieldAccessor aa = new FieldAccessor(LootTableDecorator.class, String.class, "category");
    if(aa.isValid()) {      
      registerEditor(aa, ed);
    }    
    aa = new FieldAccessor(LootCategory.class, String.class, "category");
    if(aa.isValid()) {
      registerEditor(aa, ed);
    }  
    
    ed = new EntityEditor();
    aa = new FieldAccessor(ResidentSpawner.class, String.class, "entity");
    if(aa.isValid()) {
      registerEditor(aa, ed);
    } 
    aa = new FieldAccessor(VirtualSpawnerBehaviour.class, String.class, "entity");
    if(aa.isValid()) {
      registerEditor(aa, ed);
    } 
    ed = new TextureResourceEditor();
    aa = new FieldAccessor(VillagerTemplate.class, String.class, "texture");
    if(aa.isValid()) {
      registerEditor(aa, ed);
    } 
  }

}
