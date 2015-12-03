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

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.arboriculture.genetics.TreeGenome;
import forestry.core.utils.vect.Vect;
import forestry.plugins.PluginArboriculture;

public class FarmableGE implements IFarmable {

	@Override
	public boolean isSaplingAt(World world, int x, int y, int z) {

		if (world.isAirBlock(x, y, z)) {
			return false;
		}

		Block block = world.getBlock(x, y, z);
		return PluginArboriculture.blocks.saplingGE == block;
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		if (!block.isWood(world, x, y, z)) {
			return null;
		}

		return new CropBlock(world, block, world.getBlockMetadata(x, y, z), new Vect(x, y, z));
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, int x, int y, int z) {
		return germling.copy().tryPlaceItemIntoWorld(player, world, x, y - 1, z, 1, 0, 0, 0);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		IAlleleTreeSpecies tree = TreeGenome.getSpecies(itemstack);
		return tree != null;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
