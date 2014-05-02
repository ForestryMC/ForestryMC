/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.core.utils.StackUtils;

public class JubilanceReqRes implements IJubilanceProvider {

	private final ItemStack blockRequired;

	public JubilanceReqRes(ItemStack blockRequired) {
		this.blockRequired = blockRequired;
	}

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {

		if (blockRequired == null)
			return true;

		Block block = housing.getWorld().getBlock(housing.getXCoord(), housing.getYCoord() - 1, housing.getZCoord());
		int meta = housing.getWorld().getBlockMetadata(housing.getXCoord(), housing.getYCoord() - 1, housing.getZCoord());
		if (StackUtils.equals(block, blockRequired) && meta == blockRequired.getItemDamage())
			return true;
		else
			return false;
	}

}
