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

import forestry.api.arboriculture.ITree;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.config.ForestryBlock;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;
import forestry.plugins.PluginArboriculture;

public class FarmableGE implements IFarmable {

	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {

		if (world.isAirBlock(x, y, z))
			return false;

		return world.getBlock(x, y, z) == ForestryBlock.saplingGE;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		if (block.isAir(world, x, y, z) || !block.isWood(world, x, y, z))
			return null;

		return new CropBlock(world, block, world.getBlockMetadata(x, y, z), new Vect(x, y, z));
	}

	@Override
	public boolean plantSaplingAt(ItemStack germling, World world, int x, int y, int z) {
		return germling.copy().tryPlaceItemIntoWorld(Utils.getForestryPlayer(world, x, y, z), world, x, y - 1, z, 1, 0, 0, 0);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		ITree tree = PluginArboriculture.treeInterface.getMember(itemstack);
		return tree != null;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
