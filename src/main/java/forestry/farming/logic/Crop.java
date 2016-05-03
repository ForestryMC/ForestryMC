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
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.core.config.Constants;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.vect.Vect;

public abstract class Crop implements ICrop {

	protected final World world;
	protected final Vect position;

	protected Crop(World world, Vect position) {
		this.world = world;
		this.position = position;
	}

	protected final void setBlock(Vect position, IBlockState blockState) {
		world.setBlockState(position, blockState, Constants.FLAG_BLOCK_SYNCH);
	}

	@Deprecated
	protected final void setBlock(Vect position, Block block, int meta) {
		setBlock(position, block.getStateFromMeta(meta));
	}

	protected final Block getBlock(Vect position) {
		return getBlockState(position).getBlock();
	}
	
	protected final IBlockState getBlockState(Vect position) {
		return BlockUtil.getBlockState(world, position);
	}

	protected final int getBlockMeta(Vect position) {
		return getBlock(position).getMetaFromState(getBlockState(position));
	}

	protected abstract boolean isCrop(Vect pos);

	protected abstract Collection<ItemStack> harvestBlock(Vect pos);

	@Nullable
	@Override
	public Collection<ItemStack> harvest() {
		if (!isCrop(position)) {
			return null;
		}

		return harvestBlock(position);
	}

}
