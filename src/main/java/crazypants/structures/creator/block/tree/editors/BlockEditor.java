package crazypants.structures.creator.block.tree.editors;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

public class BlockEditor extends ComboEditor<Block> {

  public BlockEditor() {
    super(Block.class);
    getComboBox().setRenderer(new Renderer());
    getComboBox().setEditable(true);
    getComboBox().setEditor(new Editor());
  }

  @Override
  protected Block[] getValues() {
    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    InventoryPlayer inv = player.inventory;
    if(inv == null || inv.mainInventory == null || inv.mainInventory.length < 9) {
      return new Block[0];
    }
    List<Block> res = new ArrayList<Block>();
    for (int i = 0; i < 9; i++) {
      ItemStack item = inv.mainInventory[i];
      if(item != null) {
        Block blk = Block.getBlockFromItem(item.getItem());
        if(blk != null) {
          res.add(blk);
        }
      }
    }
    return res.toArray(new Block[res.size()]);
  }

  private static String getTextForBlock(Block block) {
    UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(block);
    if(uid == null) {
      return "Unknown Block";
    } else {
      return uid.toString();
    }
  }

  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if(value instanceof Block) {
        setText(getTextForBlock((Block) value));
      }
      return this;
    }

  }

  private static final class Editor extends BasicComboBoxEditor {

    private Editor() {      
      DocumentListener listener = new DocumentListener() {
        
        @Override
        public void removeUpdate(DocumentEvent e) {
          onChange();          
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
          onChange();          
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
          onChange();          
        }

      };
      editor.getDocument().addDocumentListener(listener);
    }
    
    private void onChange() {
      if(editor == null) {
        return;
      }
      if(getItem() == null) {
        editor.setBackground(IntegerEditor.ERROR_COL);
      } else {
        editor.setBackground(Color.white);
      }      
    }
    
    @Override
    public void setItem(Object anObject) {
      if(anObject instanceof Block) {
        anObject = getTextForBlock((Block) anObject);
      }
      super.setItem(anObject);
    }

    @Override
    public Object getItem() {      
      String text = editor.getText();
      if(text == null) {
        return null;
      }
      try {
        UniqueIdentifier uid = new UniqueIdentifier(text);
        if(uid.modId == null || uid.name == null) {
          return null;
        }
        return GameRegistry.findBlock(uid.modId, uid.name);
      } catch (Exception e) {
        return null;
      }
    }
  }
  
  

}
