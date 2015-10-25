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
package forestry.core.worldgen;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.core.config.Defaults;

public class BlockType implements IBlockType {

	private final Block block;
	protected int meta;

	public BlockType(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}

	@Override
	public void setDirection(EnumFacing facing) {

	}

	@Override
	public void setBlock(World world, BlockPos pos) {
		world.setBlockState(pos, block.getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}

	@Override
	public int getMeta() {
		return this.meta;
	}

	@Override
	public Block getBlock() {
		return this.block;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(block, 1, meta);
	}
}
