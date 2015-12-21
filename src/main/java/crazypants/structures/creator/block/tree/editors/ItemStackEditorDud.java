package crazypants.structures.creator.block.tree.editors;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.ArrayUtils;

import crazypants.structures.creator.block.AbstractResourceTile;
import crazypants.structures.creator.block.tree.NodeData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackEditorDud extends AbstractAttributeEditor {

  private JPanel pan = new JPanel();
  private final JLabel label = new JLabel();
  private final JComboBox<Item> cb = new JComboBox<Item>();
  
  private final InnerTF sizeTF = new InnerTF();
  private final InnerTF metaTF = new InnerTF();

  private boolean ignoreUpdates = false;

  private NodeData data;

  public ItemStackEditorDud() {
    super(ItemStack.class);

    cb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onInputChanged();
      }

    });
    
    
    
    JPanel itemPan = new JPanel(new FlowLayout());
    itemPan.add(label);
    itemPan.add(cb);
    
    pan.setLayout(new GridBagLayout());
    Insets insets = new Insets(1,1,1,1);
    int x=0;
    int y=0;
    pan.add(itemPan, new GridBagConstraints(x,y,4,1,1,1,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,insets,0,0));
    y++;
    pan.add(new JLabel("Size:"), new GridBagConstraints(x,y,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,insets,0,0));
    x++;
    pan.add(sizeTF, new GridBagConstraints(x,y,1,1,1,1,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,insets,0,0));
    x++;
    pan.add(new JLabel("Meta:"), new GridBagConstraints(x,y,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,insets,0,0));
    x++;
    pan.add(metaTF, new GridBagConstraints(x,y,1,1,1,1,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL,insets,0,0));

  }

  @Override
  public Component getComponent(AbstractResourceTile tile, NodeData data) {
    this.data = data;

    DefaultComboBoxModel<Item> model;
    Item[] values = getItemsInHotbar();
    if(values == null) {
      model = new DefaultComboBoxModel<Item>();
    } else {
      model = new DefaultComboBoxModel<Item>(values);
    }
    ItemStack is = (ItemStack) data.getValue();
        
    if(is == null) {
      model.insertElementAt(null, 0);
      sizeTF.setText("1");
      metaTF.setText("0");
    } else {
      if(!ArrayUtils.contains(values, is.getItem())) {
        model.insertElementAt(is.getItem(), 0);
      }
      sizeTF.setText(is.stackSize + "");
      metaTF.setText(is.getItemDamage() + "");      
    }
    ignoreUpdates = true;
    cb.setModel(model);
    cb.setSelectedItem(is);
    ignoreUpdates = false;

    label.setText(data.getLabel());

    return pan;
  }

  private Item[] getItemsInHotbar() {
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    InventoryPlayer inv = player.inventory;
    if(inv == null || inv.mainInventory == null || inv.mainInventory.length < 9) {
      return new Item[0];
    }
    List<Item> res = new ArrayList<Item>();
    for (int i = 0; i < 9; i++) {
      ItemStack item = inv.mainInventory[i];
      if(item != null && item.getItem() != null) {
        res.add(item.getItem());
      }
    }
    return res.toArray(new Item[res.size()]);
  }

  private void onInputChanged() {
    if(ignoreUpdates) {
      return;
    }    
    data.setValue(getEditorValue());
  }

  private ItemStack getEditorValue() {
   Item item = (Item)cb.getSelectedItem();
   if(item == null) {
     return null;
   }
   Integer size = sizeTF.getValue();
   Integer meta = metaTF.getValue();   
   if(size == null || meta == null) {
     return null;
   }
   return new ItemStack(item, size, meta);       
  }
  
  private class InnerTF extends IntField {
    
    private static final long serialVersionUID = 1L;

    InnerTF() {
      getDocument().addDocumentListener(new DocumentListener() {
        
        @Override
        public void removeUpdate(DocumentEvent e) {
          onInputChanged();
          
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
          onInputChanged();
          
        }
        
        @Override
        public void changedUpdate(DocumentEvent e) {
          onInputChanged();          
        }
      });
      setColumns(4);
    }
    
  }
  
  private static class Renderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if(value instanceof Item) {
//        ItemStack
//        setText( ((Item) value).getN));
      }
      return this;
    }

  }


}
