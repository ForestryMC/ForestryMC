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
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.genetics.IFlower;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerRegistry;
import forestry.api.genetics.IIndividual;
import forestry.core.config.Defaults;

public class VanillaSnowGrowthRule implements IFlowerGrowthRule {

	@Override
	public boolean growFlower(IFlowerRegistry fr, String flowerType, World world, IIndividual individual,
			BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (world.getBlockState(pos).getBlock() != Blocks.snow) {
			return false;
		}

		Block ground = world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock();
		if (ground != Blocks.dirt && ground != Blocks.grass) {
			return false;
		}

		IFlower flower = fr.getRandomPlantableFlower(flowerType, world.rand);
		return world.setBlockState(pos, flower.getBlock().getStateFromMeta(flower.getMeta()),
				Defaults.FLAG_BLOCK_SYNCH);
	}

}
