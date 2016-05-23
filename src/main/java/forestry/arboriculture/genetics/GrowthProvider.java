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
package forestry.arboriculture.genetics;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.core.ForestryAPI;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.Translator;

public class GrowthProvider implements IGrowthProvider {

	@Override
	@Nullable
	public BlockPos canGrow(ITreeGenome genome, World world, BlockPos pos, int expectedGirth, int expectedHeight) {
		BlockPos growthPos = hasSufficientSaplingsAroundSapling(genome, world, pos, expectedGirth);
		if (growthPos == null) {
			return null;
		}

		if (!hasRoom(world, growthPos, expectedGirth, expectedHeight)) {
			return null;
		}

		if (getGrowthConditions(genome, world, growthPos) == EnumGrowthConditions.HOSTILE) {
			return null;
		}

		return growthPos;
	}

	@Override
	public EnumGrowthConditions getGrowthConditions(ITreeGenome genome, World world, BlockPos pos) {
		return getConditionFromLight(world, pos);
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocal("for.growth.normal");
	}

	@Override
	public String[] getInfo() {
		return new String[0];
	}

	protected static EnumGrowthConditions getConditionsFromRainfall(World world, BlockPos pos, float min, float max) {
		float humidity = ForestryAPI.climateManager.getHumidity(world, pos);
		
		if (humidity < min || humidity > max) {
			return EnumGrowthConditions.HOSTILE;
		}

		return EnumGrowthConditions.EXCELLENT;
	}

	protected static EnumGrowthConditions getConditionsFromTemperature(World world, BlockPos pos, float min, float max) {
		float biomeTemperature = ForestryAPI.climateManager.getTemperature(world, pos);
		if (biomeTemperature < min || biomeTemperature > max) {
			return EnumGrowthConditions.HOSTILE;
		}

		return EnumGrowthConditions.EXCELLENT;
	}

	protected static EnumGrowthConditions getConditionFromLight(World world, BlockPos pos) {
		int lightValue = world.getLightFromNeighbors(pos.up());

		if (lightValue > 13) {
			return EnumGrowthConditions.EXCELLENT;
		} else if (lightValue > 11) {
			return EnumGrowthConditions.GOOD;
		} else if (lightValue > 8) {
			return EnumGrowthConditions.NORMAL;
		} else if (lightValue > 6) {
			return EnumGrowthConditions.PALTRY;
		} else {
			return EnumGrowthConditions.HOSTILE;
		}
	}

	private static boolean hasRoom(World world, BlockPos pos, int expectedGirth, int expectedHeight) {
		Vec3i area = new Vec3i(expectedGirth, expectedHeight + 1, expectedGirth);
		return checkArea(world, pos.up(), area);
	}

	private static boolean checkArea(World world, BlockPos start, Vec3i area) {
		for (int x = 0; x < area.getX(); x++) {
			for (int y = 0; y < area.getY(); y++) {
				for (int z = 0; z < area.getZ(); z++) {
					BlockPos pos = start.add(x, y, z);
					IBlockState blockState = world.getBlockState(pos);
					if (!blockState.getBlock().isReplaceable(world, pos)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Checks an area for saplings.
	 * If saplings need to be 2x2, 3x3, etc, it will check all configurations in which pos is included in that area.
	 * Uses a knownSaplings cache to avoid checking the same saplings multiple times.
	 */
	@Nullable
	private static BlockPos hasSufficientSaplingsAroundSapling(ITreeGenome genome, World world, BlockPos saplingPos, int expectedGirth) {
		final int checkSize = (expectedGirth * 2) - 1;
		final int offset = expectedGirth - 1;
		final Map<BlockPos, Boolean> knownSaplings = new HashMap<>(checkSize * checkSize);

		for (int x = -offset; x <= 0; x++) {
			for (int z = -offset; z <= 0; z++) {
				BlockPos startPos = saplingPos.add(x, 0, z);
				if (checkForSaplings(genome, world, startPos, expectedGirth, knownSaplings)) {
					return startPos;
				}
			}
		}

		return null;
	}

	private static boolean checkForSaplings(ITreeGenome genome, World world, BlockPos startPos, int girth, Map<BlockPos, Boolean> knownSaplings) {
		for (int x = 0; x < girth; x++) {
			for (int z = 0; z < girth; z++) {
				BlockPos checkPos = startPos.add(x, 0, z);
				Boolean knownSapling = knownSaplings.get(checkPos);
				if (knownSapling == null) {
					knownSapling = isSapling(genome, world, checkPos);
					knownSaplings.put(checkPos, knownSapling);
				}

				if (!knownSapling) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean isSapling(ITreeGenome genome, World world, BlockPos pos) {
		if (!world.isBlockLoaded(pos)) {
			return false;
		}

		if (world.isAirBlock(pos)) {
			return false;
		}

		TileSapling sapling = TileUtil.getTile(world, pos, TileSapling.class);
		if (sapling == null) {
			return false;
		}

		ITree tree = sapling.getTree();
		return tree != null && tree.getGenome().getPrimary().getUID().equals(genome.getPrimary().getUID());
	}
}
