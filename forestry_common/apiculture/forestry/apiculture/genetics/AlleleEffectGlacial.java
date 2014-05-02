/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class AlleleEffectGlacial extends AlleleEffectThrottled {

	public AlleleEffectGlacial(String uid) {
		super(uid, "glacial", false, 200, true, false);
	}

	@Override
	public IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {

		World world = housing.getWorld();

		if (isHalted(storedData, housing))
			return storedData;

		EnumTemperature temp = EnumTemperature.getFromValue(BiomeGenBase.getBiome(housing.getBiomeId()).temperature);

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

		for (int i = 0; i < 10; i++) {

			Vect randomPos = new Vect(world.rand.nextInt(area.x), world.rand.nextInt(area.y), world.rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord()));
			posBlock = posBlock.add(offset);

			// Freeze water
			Block block = world.getBlock(posBlock.x, posBlock.y, posBlock.z);
			if (block != Blocks.water)
				continue;

			if (!world.isAirBlock(posBlock.x, posBlock.y + 1, posBlock.z))
				continue;

			Proxies.common.setBlockWithNotify(world, posBlock.x, posBlock.y, posBlock.z, Blocks.ice);
		}

		return storedData;
	}
}
