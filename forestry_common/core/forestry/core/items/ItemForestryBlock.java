/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import forestry.core.utils.StringUtil;

public class ItemForestryBlock extends ItemBlock {


	public ItemForestryBlock(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	protected Block getBlock() {
		return field_150939_a;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getUnlocalizedName(itemstack));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getBlock().getUnlocalizedName() + "." + itemstack.getItemDamage();
	}

}
