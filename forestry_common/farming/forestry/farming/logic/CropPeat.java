/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class CropPeat extends Crop {

	public CropPeat(World world, Vect position) {
		super(world, position);
	}

	@Override
	protected boolean isCrop(Vect pos) {
		return getBlock(pos) == ForestryBlock.soil && (getBlockMeta(pos) & 0x03) == 1;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		list.add(ForestryItem.peat.getItemStack());

		Proxies.common.addBlockDestroyEffects(world, pos.x, pos.y, pos.z, world.getBlock(pos.x, pos.y, pos.z), 0);
		setBlock(pos, Blocks.dirt, 0);
		return list;
	}

}
