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

import forestry.api.farming.ICrop;
import forestry.core.config.Constants;

public abstract class Crop implements ICrop {

	protected final World world;
	protected final BlockPos position;

	protected Crop(World world, BlockPos position) {
		this.world = world;
		this.position = position;
	}

	protected final void setBlock(BlockPos position, IBlockState blockState) {
		world.setBlockState(position, blockState, Constants.FLAG_BLOCK_SYNCH);
	}

	@Deprecated
	protected final void setBlock(BlockPos position, Block block, int meta) {
		setBlock(position, block.getStateFromMeta(meta));
	}

	protected final Block getBlock(BlockPos position) {
		return getBlockState(position).getBlock();
	}
	
	protected final IBlockState getBlockState(BlockPos position) {
		return world.getBlockState(position);
	}

	protected final int getBlockMeta(BlockPos position) {
		return getBlock(position).getMetaFromState(getBlockState(position));
	}

	protected abstract boolean isCrop(BlockPos pos);

	protected abstract Collection<ItemStack> harvestBlock(BlockPos pos);

	@Nullable
	@Override
	public Collection<ItemStack> harvest() {
		if (!isCrop(position)) {
			return null;
		}

		return harvestBlock(position);
	}

}
