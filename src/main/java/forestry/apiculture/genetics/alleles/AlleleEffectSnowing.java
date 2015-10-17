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
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.utils.vect.Vect;

public class AlleleEffectSnowing extends AlleleEffectThrottled {

	public AlleleEffectSnowing() {
		super("snowing", false, 20, true, true);
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

		Vect area = getModifiedArea(genome, housing);
		Vect offset = area.multiply(-1 / 2.0f);

		for (int i = 0; i < 1; i++) {

			Vect randomPos = Vect.getRandomPositionInArea(world.rand, area);

			Vect posBlock = randomPos.add(new Vect(housing.getCoordinates()));
			posBlock = posBlock.add(offset);

			// Put snow on the ground
			if (!world.isSideSolid(posBlock.x, posBlock.y - 1, posBlock.z, ForgeDirection.UP, false)) {
				continue;
			}

			Block block = world.getBlock(posBlock.x, posBlock.y, posBlock.z);

			if (block == Blocks.snow_layer) {
				int meta = world.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);
				if (meta < 7) {
					world.setBlockMetadataWithNotify(posBlock.x, posBlock.y, posBlock.z, meta + 1, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
				}
			} else if (block.isReplaceable(world, posBlock.x, posBlock.y, posBlock.z)) {
				world.setBlock(posBlock.x, posBlock.y, posBlock.z, Blocks.snow_layer, 0, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
			}
		}

		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (housing.getWorld().rand.nextInt(3) == 0) {
			Vect area = getModifiedArea(genome, housing);
			Vect offset = area.multiply(-0.5F);

			ChunkCoordinates coordinates = housing.getCoordinates();
			World world = housing.getWorld();

			Vect spawn = Vect.getRandomPositionInArea(world.rand, area).add(coordinates).add(offset);
			Proxies.render.addEntitySnowFX(world, spawn.x, spawn.y, spawn.z);
			return storedData;
		} else {
			return super.doFX(genome, storedData, housing);
		}
	}

}
