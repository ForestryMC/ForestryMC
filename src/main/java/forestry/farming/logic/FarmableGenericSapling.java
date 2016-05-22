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
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;

public class FarmableGenericSapling implements IFarmable {
	protected final ItemStack germling;
	protected final IBlockState plantedState;
	private final boolean replant;
	private final ItemStack[] windfall;

	public FarmableGenericSapling(ItemStack germling, IBlockState plantedState, boolean replant, ItemStack... windfall) {
		this.germling = germling;
		this.plantedState = plantedState;
		this.replant = replant;
		this.windfall = windfall;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() == plantedState.getBlock();
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		Block block = blockState.getBlock();
		if (!block.isWood(world, pos)) {
			return null;
		}

		if (replant) {
			return new CropDestroy(world, blockState, pos, plantedState);
		} else {
			return new CropDestroy(world, blockState, pos, null);
		}
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStack.areItemsEqual(germling, itemstack);
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
		return germling.copy().onItemUse(player, world, pos.down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0) == EnumActionResult.SUCCESS;
	}

}
