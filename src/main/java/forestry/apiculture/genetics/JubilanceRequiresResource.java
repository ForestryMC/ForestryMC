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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.core.tiles.TileUtil;

public class JubilanceRequiresResource implements IJubilanceProvider {

	private final Set<IBlockState> acceptedBlockStates = new HashSet<>();

	public JubilanceRequiresResource(IBlockState... acceptedBlockStates) {
		Collections.addAll(this.acceptedBlockStates, acceptedBlockStates);
	}

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		World world = housing.getWorldObj();
		BlockPos pos = housing.getCoordinates();

		TileEntity tile;
		do {
			pos = pos.down();
			tile = TileUtil.getTile(world, pos);
		} while (tile instanceof IBeeHousing && pos.getY() > 0);

		IBlockState blockState = world.getBlockState(pos);
		return this.acceptedBlockStates.contains(blockState);
	}

}
