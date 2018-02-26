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
package forestry.arboriculture.charcoal;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import forestry.api.arboriculture.ICharcoalPileWall;

public class CharcoalPileWall implements ICharcoalPileWall {

	private final IBlockState blockState;
	private final Block block;
	private final int charcoalAmount;

	public CharcoalPileWall(IBlockState blockState, int charcoalAmount) {
		this.blockState = blockState;
		this.block = Blocks.AIR;
		this.charcoalAmount = charcoalAmount;
	}

	public CharcoalPileWall(Block block, int charcoalAmount) {
		this.blockState = Blocks.AIR.getDefaultState();
		this.block = block;
		this.charcoalAmount = charcoalAmount;
	}

	@Override
	public int getCharcoalAmount() {
		return charcoalAmount;
	}

	@Override
	public boolean matches(IBlockState state) {
		return block == state.getBlock() || blockState == state;
	}

	@Override
	public NonNullList<ItemStack> getDisplayItems() {
		if (block == Blocks.AIR) {
			return NonNullList.withSize(1, new ItemStack(blockState.getBlock(), 1, blockState.getBlock().getMetaFromState(blockState)));
		} else if (blockState == Blocks.AIR.getDefaultState()) {
			return NonNullList.withSize(1, new ItemStack(block));
		}
		return NonNullList.create();
	}

	@Override
	public NonNullList<ItemStack> getDisplyItems() {
		return getDisplayItems();
	}

}
