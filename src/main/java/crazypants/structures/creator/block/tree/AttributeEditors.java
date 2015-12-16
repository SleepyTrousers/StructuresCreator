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
import crazypants.structures.creator.block.tree.editors.IntegerEditor;
import crazypants.structures.creator.block.tree.editors.ItemStackEditor;
import crazypants.structures.creator.block.tree.editors.Point3iEditor;
import crazypants.structures.creator.block.tree.editors.RotationEditor;
import crazypants.structures.creator.block.tree.editors.StringEditor;
import crazypants.structures.creator.block.tree.editors.TemplateEditor;
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
  }

}
