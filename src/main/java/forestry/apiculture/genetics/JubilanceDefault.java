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

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.core.vect.Vect;

public class JubilanceDefault implements IJubilanceProvider {

	@Override
	public boolean isJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
		BiomeGenBase biome = BiomeGenBase.getBiome(housing.getBiomeId());

		if (EnumTemperature.getFromValue(biome.temperature) != species.getTemperature() ||
				EnumHumidity.getFromValue(biome.rainfall) != species.getHumidity()) {
			return false;
		}

		return true;
	}

	protected AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing, float modifier) {
		int[] areaAr = genome.getTerritory();
		Vect area = new Vect(areaAr[0], areaAr[1], areaAr[2]).multiply(modifier);
		Vect offset = new Vect(-Math.round(area.x / 2), -Math.round(area.y / 2), -Math.round(area.z / 2));

		Vect min = new Vect(housing.getCoords().getX() + offset.x, housing.getCoords().getY() + offset.y, housing.getCoords().getZ() + offset.z);
		Vect max = new Vect(housing.getCoords().getX() + offset.x + area.x, housing.getCoords().getY() + offset.y + area.y, housing.getCoords().getZ() + offset.z + area.z);

		return AxisAlignedBB.fromBounds(min.x, min.y, min.z, max.x, max.y, max.z);
	}

}
