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
import crazypants.structures.creator.block.tree.editors.AddEditor;
import crazypants.structures.creator.block.tree.editors.BooleanEditor;
import crazypants.structures.creator.block.tree.editors.BorderEditor;
import crazypants.structures.creator.block.tree.editors.ComponentEditor;
import crazypants.structures.creator.block.tree.editors.IntegerEditor;
import crazypants.structures.creator.block.tree.editors.Point3iEditor;
import crazypants.structures.creator.block.tree.editors.RotationEditor;
import crazypants.structures.creator.block.tree.editors.StringEditor;
import crazypants.structures.creator.block.tree.editors.TypedEditor;

public class AttributeEditors {

  public static final AttributeEditors INSTANCE = new AttributeEditors();
  
  private final Map<Class<?>, IAttributeEditor> editors = new HashMap<Class<?>, IAttributeEditor>();
  
  public void registerEditor(IAttributeEditor ed) {
    if(ed == null) {
      return;
    }
    editors.put(ed.getType(), ed);
  }
  
  public IAttributeEditor getEditor(Class<?> type) {
    IAttributeEditor res = editors.get(type);
    if(res == null) {
      for(Class<?> cl : editors.keySet()) {
        if(type.isInstance(cl)) {
          res = editors.get(cl);
        }
      }
    }    
    return res;
  }
  
  private AttributeEditors() {
    registerEditor(new BooleanEditor());
    registerEditor(new IntegerEditor());
    registerEditor(new StringEditor());
    registerEditor(new Point3iEditor());    
    registerEditor(new ComponentEditor());    
    registerEditor(new BorderEditor());
    registerEditor(new RotationEditor());
    registerEditor(new AddEditor());
    
    //TODO: Make the type checking go up the heirachy and I can just use one generic IType editor
    //for all these (and any other) types
    registerEditor(new TypedEditor<ISitePreperation>(ISitePreperation.class));
    registerEditor(new TypedEditor<ISiteValidator>(ISiteValidator.class));
    registerEditor(new TypedEditor<IDecorator>(IDecorator.class));
    registerEditor(new TypedEditor<IBehaviour>(IBehaviour.class));
    registerEditor(new TypedEditor<IAction>(IAction.class));
    registerEditor(new TypedEditor<ICondition>(ICondition.class));    
    registerEditor(new TypedEditor<IChunkValidator>(IChunkValidator.class));
    registerEditor(new TypedEditor<ILocationSampler>(ILocationSampler.class));
  }
  
  
}
