package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.util.Collection;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.gen.StructureGenRegister;

public class ComponentEditor extends ComboEditor<IStructureComponent> {

  public ComponentEditor() {
    super(IStructureComponent.class);
    getComboBox().setRenderer(new Renderer());
  }

  @Override
  protected IStructureComponent[] getValues() {
    Collection<IStructureComponent> curComps = StructureGenRegister.instance.getStructureComponents();
    if(curComps == null) {
      return null;
    }
    return curComps.toArray(new IStructureComponent[curComps.size()]);
  }

  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if(value instanceof IStructureComponent) {
        setText(((IStructureComponent) value).getUid());
      }
      return this;
    }

  }

}
