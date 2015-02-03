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
import forestry.core.utils.StackUtils;
import forestry.core.vect.Vect;

public class FarmableGenericSapling implements IFarmable {

	private final Block sapling;
	private final int saplingMeta;
	private final ItemStack[] windfall;

	public FarmableGenericSapling(Block sapling, int saplingMeta, ItemStack... windfall) {
		this.sapling = sapling;
		this.saplingMeta = saplingMeta;
		this.windfall = windfall;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {

		if (world.isAirBlock(pos)) {
			return false;
		}

		if (world.getBlockState(pos).getBlock() == sapling) {
			return true;
		}

		if (saplingMeta >= 0) {
			return world.getBlockMetadata(x, y, z) == saplingMeta;
		} else {
			return true;
		}

	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!block.isWood(world, pos)) {
			return null;
		}

		return new CropBlock(world, block, world.getBlockMetadata(x, y, z), new Vect(pos));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {

		if (!StackUtils.equals(sapling, itemstack)) {
			return false;
		}

		if (saplingMeta >= 0) {
			return itemstack.getItemDamage() == saplingMeta;
		} else {
			return true;
		}
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

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return germling.copy().onItemUse(player, world, pos.down(), EnumFacing.UP, 0, 0, 0);
	}

}
