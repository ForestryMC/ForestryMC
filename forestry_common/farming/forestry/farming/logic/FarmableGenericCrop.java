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
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public class FarmableGenericCrop implements IFarmable {

	private final ItemStack seed;
	private final Block block;
	private final int mature;

	public FarmableGenericCrop(ItemStack seed, Block block, int mature) {
		this.seed = seed;
		this.block = block;
		this.mature = mature;
	}

	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {

		if (world.isAirBlock(x, y, z))
			return false;

		if (world.getBlock(x, y, z) != block)
			return false;
		else
			return true;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) != block)
			return null;
		if (world.getBlockMetadata(x, y, z) != mature)
			return null;

		return new CropBlock(world, block, mature, new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		if (seed.getItem() != itemstack.getItem())
			return false;

		if (seed.getItemDamage() >= 0)
			return seed.getItemDamage() == itemstack.getItemDamage();
		else
			return true;
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		return germling.copy().tryPlaceItemIntoWorld(Utils.getForestryPlayer(world, x, y, z), world, x, y - 1, z, 1, 0, 0, 0);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
