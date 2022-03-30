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

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.core.tiles.TileUtil;

import genetics.api.individual.IGenome;

public class JubilanceRequiresResource implements IJubilanceProvider {

	private final Set<BlockState> acceptedBlockStates = new HashSet<>();

	public JubilanceRequiresResource(BlockState... acceptedBlockStates) {
		Collections.addAll(this.acceptedBlockStates, acceptedBlockStates);
	}

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IGenome genome, IBeeHousing housing) {
		Level world = housing.getWorldObj();
		BlockPos pos = housing.getCoordinates();

		BlockEntity tile;
		do {
			pos = pos.below();
			tile = TileUtil.getTile(world, pos);
		} while (tile instanceof IBeeHousing && pos.getY() > 0);

		BlockState blockState = world.getBlockState(pos);
		return this.acceptedBlockStates.contains(blockState);
	}

}
