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
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.proxy.Proxies;
import forestry.core.vect.Vect;

public class AlleleEffectGlacial extends AlleleEffectThrottled {

	public AlleleEffectGlacial() {
		super("glacial", false, 200, true, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();

		if (isHalted(storedData, housing)) {
			return storedData;
		}

		EnumTemperature temp = housing.getTemperature();

		switch (temp) {
			case HELLISH:
			case HOT:
			case WARM:
				return storedData;
			default:
		}

		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));
		Vect housingCoords = new Vect(housing.getCoordinates());

		for (int i = 0; i < 10; i++) {

			Vect randomPos = Vect.getRandomPositionInArea(world.rand, area);
			Vect posBlock = Vect.add(randomPos, housingCoords, offset);

			// Freeze water
			Block block = world.getBlock(posBlock.x, posBlock.y, posBlock.z);
			if (block != Blocks.water) {
				continue;
			}

			if (!world.isAirBlock(posBlock.x, posBlock.y + 1, posBlock.z)) {
				continue;
			}

			Proxies.common.setBlockWithNotify(world, posBlock.x, posBlock.y, posBlock.z, Blocks.ice);
		}

		return storedData;
	}
}
