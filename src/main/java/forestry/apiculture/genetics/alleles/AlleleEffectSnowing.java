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
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.IEffectData;
import forestry.core.render.ParticleRender;
import forestry.core.utils.VectUtil;

public class AlleleEffectSnowing extends AlleleEffectThrottled {

	public AlleleEffectSnowing() {
		super("snowing", false, 20, true, true);
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

		Vec3i area = getModifiedArea(genome, housing);
		Vec3i offset = VectUtil.scale(area, -1 / 2.0f);

		for (int i = 0; i < 1; i++) {

			BlockPos randomPos = VectUtil.getRandomPositionInArea(world.rand, area);

			BlockPos posBlock = randomPos.add(housing.getCoordinates()).add(offset);

			// Put snow on the ground
			if (world.isBlockLoaded(posBlock) && world.isSideSolid(posBlock.down(), EnumFacing.UP, false)) {
				IBlockState state = world.getBlockState(posBlock);
				Block block = state.getBlock();

				if (block == Blocks.SNOW_LAYER) {
					Integer layers = state.getValue(BlockSnow.LAYERS);
					if (layers < 7) {
						IBlockState moreSnow = state.withProperty(BlockSnow.LAYERS, layers + 1);
						world.setBlockState(posBlock, moreSnow);
					} else {
						world.setBlockState(posBlock, Blocks.SNOW.getDefaultState());
					}
				} else if (block.isReplaceable(world, posBlock)) {
					world.setBlockState(posBlock, Blocks.SNOW_LAYER.getDefaultState());
				}
			}
		}

		return storedData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
		if (housing.getWorldObj().rand.nextInt(3) == 0) {
			Vec3i area = getModifiedArea(genome, housing);
			Vec3i offset = VectUtil.scale(area, -0.5F);

			BlockPos coordinates = housing.getCoordinates();
			World world = housing.getWorldObj();

			BlockPos spawn = VectUtil.getRandomPositionInArea(world.rand, area).add(coordinates).add(offset);
			ParticleRender.addEntitySnowFX(world, spawn.getX(), spawn.getY(), spawn.getZ());
			return storedData;
		} else {
			return super.doFX(genome, storedData, housing);
		}
	}

}
