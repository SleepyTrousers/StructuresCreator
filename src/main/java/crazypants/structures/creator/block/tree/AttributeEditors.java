package crazypants.structures.creator.block.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.creator.block.tree.editors.AddElementEditor;
import crazypants.structures.creator.block.tree.editors.BiomeNameEditor;
import crazypants.structures.creator.block.tree.editors.BlockEditor;
import crazypants.structures.creator.block.tree.editors.BooleanEditor;
import crazypants.structures.creator.block.tree.editors.BorderEditor;
import crazypants.structures.creator.block.tree.editors.ComponentEditor;
import crazypants.structures.creator.block.tree.editors.EntityEditor;
import crazypants.structures.creator.block.tree.editors.EnumEditor;
import crazypants.structures.creator.block.tree.editors.IntegerEditor;
import crazypants.structures.creator.block.tree.editors.ItemStackEditor;
import crazypants.structures.creator.block.tree.editors.LootCategoryEditor;
import crazypants.structures.creator.block.tree.editors.Point3iEditor;
import crazypants.structures.creator.block.tree.editors.StringEditor;
import crazypants.structures.creator.block.tree.editors.TaggedLocationEditor;
import crazypants.structures.creator.block.tree.editors.TemplateEditor;
import crazypants.structures.creator.block.tree.editors.TemplateNameEditor;
import crazypants.structures.creator.block.tree.editors.TextureResourceEditor;
import crazypants.structures.creator.block.tree.editors.TypedEditor;

public class AttributeEditors {

  public static final AttributeEditors INSTANCE = new AttributeEditors();

  private final Map<Class<?>, IAttributeEditor> typeEditors = new HashMap<Class<?>, IAttributeEditor>();
  private final Map<IAttributeAccessor, IAttributeEditor> attributeEditors = new HashMap<IAttributeAccessor, IAttributeEditor>();
  private final Map<String, IAttributeEditor> namedEditors = new HashMap<String, IAttributeEditor>();

  private final AddElementEditor addEditor = new AddElementEditor();
  
  public void registerEditor(IAttributeEditor ed) {
    if(ed == null) {
      return;
    }
    typeEditors.put(ed.getType(), ed);
  }
  
  public void registerEditor(IAttributeAccessor attribue, IAttributeEditor ed) {    
    if(attribue == null) {
      return;
    }
    attributeEditors.put(attribue, ed);
  }
  
  public void registerEditor(String name, IAttributeEditor ed) {    
    if(name == null) {
      return;
    }
    namedEditors.put(name, ed);
  }  

  public IAttributeEditor getEditor(IAttributeAccessor attribue) {
    if(attribue == null) {
      return null;
    }    
    
    if(List.class.isAssignableFrom(attribue.getType())) {
      //TODO: I really hate this special case
      //It is here as a named editor gets selected for this list, not just its
      //contents
      return addEditor;
    }
    
    //Search from most specific to least specific typing:
    //specific attribute binding, named editor binding, attribute type binding
    IAttributeEditor res = attributeEditors.get(attribue);
    if(res == null) {
      for (IAttributeAccessor registeredAcc : attributeEditors.keySet()) {
        if(isParent(registeredAcc, attribue)) {
          res = attributeEditors.get(registeredAcc);          
          break;
        }
      }
      if(res == null) {      
        if(attribue.getEditorType() != null) {
          res = namedEditors.get(attribue.getEditorType());  
        }
        if(res == null) {
          res = getEditor(attribue.getType());
        }
      }      
      //Always put the result in the attribute editors so we don't search again
      attributeEditors.put(attribue, res);            
    }
    return res;
  }
  
  private boolean isParent(IAttributeAccessor parent, IAttributeAccessor child) {
    if(parent == null || child == null) {
      return false;
    }
    if(parent.getType() != child.getType()) {
      return false;
    }
    if(!StringUtils.equals(parent.getAttribuiteName(), child.getAttribuiteName())) {
      return false;
    }
    if(parent.getDeclaringClass() != child.getDeclaringClass() && !parent.getDeclaringClass().isAssignableFrom(child.getDeclaringClass())) {
      return false;
    }       
    return true;
  }

  public IAttributeEditor getEditor(Class<?> type) {
    IAttributeEditor res = typeEditors.get(type);
    if(res == null) {
      for (Class<?> cl : typeEditors.keySet()) {
        if(cl.isAssignableFrom(type)) {
          res = typeEditors.get(cl);
          break;
        }
      }
      typeEditors.put(type, res);
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
    registerEditor(new EnumEditor());
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
    
    //Editors specified by AttributeEditor annotation        
    registerEditor("lootCategory", new LootCategoryEditor());    
    registerEditor("entity", new EntityEditor());
    registerEditor("texture", new TextureResourceEditor());
    registerEditor("taggedPosition", new TaggedLocationEditor());
    registerEditor("templateUid", new TemplateNameEditor());
    registerEditor("biomeName", new BiomeNameEditor());
    
  }

}
