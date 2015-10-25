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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.gadgets.TileForestry;

public class BlockTypeTileForestry implements IBlockType {

	private final Block block;
	private final int meta;
	private EnumFacing direction;

	public BlockTypeTileForestry(ForestryBlock forestryBlock, int meta) {
		this.block = forestryBlock.block();
		this.meta = meta;
	}

	@Override
	public void setDirection(EnumFacing facing) {
		this.direction = facing;
	}

	@Override
	public void setBlock(World world, BlockPos pos) {
		boolean placed = world.setBlockState(pos, block.getStateFromMeta(meta), Defaults.FLAG_BLOCK_SYNCH_AND_UPDATE);
		if (!placed) {
			return;
		}

		Block worldBlock = world.getBlockState(pos).getBlock();
		if (!Block.isEqualTo(block, worldBlock)) {
			return;
		}

		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileForestry)) {
			world.setBlockToAir(pos);
			return;
		}

		TileForestry tileForestry = (TileForestry) tile;

		tileForestry.setOrientation(direction);
	}

	@Override
	public int getMeta() {
		return meta;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(block, 1, meta);
	}
}
