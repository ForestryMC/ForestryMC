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
package forestry.lepidopterology.worldgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.Log;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.features.LepidopterologyBlocks;
import forestry.lepidopterology.tiles.TileCocoon;

public class CocoonDecorator extends Feature<NoneFeatureConfiguration> {
	public CocoonDecorator() {
		super(NoneFeatureConfiguration.CODEC);
	}

	public static boolean genCocoon(WorldGenLevel world, Random rand, BlockPos pos, IButterfly butterfly) {
		if (butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getRarity() * ModuleLepidopterology
				.getGenerateCocoonsAmount() < rand.nextFloat() * 100.0f) {
			return false;
		}

		Biome biome = world.getBiome(new BlockPos(pos.getX(), 0, pos.getZ())).value();

		Set<Biome.BiomeCategory> speciesCategories = butterfly.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES)
				.getSpawnBiomes();

		boolean biomeTypesGood = false;
		for (Biome.BiomeCategory category : speciesCategories) {
			if (category == biome.getBiomeCategory()) {
				biomeTypesGood = true;
				break;
			}
		}

		if (!biomeTypesGood) {
			return false;
		}

		for (int tries = 0; tries < 4; tries++) {
			int x = pos.getX() + rand.nextInt(16);
			int z = pos.getZ() + rand.nextInt(16);

			if (tryGenCocoon(world, x, z, butterfly)) {
				return true;
			}
		}

		return false;
	}

	private static boolean tryGenCocoon(WorldGenLevel world, int x, int z, IButterfly butterfly) {
		int y = getYForCocoon(world, x, z);
		if (y < 0) {
			return false;
		}

		if (!isValidLocation(world, new BlockPos(x, y, z))) {
			return false;
		}

		return setCocoon(world, new BlockPos(x, y, z), butterfly);
	}

	private static boolean setCocoon(WorldGenLevel world, BlockPos pos, IButterfly butterfly) {
		Block cocoonBlock = LepidopterologyBlocks.COCOON_SOLID.getBlock();
		boolean placed = world.setBlock(pos, cocoonBlock.defaultBlockState(), Constants.FLAG_BLOCK_SYNC);
		if (!placed) {
			return false;
		}

		BlockState state = world.getBlockState(pos);
		if (cocoonBlock != state.getBlock()) {
			return false;
		}

		TileCocoon cocoon = TileUtil.getTile(world, pos, TileCocoon.class);
		if (cocoon != null) {
			cocoon.setCaterpillar(butterfly);
		} else {
			return false;
		}

		cocoonBlock.onPlace(state, world.getLevel(), pos, cocoonBlock.defaultBlockState(), false);
		world.getLevel().setBlocksDirty(pos, state, cocoonBlock.defaultBlockState());

		if (Config.logCocoonPlacement) {
			Log.info("Placed {} at {}", cocoonBlock.toString(), pos.toString());
		}

		return true;
	}

	private static int getYForCocoon(WorldGenLevel world, int x, int z) {
		int y = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z)).getY() - 1;
		BlockPos pos = new BlockPos(x, y, z);
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getMaterial() != Material.LEAVES) {
			return -1;
		}

		do {
			pos = pos.below();
			blockState = world.getBlockState(pos);
		} while (blockState.getMaterial() == Material.LEAVES);

		return y;
	}

	public static boolean isValidLocation(WorldGenLevel world, BlockPos pos) {
		BlockPos posAbove = pos.above();
		BlockState blockStateAbove = world.getBlockState(posAbove);
		Block blockAbove = blockStateAbove.getBlock();
		if (blockStateAbove.getMaterial() != Material.LEAVES) {
			return false;
		}

		BlockPos posBelow = pos.below();
		BlockState blockStateBelow = world.getBlockState(posBelow);
		return BlockUtil.canReplace(blockStateBelow, world, posBelow);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		ArrayList<IButterfly> butterflys = new ArrayList<>(ButterflyManager.butterflyRoot
				.getIndividualTemplates());

		Collections.shuffle(butterflys, context.random());

		for (IButterfly butterfly : butterflys) {
			if (genCocoon(context.level(), context.random(), context.origin(), butterfly)) {
				return true;
			}
		}

		return false;
	}
}
