package crazypants.structures.creator.endercore.api.client.gui;

import net.minecraft.item.ItemStack;

public interface IResourceTooltipProvider {

    String getUnlocalizedNameForTooltip(ItemStack itemStack);

}
