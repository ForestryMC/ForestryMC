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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.vect.Vect;

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
			if (!world.isSideSolid(new BlockPos(posBlock.getX(), posBlock.getY() - 1, posBlock.getZ()), EnumFacing.UP, false)) {
				continue;
			}

			BlockPos pos = new BlockPos(posBlock.getX(), posBlock.getY(), posBlock.getZ());
			Block block = world.getBlockState(pos).getBlock();
			if (block == Blocks.snow_layer) {
				int meta = block.getMetaFromState(world.getBlockState(pos));
				if (meta < 7) {
					world.setBlockState(pos, block.getStateFromMeta(meta + 1), Defaults.FLAG_BLOCK_SYNCH_AND_UPDATE);
				}
			} else if (block.isReplaceable(world, pos)) {
				Proxies.common.setBlockStateWithNotify(world, pos, Blocks.snow_layer);
			}
		}

		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (housing.getWorld().rand.nextInt(3) == 0) {
			Vect area = getModifiedArea(genome, housing);
			Vect offset = area.multiply(-0.5F);

			BlockPos coordinates = housing.getCoordinates();
			World world = housing.getWorld();

			Vect spawn = Vect.getRandomPositionInArea(world.rand, area).add(coordinates).add(offset);
			Proxies.common.addEntitySnowFX(world, spawn.getX(), spawn.getY(), spawn.getZ());
			return storedData;
		} else {
			return super.doFX(genome, storedData, housing);
		}
	}

}
