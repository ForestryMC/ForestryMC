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

import forestry.core.proxy.Proxies;
import forestry.core.utils.Vect;

public class CropBlock extends Crop {

	Block block;
	int meta;

	public CropBlock(World world, Block block, int meta, Vect position) {
		super(world, position);
		this.block = block;
		this.meta = meta;
	}

	@Override
	protected boolean isCrop(Vect pos) {
		return getBlock(pos) == block && getBlockMeta(pos) == meta;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		Collection<ItemStack> harvested = block.getDrops(world, pos.x, pos.y, pos.z, meta, 0);
		Proxies.common.addBlockDestroyEffects(world, pos.x, pos.y, pos.z, block, 0);
		// Block.breakBlock() is called by vanilla itself, removing TEs.
		world.setBlockToAir(pos.x, pos.y, pos.z);
		return harvested;
	}

	@Override
	public String toString() {
		return String.format("CropBlock [ position: [ %s ]; block: %s; meta: %s ]", position.toString(), block.getUnlocalizedName(), meta);
	}
}
