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
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public class FarmableGenericSapling implements IFarmable {

	final Block sapling;
	final int saplingMeta;

	ItemStack[] windfall;

	public FarmableGenericSapling(Block sapling, int saplingMeta, ItemStack... windfall) {
		this.sapling = sapling;
		this.saplingMeta = saplingMeta;
		this.windfall = windfall;
	}

	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {

		if (world.isAirBlock(x, y, z))
			return false;

		if (world.getBlock(x, y, z) == sapling)
			return true;

		if (saplingMeta >= 0)
			return world.getBlockMetadata(x, y, z) == saplingMeta;
		else
			return true;

	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (!block.isWood(world, x, y, z))
			return null;

		return new CropBlock(world, block, world.getBlockMetadata(x, y, z), new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {

		if (!StackUtils.equals(sapling, itemstack))
			return false;

		if (saplingMeta >= 0)
			return itemstack.getItemDamage() == saplingMeta;
		else
			return true;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for (ItemStack drop : windfall)
			if (drop.isItemEqual(itemstack))
				return true;
		return false;
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		return germling.copy().tryPlaceItemIntoWorld(Utils.getForestryPlayer(world, x, y, z), world, x, y - 1, z, 1, 0, 0, 0);
	}

}
