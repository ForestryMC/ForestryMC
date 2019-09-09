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
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.genetics.IFlowerGrowthHelper;
import forestry.api.genetics.IFlowerGrowthRule;

public class GrowthRuleFertilize implements IFlowerGrowthRule {

	private final List<Block> allowedItems;

	public GrowthRuleFertilize(Block... allowedItems) {
		this.allowedItems = Arrays.asList(allowedItems);
	}

	@Override
	public boolean growFlower(IFlowerGrowthHelper helper, String flowerType, World world, BlockPos pos, Collection<BlockState> potentialFlowers) {
		return growFlower(world, pos);
	}

	private boolean growFlower(World world, BlockPos pos) {
		if (!world.isBlockLoaded(pos)) {
			return false;
		}

		BlockState state = world.getBlockState(pos);
		Block ground = state.getBlock();
		int groundMeta;
		for (Block b : this.allowedItems) {
			if (b == ground) {
				groundMeta = 0;//TODO flatten ground.getMetaFromState(state); Want the maturity property instead?
				if (groundMeta > 6) {
					return false;
				}
				if (groundMeta < 6) {
					groundMeta += world.rand.nextInt(2) + 1;
				} else {
					groundMeta = 7;
				}

				return world.setBlockState(pos, ground.getDefaultState(), 2); //TODO flatten ground.getStateFromMeta(groundMeta), 2);
			}
		}

		return false;
	}

}
