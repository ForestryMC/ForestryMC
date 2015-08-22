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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.vect.Vect;

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
	public boolean isSaplingAt(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() == block;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() != block) {
			return null;
		}
		IBlockState state = world.getBlockState(pos);
		if (block.getMetaFromState(state) != mature) {
			return null;
		}

		return new CropBlock(world, block, mature, new Vect(pos));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		if (seed.getItem() != itemstack.getItem()) {
			return false;
		}

		if (seed.getItemDamage() >= 0) {
			return seed.getItemDamage() == itemstack.getItemDamage();
		} else {
			return true;
		}
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return germling.copy().onItemUse(player, world, pos.down(), EnumFacing.UP, 0, 0, 0);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
