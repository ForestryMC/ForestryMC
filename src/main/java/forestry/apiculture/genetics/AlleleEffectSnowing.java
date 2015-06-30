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

import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.proxy.Proxies;
import forestry.core.vect.IVect;
import forestry.core.vect.Vect;

public class AlleleEffectSnowing extends AlleleEffectThrottled {

	public AlleleEffectSnowing() {
		super("snowing", false, 20, true, true);
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

		for (int i = 0; i < 1; i++) {

			Vect randomPos = new Vect(world.rand.nextInt(area.x), world.rand.nextInt(area.y), world.rand.nextInt(area.z));

			Vect posBlock = randomPos.add(new Vect(housing.getCoordinates()));
			posBlock = posBlock.add(offset);

			// Put snow on the ground
			if (!world.isSideSolid(posBlock.x, posBlock.y - 1, posBlock.z, ForgeDirection.UP, false)) {
				continue;
			}

			if (!world.isAirBlock(posBlock.x, posBlock.y, posBlock.z)) {
				continue;
			}

			Proxies.common.setBlockWithNotify(world, posBlock.x, posBlock.y, posBlock.z, Blocks.snow_layer);
		}

		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (housing.getWorld().rand.nextInt(3) == 0) {
			IVect area = getModifiedArea(genome, housing);

			ChunkCoordinates coordinates = housing.getCoordinates();
			World world = housing.getWorld();

			double spawnX = coordinates.posX + world.rand.nextInt(area.getX() * 2) - area.getX();
			double spawnY = coordinates.posY + world.rand.nextInt(area.getY());
			double spawnZ = coordinates.posZ + world.rand.nextInt(area.getZ() * 2) - area.getZ();

			Proxies.common.addEntitySnowFX(world, spawnX, spawnY, spawnZ, 0F, 0F, 0F);
		}

		return storedData;
	}
}
