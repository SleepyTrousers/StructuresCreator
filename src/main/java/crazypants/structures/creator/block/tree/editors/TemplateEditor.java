package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.util.Collection;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.StructureGenRegister;

public class TemplateEditor extends ComboEditor<IStructureTemplate> {

  public TemplateEditor() {
    super(IStructureTemplate.class);
    getComboBox().setRenderer(new Renderer());
  }

  @Override
  protected IStructureTemplate[] getValues() {
    Collection<IStructureTemplate> curComps = StructureGenRegister.instance.getStructureTemplates();
    if(curComps == null) {
      return null;
    }
    return curComps.toArray(new IStructureTemplate[curComps.size()]);
  }

  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if(value instanceof IStructureTemplate) {
        setText(((IStructureTemplate) value).getUid());
      }
      return this;
    }

  }


}
