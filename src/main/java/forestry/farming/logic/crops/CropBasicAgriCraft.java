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
package forestry.farming.logic.crops;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.utils.BlockUtil;

public class CropBasicAgriCraft extends Crop {

	private final IBlockState blockState;

	public CropBasicAgriCraft(World world, IBlockState blockState, BlockPos position) {
		super(world, position);
		this.blockState = blockState;
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return world.getBlockState(pos) == blockState;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		Block block = blockState.getBlock();
		NonNullList<ItemStack> harvest = NonNullList.create();
		block.getDrops(harvest, world, pos, blockState, 0);
		if (harvest.size() > 1) {
			harvest.remove(1); //AgriCraft returns cropsticks in 0, seeds in 1 in getDrops, removing since harvesting doesn't return them.
		}
		harvest.remove(0);

		IBlockState oldState = world.getBlockState(pos);
		BlockUtil.setBlockWithBreakSound(world, pos, block.getDefaultState(), oldState);
		return harvest;
	}

	@Override
	public String toString() {
		return String.format("CropBasicAgriCraft [ position: [ %s ]; block: %s ]", position.toString(), blockState);
	}
}
