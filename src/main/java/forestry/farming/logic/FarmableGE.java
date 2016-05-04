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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.genetics.TreeGenome;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.vect.Vect;

public class FarmableGE implements IFarmable {

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {

		if (world.isAirBlock(pos)) {
			return false;
		}

		Block block = BlockUtil.getBlock(world, pos);
		return PluginArboriculture.blocks.saplingGE == block;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		Block block = BlockUtil.getBlock(world, pos);

		if (!block.isWood(world, pos)) {
			return null;
		}

		return new CropBlock(world, block, BlockUtil.getBlockMetadata(world, pos), new Vect(pos));
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return germling.copy().onItemUse(player, world, pos.down(), EnumFacing.UP, 0, 0, 0);
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
