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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockPosUtil;

public class FarmableGenericCrop implements IFarmable {

	private final ItemStack seed;
	private final Block block;
	private final int mature;
	private final ItemStack[] windfall;

	public FarmableGenericCrop(ItemStack seed, Block block, int mature, ItemStack... windfall) {
		this.seed = seed;
		this.block = block;
		this.mature = mature;
		this.windfall = windfall;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {
		return BlockPosUtil.getBlock(world, pos) == block;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		if (BlockPosUtil.getBlock(world, pos) != block) {
			return null;
		}
		if (BlockPosUtil.getBlockMeta(world, pos) != mature) {
			return null;
		}

		return new CropBlock(world, block, mature, pos);
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
		return germling.copy().onItemUse(player, world, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()), EnumFacing.UP, 0, 0, 0);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		for (ItemStack drop : windfall) {
			if (drop.isItemEqual(itemstack)) {
				return true;
			}
		}
		return false;
	}

}
