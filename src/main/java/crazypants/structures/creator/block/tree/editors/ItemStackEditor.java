package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ItemStackEditor extends ComboEditor<ItemStack> {

  public ItemStackEditor() {
    super(ItemStack.class);
    getComboBox().setRenderer(new Renderer());
    getComboBox().addPopupMenuListener(new PopupMenuListener() {
      
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        updateComboModel(getNodeData());
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}      
      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {}
    });
  }

  @Override
  protected ItemStack[] getValues() {
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    InventoryPlayer inv = player.inventory;
    if(inv == null || inv.mainInventory == null || inv.mainInventory.length < 9) {
      return new ItemStack[] { null };
    }
    List<ItemStack> res = new ArrayList<ItemStack>();
    res.add(null);
    for (int i = 0; i < 9; i++) {
      ItemStack item = inv.mainInventory[i];
      if(item != null) {
        res.add(item);
      }
    }
    return res.toArray(new ItemStack[res.size()]);
  }
 
 
  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if(value instanceof ItemStack) {
        ItemStack is = (ItemStack) value;
        setText(is.stackSize + " " + is.getDisplayName());
      } else {
        setText("   ");
      }
      return this;
    }

  }

}
