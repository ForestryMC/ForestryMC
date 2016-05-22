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
package forestry.farming.logic;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;

public class CropDestroy extends Crop {

	protected final IBlockState blockState;
	@Nullable
	protected final IBlockState replantState;

	public CropDestroy(World world, IBlockState blockState, BlockPos position, @Nullable IBlockState replantState) {
		super(world, position);
		this.blockState = blockState;
		this.replantState = replantState;
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return world.getBlockState(pos) == blockState;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(World world, BlockPos pos) {
		Block block = blockState.getBlock();
		Collection<ItemStack> harvested = block.getDrops(world, pos, blockState, 0);
		Proxies.common.addBlockDestroyEffects(world, pos, blockState);

		if (replantState != null) {
			world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNCH);
		} else {
			world.setBlockToAir(pos);
		}

		return harvested;
	}

	@Override
	public String toString() {
		return String.format("CropDestroy [ position: [ %s ]; block: %s ]", position.toString(), blockState);
	}
}
