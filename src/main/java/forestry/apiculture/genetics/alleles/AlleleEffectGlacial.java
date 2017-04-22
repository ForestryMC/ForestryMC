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

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.utils.VectUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class AlleleEffectGlacial extends AlleleEffectThrottled {

	public AlleleEffectGlacial() {
		super("glacial", false, 200, true, false);
	}

	@Override
	public IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorldObj();
		EnumTemperature temp = housing.getTemperature();

		switch (temp) {
			case HELLISH:
			case HOT:
			case WARM:
				return storedData;
			default:
		}

		Vec3i area = genome.getTerritory();
		Vec3i offset = VectUtil.scale(area, -1 / 2.0f);
		BlockPos housingCoords = housing.getCoordinates();

		for (int i = 0; i < 10; i++) {

			BlockPos randomPos = VectUtil.getRandomPositionInArea(world.rand, area);
			BlockPos posBlock = VectUtil.add(randomPos, housingCoords, offset);

			// Freeze water
			if (world.isBlockLoaded(posBlock)) {
				Block block = world.getBlockState(posBlock).getBlock();
				if (block == Blocks.WATER) {
					if (world.isAirBlock(new BlockPos(posBlock.getX(), posBlock.getY() + 1, posBlock.getZ()))) {
						world.setBlockState(posBlock, Blocks.ICE.getDefaultState());
					}
				}
			}
		}

		return storedData;
	}
}
