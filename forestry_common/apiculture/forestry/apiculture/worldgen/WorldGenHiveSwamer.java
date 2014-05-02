/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.worldgen;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import forestry.apiculture.gadgets.TileSwarm;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class WorldGenHiveSwamer extends WorldGenHive {

	private ItemStack[] bees;

	public WorldGenHiveSwamer(ItemStack[] bees) {
		this.bees = bees;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		if (world.blockExists(x, y, z) && world.isAirBlock(x, y, z) && (!world.isAirBlock(x, y - 1, z) || !world.isAirBlock(x, y + 1, z))) {
			setHive(world, x, y, z, 8);
			return true;
		}

		return false;
	}

	@Override
	protected void postGen(World world, int x, int y, int z, int meta) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileSwarm)
			((TileSwarm) tile).setContained(bees);
	}
}
