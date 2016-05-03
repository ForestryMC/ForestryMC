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
package forestry.arboriculture.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.world.ITreeGenData;
import forestry.core.config.Constants;
import forestry.core.utils.ItemStackUtil;
import forestry.core.worldgen.IBlockType;

public class BlockTypeWood implements IBlockType, ITreeBlockType {
	protected Block block;
	protected IBlockState state;

	public BlockTypeWood(ItemStack itemStack) {
		block = ItemStackUtil.getBlock(itemStack);
		if (block == null) {
			throw new NullPointerException("Couldn't find block for itemStack " + itemStack);
		}
		state = block.getStateFromMeta(itemStack.getMetadata());
	}

	@Override
	public void setBlock(World world, ITreeGenData tree, BlockPos pos) {
		setBlock(world, pos);
	}

	@Override
	public void setBlock(World world, BlockPos pos) {
		world.setBlockState(pos, state, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}

	@Override
	public void setDirection(EnumFacing facing) {

	}
}
