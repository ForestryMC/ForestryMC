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

import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.core.config.Defaults;

public class VanillaFlowerPotGrowthRule implements IFlowerGrowthRule {

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, int x, int y, int z) {
		Block ground = world.getBlock(x, y, z);
		int groundMeta = world.getBlockMetadata(x, y, z);
		if (ground != Blocks.flower_pot || groundMeta != 0) {
			return false;
		}

		if (flowerType.equals(FlowerManager.FlowerTypeVanilla) || flowerType.equals(FlowerManager.FlowerTypeSnow)) {
			groundMeta = world.rand.nextInt(1) + 1;
		} else if (flowerType.equals(FlowerManager.FlowerTypeJungle)) {
			groundMeta = 11;
		} else if (flowerType.equals(FlowerManager.FlowerTypeCacti)) {
			groundMeta = world.rand.nextInt(1) + 9;
		} else if (flowerType.equals(FlowerManager.FlowerTypeMushrooms)) {
			groundMeta = world.rand.nextInt(1) + 7;
		} else {
			return false;
		}

		return world.setBlock(x, y, z, ground, groundMeta, Defaults.FLAG_BLOCK_SYNCH);
	}
}
