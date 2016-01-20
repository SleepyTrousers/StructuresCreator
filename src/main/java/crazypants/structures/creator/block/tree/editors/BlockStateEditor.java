package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;

public class BlockStateEditor extends ComboEditor<IBlockState> {

  public BlockStateEditor() {
    super(IBlockState.class);
    getComboBox().setRenderer(new Renderer());
  }

  @Override
  protected IBlockState[] getValues() {
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    InventoryPlayer inv = player.inventory;
    if (inv == null || inv.mainInventory == null || inv.mainInventory.length < 9) {
      return new IBlockState[0];
    }
    List<IBlockState> res = new ArrayList<IBlockState>();
    for (int i = 0; i < 9; i++) {
      ItemStack item = inv.mainInventory[i];
      if (item != null) {
        Block blk = Block.getBlockFromItem(item.getItem());
        if (blk != null) {
          IBlockState bs = blk.getStateFromMeta(item.getMetadata());
          if (bs != null) {
            res.add(bs);
          }
        }
      }
    }
    return res.toArray(new IBlockState[res.size()]);
  }

  private static String getTextForBlock(IBlockState blockState) {
    if (blockState == null) {
      return "";
    }
    Block blk = blockState.getBlock();
    Item item = GameData.getBlockItemMap().get(blk);
    if(item == null) {
      return "";
    }    
    ItemStack is = new ItemStack(blk, 1, blk.getMetaFromState(blockState));    
    return is.getDisplayName();    
  }

  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof IBlockState) {
        setText(getTextForBlock((IBlockState) value));
      }
      return this;
    }

  }

}
