/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.logic;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.core.config.Defaults;
import forestry.core.utils.Vect;

public abstract class Crop implements ICrop {

	protected World world;
	protected Vect position;

	public Crop(World world, Vect position) {
		this.world = world;
		this.position = position;
	}

	protected final void setBlock(Vect position, Block block, int meta) {
		world.setBlock(position.x, position.y, position.z, block, meta, Defaults.FLAG_BLOCK_SYNCH);
	}

	protected final void clearBlock(Vect position) {
		world.setBlockToAir(position.x, position.y, position.z);
		if (world.getTileEntity(position.x, position.y, position.z) != null)
			world.setTileEntity(position.x, position.y, position.z, null);
	}

	protected final Block getBlock(Vect position) {
		return world.getBlock(position.x, position.y, position.z);
	}

	protected final int getBlockMeta(Vect position) {
		return world.getBlockMetadata(position.x, position.y, position.z);
	}

	protected final ItemStack getAsItemStack(Vect position) {
		return new ItemStack(getBlock(position), 1, getBlockMeta(position));
	}

	protected abstract boolean isCrop(Vect pos);

	protected abstract Collection<ItemStack> harvestBlock(Vect pos);

	@Override
	public Collection<ItemStack> harvest() {
		if (!isCrop(position))
			return null;

		return harvestBlock(position);
	}

}
