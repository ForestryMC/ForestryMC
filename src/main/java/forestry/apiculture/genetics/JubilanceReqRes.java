/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
