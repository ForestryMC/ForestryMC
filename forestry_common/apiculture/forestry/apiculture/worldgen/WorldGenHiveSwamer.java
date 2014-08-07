/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
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
