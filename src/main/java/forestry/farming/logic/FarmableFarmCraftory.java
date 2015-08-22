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

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;
import forestry.plugins.PluginFarmCraftory;

public class FarmableFarmCraftory implements IFarmable {

	Collection<ItemStack> germlings;
	Collection<ItemStack> windfall;

	public FarmableFarmCraftory(Collection<ItemStack> germlings, Collection<ItemStack> windfall) {
		this.germlings = germlings;
		this.windfall = windfall;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {

		if (world.isAirBlock(pos)) {
			return false;
		}

		Block block = world.getBlockState(pos).getBlock();
		return block == PluginFarmCraftory.blockSingle || block == PluginFarmCraftory.blockMulti;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (block != PluginFarmCraftory.blockSingle && block != PluginFarmCraftory.blockMulti) {
			return null;
		}
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) {
			return null;
		}
		if (PluginFarmCraftory.getGrowthStage(tile) < 2) {
			return null;
		}
		IBlockState state = world.getBlockState(pos);
		return new CropBlock(world, block, state.getBlock().getMetaFromState(state), new Vect(pos));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		for (ItemStack seed : germlings) {
			if (StackUtils.isIdenticalItem(seed, itemstack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return germling.copy().onItemUse(player, world, pos.down(), EnumFacing.UP, 0, 0, 0);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for (ItemStack fruit : windfall) {
			if (StackUtils.isIdenticalItem(fruit, itemstack)) {
				return true;
			}
		}

		return false;
	}

}
