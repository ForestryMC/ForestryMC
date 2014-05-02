/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.core.items.ItemForestryBlock;
import forestry.core.utils.StringUtil;

public class ItemWoodBlock extends ItemForestryBlock {

	public ItemWoodBlock(Block block) {
		super(block);
	}

	public static int getTypeFromMeta(int damage) {
		return damage & 3;
	}

	private String getWoodNameIS(WoodType type) {
		return StringUtil.localize("wood." + type.ordinal());
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {

		if (this.getBlock() instanceof IWoodTyped) {
			IWoodTyped block = (IWoodTyped) getBlock();
			return getWoodNameIS(block.getWoodType(itemstack.getItemDamage())) + " " + StringUtil.localize("tile." + block.getBlockKind());
		} else
			return StringUtil.localize(getUnlocalizedName(itemstack));
	}

}
