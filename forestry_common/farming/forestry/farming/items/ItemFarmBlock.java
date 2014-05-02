/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import forestry.core.gadgets.IStructureBlockItem;
import forestry.core.utils.StringUtil;
import forestry.farming.gadgets.TileFarm.EnumFarmBlock;

public class ItemFarmBlock extends ItemBlock implements IStructureBlockItem {
	public ItemFarmBlock(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List info, boolean par4) {
		if (!itemstack.hasTagCompound())
			return;

		info.add(EnumFarmBlock.getFromCompound(itemstack.getTagCompound()).getName());
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return StringUtil.localize(getUnlocalizedName(itemstack));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return "tile.ffarm." + itemstack.getItemDamage();
	}
}
