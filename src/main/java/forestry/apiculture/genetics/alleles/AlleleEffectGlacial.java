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
package forestry.apiculture.genetics.alleles;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.config.Constants;
import forestry.core.utils.BlockUtil;

public class AlleleEffectGlacial extends AlleleEffectThrottled {

	public AlleleEffectGlacial() {
		super("glacial", false, 200, true, false);
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();
		EnumTemperature temp = housing.getTemperature();

		switch (temp) {
			case HELLISH:
			case HOT:
			case WARM:
				return storedData;
			default:
		}

		int[] areaAr = genome.getTerritory();
		BlockPos area = new BlockPos(areaAr[0], areaAr[1], areaAr[2]);
		BlockPos offset = BlockUtil.multiply(area, -1 / 2.0f);
		BlockPos housingCoords = new BlockPos(housing.getCoordinates());

		for (int i = 0; i < 10; i++) {

			BlockPos randomPos = BlockUtil.getRandomPositionInArea(world.rand, area);
			BlockPos posBlock = BlockUtil.add(randomPos, housingCoords, offset);

			// Freeze water
			Block block = world.getBlockState(posBlock).getBlock();
			if (block != Blocks.water) {
				continue;
			}

			if (!world.isAirBlock(posBlock)) {
				continue;
			}

			world.setBlockState(posBlock, Blocks.ice.getStateFromMeta(0), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
		}

		return storedData;
	}
}
