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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.StringUtil;

public class GrowthProvider implements IGrowthProvider {

	@Override
	public boolean canGrow(ITreeGenome genome, World world, BlockPos pos, int expectedGirth, int expectedHeight) {
		if (!hasRoom(world, pos, expectedGirth, expectedHeight)) {
			return false;
		}

		if (getGrowthConditions(genome, world, pos) == EnumGrowthConditions.HOSTILE) {
			return false;
		}

		return hasSufficientSaplings(genome, world, pos.getX(), pos.getY(), pos.getZ(), expectedGirth);
	}

	@Override
	public EnumGrowthConditions getGrowthConditions(ITreeGenome genome, World world, BlockPos pos) {
		return getConditionFromLight(world, pos);
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("growth.normal");
	}

	@Override
	public String[] getInfo() {
		return new String[0];
	}

	protected static EnumGrowthConditions getConditionsFromRainfall(World world, BlockPos pos, float min, float max) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenerator(pos);
		if (biome.rainfall < min || biome.rainfall > max) {
			return EnumGrowthConditions.HOSTILE;
		}

		return EnumGrowthConditions.EXCELLENT;
	}

	protected static EnumGrowthConditions getConditionsFromTemperature(World world, BlockPos pos, float min, float max) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenerator(pos);
		float biomeTemperature = biome.getFloatTemperature(pos);
		if (biomeTemperature < min || biomeTemperature > max) {
			return EnumGrowthConditions.HOSTILE;
		}

		return EnumGrowthConditions.EXCELLENT;
	}

	protected static EnumGrowthConditions getConditionFromLight(World world, BlockPos pos) {
		int lightvalue = world.getLight(pos.add(0, 1, 0));

		if (lightvalue > 13) {
			return EnumGrowthConditions.EXCELLENT;
		} else if (lightvalue > 11) {
			return EnumGrowthConditions.GOOD;
		} else if (lightvalue > 8) {
			return EnumGrowthConditions.NORMAL;
		} else if (lightvalue > 6) {
			return EnumGrowthConditions.PALTRY;
		} else {
			return EnumGrowthConditions.HOSTILE;
		}
	}

	private static boolean hasRoom(World world, BlockPos pos, int expectedGirth, int expectedHeight) {

		int offset = (expectedGirth - 1) / 2;
		// if(offset <= 0)
		// offset = 1;
		BlockPos start = new BlockPos(pos.getX() - offset, pos.getY() + 1, pos.getY() + offset);
		BlockPos area = new BlockPos(-offset + expectedGirth, expectedHeight + 1, -offset + expectedGirth);

		return checkArea(world, start, area);
	}

	private static boolean checkArea(World world, BlockPos start, BlockPos area) {
		for (int x = start.getX(); x < start.getX() + area.getX(); x++) {
			for (int y = start.getY(); y < start.getY() + area.getY(); y++) {
				for (int z = start.getZ(); z < start.getZ() + area.getZ(); z++) {
					BlockPos posS = new BlockPos(x, y, z);
					if (!world.isAirBlock(posS) && !BlockUtil.isReplaceableBlock(world, posS)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static boolean hasSufficientSaplings(ITreeGenome genome, World world, int xPos, int yPos, int zPos, int expectedGirth) {

		if (expectedGirth == 1) {
			return true;
		}

		int offset = (expectedGirth - 1) / 2;

		for (int x = xPos - offset; x < xPos - offset + expectedGirth; x++) {
			for (int z = zPos - offset; z < zPos - offset + expectedGirth; z++) {

				if (world.isAirBlock(new BlockPos(x, yPos, z))) {
					return false;
				}

				TileEntity tile = world.getTileEntity(new BlockPos(x, yPos, z));
				if (!(tile instanceof TileSapling)) {
					return false;
				}

				ITree tree = ((TileSapling) tile).getTree();
				if (tree == null || !tree.getGenome().getPrimary().getUID().equals(genome.getPrimary().getUID())) {
					return false;
				}
			}
		}

		return true;
	}

}
