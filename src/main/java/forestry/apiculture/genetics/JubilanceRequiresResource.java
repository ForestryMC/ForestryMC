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
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.core.utils.ItemStackUtil;

public class JubilanceRequiresResource implements IJubilanceProvider {

	private final ItemStack blockRequired;

	public JubilanceRequiresResource(Block block, int meta) {
		this.blockRequired = new ItemStack(block, meta);
	}

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		World world = housing.getWorld();
		BlockPos housingCoords = housing.getCoordinates();

		IBlockState state = world.getBlockState(housingCoords.down());
		return ItemStackUtil.equals(state, blockRequired);
	}

}
