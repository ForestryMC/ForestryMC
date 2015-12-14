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

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;

public class GrowthRuleFertilize implements IFlowerGrowthRule {

	private final List<Block> allowedItems;

	public GrowthRuleFertilize(Block... allowedItems) {
		this.allowedItems = Arrays.asList(allowedItems);
	}

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual, int x, int y, int z) {
		return growFlower(world, x, y, z);
	}

	@Override
	public boolean growFlower(IFlowerGrowthHelper helper, String flowerType, World world, int x, int y, int z) {
		return growFlower(world, x, y, z);
	}

	private boolean growFlower(World world, int x, int y, int z) {
		Block ground = world.getBlock(x, y, z);
		int groundMeta;
		for (Block b : this.allowedItems) {
			if (b == ground) {
				groundMeta = world.getBlockMetadata(x, y, z);
				if (groundMeta > 6) {
					return false;
				}
				if (groundMeta < 6) {
					groundMeta += world.rand.nextInt(2) + 1;
				} else {
					groundMeta = 7;
				}

				return world.setBlockMetadataWithNotify(x, y, z, groundMeta, 2);
			}
		}

		return false;
	}

}
