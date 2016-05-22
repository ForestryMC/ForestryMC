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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;

public class FarmableStacked implements IFarmable {
	protected final ItemStack germling;
	protected final Block cropBlock;
	protected final int matureHeight;

	public FarmableStacked(ItemStack germling, Block cropBlock, int matureHeight) {
		this.germling = germling;
		this.cropBlock = cropBlock;
		this.matureHeight = matureHeight;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() == cropBlock;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		BlockPos cropPos = pos.add(0, matureHeight - 1, 0);
		blockState = world.getBlockState(cropPos);
		if (blockState.getBlock() != cropBlock) {
			return null;
		}

		return new CropDestroy(world, blockState, cropPos, null);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStack.areItemsEqual(germling, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		IBlockState plantedState = cropBlock.getDefaultState();
		return world.setBlockState(pos, plantedState);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}
}
