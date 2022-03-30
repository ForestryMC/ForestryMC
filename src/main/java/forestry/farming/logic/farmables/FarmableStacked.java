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
package forestry.farming.logic.farmables;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.utils.BlockUtil;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableStacked implements IFarmable {
	protected final ItemStack germling;
	protected final Block cropBlock;
	protected final int matureHeight;
	protected final ItemStack fruit;

	public FarmableStacked(ItemStack germling, Block cropBlock, int matureHeight) {
		this(germling, germling, cropBlock, matureHeight);
	}

	public FarmableStacked(ItemStack germling, ItemStack fruit, Block cropBlock, int matureHeight) {
		this.germling = germling;
		this.fruit = fruit;
		this.cropBlock = cropBlock;
		this.matureHeight = matureHeight;
	}

	@Override
	public boolean isSaplingAt(Level world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == cropBlock;
	}

	@Override
	public ICrop getCropAt(Level world, BlockPos pos, BlockState blockState) {
		BlockPos cropPos = pos.offset(0, matureHeight - 1, 0);
		blockState = world.getBlockState(cropPos);
		if (blockState.getBlock() != cropBlock) {
			return null;
		}

		return new CropDestroy(world, blockState, cropPos, null);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStack.isSame(germling, itemstack);
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		info.addSeedlings(germling);
		info.addProducts(fruit);
	}

	@Override
	public boolean plantSaplingAt(Player player, ItemStack germling, Level world, BlockPos pos) {
		return BlockUtil.setBlockWithPlaceSound(world, pos, cropBlock.defaultBlockState());
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}
}
