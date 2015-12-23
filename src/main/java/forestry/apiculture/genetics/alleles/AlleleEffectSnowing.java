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

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;

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

		BlockPos area = getModifiedArea(genome, housing);
		BlockPos offset = BlockUtil.multiply(area, -1 / 2.0f);

		for (int i = 0; i < 1; i++) {

			BlockPos randomPos = BlockUtil.getRandomPositionInArea(world.rand, area);

			BlockPos posBlock = randomPos.add(new BlockPos(housing.getCoordinates()));
			posBlock = posBlock.add(offset);

			// Put snow on the ground
			if (!world.isSideSolid(posBlock.add(0, -1, 0), EnumFacing.UP, false)) {
				continue;
			}

			IBlockState state = world.getBlockState(posBlock);

			if (state.getBlock() == Blocks.snow_layer) {
				if (state.getBlock().getMetaFromState(state) < 7) {
					world.setBlockState(posBlock, state.getBlock().getStateFromMeta(state.getBlock().getMetaFromState(state) + 1), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
				}
			} else if (state.getBlock().isReplaceable(world, posBlock)) {
				world.setBlockState(posBlock, Blocks.snow_layer.getStateFromMeta(0), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
			}
		}

		return storedData;
	}

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (housing.getWorld().rand.nextInt(3) == 0) {
			BlockPos area = getModifiedArea(genome, housing);
			BlockPos offset = BlockUtil.multiply(area, -0.5F);

			BlockPos coordinates = housing.getCoordinates();
			World world = housing.getWorld();

			BlockPos spawn = BlockUtil.getRandomPositionInArea(world.rand, area).add(coordinates).add(offset);
			Proxies.render.addEntitySnowFX(world, spawn.getX(), spawn.getY(), spawn.getZ());
			return storedData;
		} else {
			return super.doFX(genome, storedData, housing);
		}
	}

}
