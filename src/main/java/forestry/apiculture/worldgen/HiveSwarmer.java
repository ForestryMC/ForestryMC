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
package forestry.apiculture.worldgen;

import forestry.api.apiculture.hives.HiveGround;
import forestry.apiculture.gadgets.TileSwarm;
import forestry.core.config.ForestryBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class HiveSwarmer extends HiveGround {

	private final ItemStack[] bees;

	public HiveSwarmer(float genChance, ItemStack... bees) {
		super(ForestryBlock.beehives.block(), 8, genChance, Blocks.dirt, Blocks.grass);
		this.bees = bees;
	}

	@Override
	public void postGen(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileSwarm)
			((TileSwarm) tile).setContained(bees);
	}

}
