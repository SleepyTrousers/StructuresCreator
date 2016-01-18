package crazypants.structures.creator.item;

import java.util.Iterator;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.gen.StructureGenRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemTemplateTool extends Item {

  private static final String NAME = "itemTemplateTool";

  public static ItemTemplateTool create() {
    ItemTemplateTool res = new ItemTemplateTool();
    res.init();
    return res;
  }

  private ItemTemplateTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);    
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
        player.addChatComponentMessage(new ChatComponentText("Template set to " + uid));
      }
    }

    return super.onItemRightClick(stack, world, player);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {    
    if (world.isRemote) {
      return true;
    }

    String uid = getGenUid(stack, true);    
    if (uid != null) {
      buildComponent(world, pos, side, uid);
    }
    return true;
  }

  private void buildComponent(World world, BlockPos pos, EnumFacing side, String uid) {
    IStructureTemplate st = StructureGenRegister.instance.getStructureTemplate(uid, true);
    if(st != null) {
      BlockPos origin = pos.offset(side);
      origin = origin.down(st.getSurfaceOffset() + 1);        
            
      IStructure structure = st.createInstance();
      structure.setOrigin(new Point3i(origin));
      structure.build(world, world.rand, null);                  
    }
  }

  private String setNextUid(ItemStack stack) {
    String curUid = getGenUid(stack, false);
    if(curUid == null) {
      return setDefaultUid(stack);
    }
    Iterator<IStructureTemplate> it = StructureGenRegister.instance.getStructureTemplates().iterator();
    while (it.hasNext()) {
      IStructureTemplate template = it.next();
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
    NBTTagCompound stackTagCompound = stack.getTagCompound();
    if (stackTagCompound != null && stackTagCompound.hasKey("genUid")) {      
      result = stackTagCompound.getString("genUid");
    }
    if(setDefaultIfNull && result == null) {
      result = setDefaultUid(stack);
    }    
    return result;     
  }
  
  private void setGenUid(ItemStack stack, String uid) {
    NBTTagCompound stackTagCompound = stack.getTagCompound();
    if (stackTagCompound == null) {      
      stackTagCompound = new NBTTagCompound();
    }
    if(uid == null) {
      stackTagCompound.removeTag("genUid");
    } else {
      stackTagCompound.setString("genUid", uid);
    }
    stack.setTagCompound(stackTagCompound);
  }
  
  private String getFirstTemplateUid() {
    Iterator<IStructureComponent> it = StructureGenRegister.instance.getStructureComponents().iterator();
    if(it.hasNext()) {
      return it.next().getUid();
    }
    return null;
  }
  
}
