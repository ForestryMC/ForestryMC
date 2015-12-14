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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.StringUtil;
import forestry.core.utils.vect.Vect;

public class GrowthProvider implements IGrowthProvider {

	@Override
	public boolean canGrow(ITreeGenome genome, World world, int xPos, int yPos, int zPos, int expectedGirth, int expectedHeight) {
		if (!hasRoom(world, xPos, yPos, zPos, expectedGirth, expectedHeight)) {
			return false;
		}

		if (getGrowthConditions(genome, world, xPos, yPos, zPos) == EnumGrowthConditions.HOSTILE) {
			return false;
		}

		return hasSufficientSaplings(genome, world, xPos, yPos, zPos, expectedGirth);
	}

	@Override
	public EnumGrowthConditions getGrowthConditions(ITreeGenome genome, World world, int xPos, int yPos, int zPos) {
		return getConditionFromLight(world, xPos, yPos, zPos);
	}

	@Override
	public String getDescription() {
		return StringUtil.localize("growth.normal");
	}

	@Override
	public String[] getInfo() {
		return new String[0];
	}

	protected static EnumGrowthConditions getConditionsFromRainfall(World world, int xPos, int yPos, int zPos, float min, float max) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(xPos, zPos);
		if (biome.rainfall < min || biome.rainfall > max) {
			return EnumGrowthConditions.HOSTILE;
		}

		return EnumGrowthConditions.EXCELLENT;
	}

	protected static EnumGrowthConditions getConditionsFromTemperature(World world, int xPos, int yPos, int zPos, float min, float max) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(xPos, zPos);
		float biomeTemperature = biome.getFloatTemperature(xPos, yPos, zPos);
		if (biomeTemperature < min || biomeTemperature > max) {
			return EnumGrowthConditions.HOSTILE;
		}

		return EnumGrowthConditions.EXCELLENT;
	}

	protected static EnumGrowthConditions getConditionFromLight(World world, int xPos, int yPos, int zPos) {
		int lightvalue = world.getBlockLightValue(xPos, yPos + 1, zPos);

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

	private static boolean hasRoom(World world, int xPos, int yPos, int zPos, int expectedGirth, int expectedHeight) {

		int offset = (expectedGirth - 1) / 2;
		// if(offset <= 0)
		// offset = 1;
		Vect start = new Vect(xPos - offset, yPos + 1, zPos + offset);
		Vect area = new Vect(-offset + expectedGirth, expectedHeight + 1, -offset + expectedGirth);

		return checkArea(world, start, area);
	}

	private static boolean checkArea(World world, Vect start, Vect area) {
		for (int x = start.x; x < start.x + area.x; x++) {
			for (int y = start.y; y < start.y + area.y; y++) {
				for (int z = start.z; z < start.z + area.z; z++) {
					if (!world.isAirBlock(x, y, z) && !BlockUtil.isReplaceableBlock(world, x, y, z)) {
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

				if (world.isAirBlock(x, yPos, z)) {
					return false;
				}

				TileEntity tile = world.getTileEntity(x, yPos, z);
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
