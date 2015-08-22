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

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.core.config.Defaults;
import forestry.core.vect.Vect;

public abstract class Crop implements ICrop {

	protected final World world;
	protected final Vect position;

	public Crop(World world, Vect position) {
		this.world = world;
		this.position = position;
	}

	protected final void setBlock(Vect position, Block block, int meta) {
		world.setBlockState(position.toBlockPos(), block.getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH);
	}

	protected final void clearBlock(Vect position) {
		world.setBlockToAir(position.toBlockPos());
		if (world.getTileEntity(position.toBlockPos()) != null) {
			world.setTileEntity(position.toBlockPos(), null);
		}
	}

	protected final Block getBlock(Vect position) {
		return world.getBlockState(position.toBlockPos()).getBlock();
	}
	
	protected final IBlockState getBlockState(Vect position) {
		return world.getBlockState(position.toBlockPos());
	}

	protected final int getBlockMeta(Vect position) {
		IBlockState state = world.getBlockState(position.toBlockPos());
		return state.getBlock().getMetaFromState(state);
	}

	protected final ItemStack getAsItemStack(Vect position) {
		return new ItemStack(getBlock(position), 1, getBlockMeta(position));
	}

	protected abstract boolean isCrop(Vect pos);

	protected abstract Collection<ItemStack> harvestBlock(Vect pos);

	@Override
	public Collection<ItemStack> harvest() {
		if (!isCrop(position)) {
			return null;
		}

		return harvestBlock(position);
	}

}
