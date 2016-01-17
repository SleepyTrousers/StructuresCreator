package crazypants.structures.creator.item;

import java.util.Iterator;

import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.gen.StructureGenRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemComponentTool extends Item {

  private static final String NAME = "itemComponentTool";

  public static ItemComponentTool create() {
    ItemComponentTool res = new ItemComponentTool();
    res.init();
    return res;
  }

  private ItemComponentTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setTextureName(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

    if (!world.isRemote) {
      if (player.isSneaking()) {
        String uid = setNextUid(stack);
        player.addChatComponentMessage(new ChatComponentText("Component set to " + uid));
      }
    }

    return super.onItemRightClick(stack, world, player);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    
    if (world.isRemote) {
      return true;
    }

    String uid = getGenUid(stack, true);    
    if (uid != null) {
      IStructureComponent st = StructureGenRegister.instance.getStructureComponent(uid);
      if(st != null) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        Point3i origin = new Point3i(x + dir.offsetX, y + dir.offsetY - 1, z + dir.offsetZ);
        origin.y -= st.getSurfaceOffset();
        st.build(world, origin.x,origin.y,origin.z, Rotation.DEG_0, null);                    
      }
    }
    return true;
  }

  
  private String setNextUid(ItemStack stack) {
    String curUid = getGenUid(stack, false);
    if(curUid == null) {
      return setDefaultUid(stack);
    }
    Iterator<IStructureComponent> it = StructureGenRegister.instance.getStructureComponents().iterator();
    while (it.hasNext()) {
      IStructureComponent template = it.next();
      if (curUid.equals(template.getUid())) {
        if (it.hasNext()) {
          String uid = it.next().getUid();
          setGenUid(stack, uid);
          return uid;
        }
      }
    }        
    return setDefaultUid(stack);
  }

  private String setDefaultUid(ItemStack stack) {
    String uid =  getFirstTemplateUid();
    setGenUid(stack, uid);
    return uid;
  }

  private String getGenUid(ItemStack stack, boolean setDefaultIfNull) {   
    String result = null;
    if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("genUid")) {      
      result = stack.stackTagCompound.getString("genUid");
    }
    if(setDefaultIfNull && result == null) {
      result = setDefaultUid(stack);
    }    
    return result;     
  }
  
  private void setGenUid(ItemStack stack, String uid) {
    if (stack.stackTagCompound == null) {      
      stack.stackTagCompound = new NBTTagCompound();
    }
    if(uid == null) {
      stack.stackTagCompound.removeTag("genUid");
    } else {
      stack.stackTagCompound.setString("genUid", uid);
    }
  }
  
  private String getFirstTemplateUid() {
    Iterator<IStructureComponent> it = StructureGenRegister.instance.getStructureComponents().iterator();
    if(it.hasNext()) {
      return it.next().getUid();
    }
    return null;
  }

}
