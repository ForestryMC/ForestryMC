/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.genetics;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.utils.Vect;

public class JubilanceDefault implements IJubilanceProvider {

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		BiomeGenBase biome = BiomeGenBase.getBiome(housing.getBiomeId());

		if (EnumTemperature.getFromValue(biome.temperature) != species.getTemperature() ||
				EnumHumidity.getFromValue(biome.rainfall) != species.getHumidity())
			return false;

		return true;
	}

	protected AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing, float modifier) {
		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(modifier);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		Vect min = new Vect(housing.getXCoord() + offset.x, housing.getYCoord() + offset.y, housing.getZCoord() + offset.z);
		Vect max = new Vect(housing.getXCoord() + offset.x + area.x, housing.getYCoord() + offset.y + area.y, housing.getZCoord() + offset.z + area.z);

		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}

}
