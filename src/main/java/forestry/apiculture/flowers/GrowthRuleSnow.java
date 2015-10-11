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
package forestry.apiculture.flowers;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.core.config.Constants;

public class GrowthRuleSnow implements IFlowerGrowthRule {

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, int x, int y, int z) {
		if (world.getBlock(x, y, z) != Blocks.snow) {
			return false;
		}

		Block ground = world.getBlock(x, y - 1, z);
		if (ground != Blocks.dirt && ground != Blocks.grass) {
			return false;
		}

		IFlower flower = fr.getRandomPlantableFlower(flowerType, world.rand);
		return world.setBlock(x, y, z, flower.getBlock(), flower.getMeta(), Constants.FLAG_BLOCK_SYNCH);
	}

	@Override
	public boolean growFlower(IFlowerGrowthHelper helper, String flowerType, World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) != Blocks.snow) {
			return false;
		}

		Block ground = world.getBlock(x, y - 1, z);
		if (ground != Blocks.dirt && ground != Blocks.grass) {
			return false;
		}

		return helper.plantRandomFlower(flowerType, world, x, y, z);
	}

}
