/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.logic;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.config.Defaults;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Vect;

public class FarmableStacked implements IFarmable {

	Block block;
	int matureHeight;

	public FarmableStacked(Block block, int matureHeight) {
		this.block = block;
		this.matureHeight = matureHeight;
	}

	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {
		return world.getBlock(x, y, z) == block;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		if (world.getBlock(x, y + (matureHeight - 1), z) != block)
			return null;

		return new CropBlock(world, block, 0, new Vect(x, y + (matureHeight - 1), z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return StackUtils.equals(block, itemstack);
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		return world.setBlock(x, y, z, block, 0, Defaults.FLAG_BLOCK_SYNCH);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
