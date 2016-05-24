package forestry.energy.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.ItemTooltipUtil;
import forestry.energy.blocks.BlockEngine;

public class ItemEngine extends ItemBlockForestry<BlockEngine> {

	public ItemEngine(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		ItemTooltipUtil.addInformation(stack, playerIn, tooltip, advanced);
	}
}
