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

import forestry.api.arboriculture.EnumGrowthConditions;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeGenome;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Utils;
import forestry.core.vect.Vect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class GrowthProvider implements IGrowthProvider {

	@Override
	public boolean canGrow(ITreeGenome genome, World world, int xPos, int yPos, int zPos, int expectedGirth, int expectedHeight) {
		return hasRoom(genome, world, xPos, yPos, zPos, expectedGirth, expectedHeight)
				&& getGrowthConditions(genome, world, xPos, yPos, zPos) != EnumGrowthConditions.HOSTILE
				&& hasSufficientSaplings(genome, world, xPos, yPos, zPos, expectedGirth);
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

	protected EnumGrowthConditions getConditionsFromRainfall(World world, int xPos, int yPos, int zPos, float min, float max) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(xPos, zPos);
		if (biome.rainfall < min || biome.rainfall > max)
			return EnumGrowthConditions.HOSTILE;

		return EnumGrowthConditions.EXCELLENT;
	}

	protected EnumGrowthConditions getConditionsFromTemperature(World world, int xPos, int yPos, int zPos, float min, float max) {

		BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(xPos, zPos);
		if (biome.temperature < min || biome.temperature > max)
			return EnumGrowthConditions.HOSTILE;

		return EnumGrowthConditions.EXCELLENT;
	}

	protected EnumGrowthConditions getConditionFromLight(World world, int xPos, int yPos, int zPos) {
		int lightvalue = world.getBlockLightValue(xPos, yPos + 1, zPos);

		if (lightvalue > 13)
			return EnumGrowthConditions.EXCELLENT;
		else if (lightvalue > 11)
			return EnumGrowthConditions.GOOD;
		else if (lightvalue > 8)
			return EnumGrowthConditions.NORMAL;
		else if (lightvalue > 6)
			return EnumGrowthConditions.PALTRY;
		else
			return EnumGrowthConditions.HOSTILE;
	}

	/*
	 * protected EnumGrowthConditions getConditionFromGround(World world, int xPos, int yPos, int zPos) { Block block = Block.blocksList[world.getBlock(xPos,
	 * yPos - 1, zPos)]; if(block == null) return EnumGrowthConditions.HOSTILE;
	 * 
	 * for(EnumPlantType type : this.validPlantTypes) { this.plantType = type; if(block.canSustainPlant(world, xPos, yPos - 1, zPos, ForgeDirection.UP, this))
	 * return EnumGrowthConditions.EXCELLENT; }
	 * 
	 * return EnumGrowthConditions.HOSTILE; }
	 */

	protected boolean hasRoom(ITreeGenome genome, World world, int xPos, int yPos, int zPos, int expectedGirth, int expectedHeight) {

		int offset = (expectedGirth - 1) / 2;
		// if(offset <= 0)
		// offset = 1;

		return checkArea(world, new Vect(xPos - offset, yPos + 1, zPos + offset),
				new Vect(-offset + expectedGirth, expectedHeight + 1, -offset + expectedGirth));
	}

	protected final boolean checkArea(World world, Vect start, Vect area) {
		for (int x = start.x; x < start.x + area.x; x++)
			for (int y = start.y; y < start.y + area.y; y++)
				for (int z = start.z; z < start.z + area.z; z++)
					if (!world.isAirBlock(x, y, z) && !Utils.isReplaceableBlock(world, x, y, z))
						return false;
		return true;
	}

	protected boolean hasSufficientSaplings(ITreeGenome genome, World world, int xPos, int yPos, int zPos, int expectedGirth) {

		if (expectedGirth == 1)
			return true;

		int offset = (expectedGirth - 1) / 2;

		for (int x = xPos - offset; x < xPos - offset + expectedGirth; x++)
			for (int z = zPos - offset; z < zPos - offset + expectedGirth; z++) {

				if (world.isAirBlock(x, yPos, z))
					return false;

				TileEntity tile = world.getTileEntity(x, yPos, z);
				if (!(tile instanceof TileSapling))
					return false;

				ITree tree = ((TileSapling) tile).getTree();
				if (tree == null || !tree.getGenome().getPrimary().getUID().equals(genome.getPrimary().getUID()))
					return false;
			}

		return true;
	}

}
