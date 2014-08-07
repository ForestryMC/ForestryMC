/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.genetics;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class AlleleEffectSnowing extends AlleleEffectThrottled {

	public AlleleEffectSnowing(String uid) {
		super(uid, "snowing", false, 20, true, true);
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

		for (int i = 0; i < 1; i++) {

			Vect randomPos = new Vect(world.rand.nextInt(area.x), world.rand.nextInt(area.y), world.rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getXCoord(), housing.getYCoord(), housing.getZCoord()));
			posBlock = posBlock.add(offset);

			// Put snow on the ground
			if (!world.isSideSolid(posBlock.x, posBlock.y - 1, posBlock.z, ForgeDirection.UP, false))
				continue;

			if (!world.isAirBlock(posBlock.x, posBlock.y, posBlock.z))
				continue;

			Proxies.common.setBlockWithNotify(world, posBlock.x, posBlock.y, posBlock.z, Blocks.snow_layer);
		}

		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (housing.getWorld().rand.nextInt(3) == 0) {
			int[] area = getModifiedArea(genome, housing);
			Proxies.render.addSnowFX(housing.getWorld(), housing.getXCoord(), housing.getYCoord(), housing.getZCoord(), genome.getPrimary().getIconColour(0), area[0], area[1], area[2]);
		}
		return storedData;
	}
}
