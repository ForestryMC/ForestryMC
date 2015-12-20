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
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.config.Constants;
import forestry.core.utils.BlockPosUtil;
import forestry.core.utils.ItemStackUtil;

public class FarmableStacked implements IFarmable {

	private final Block block;
	private final int matureHeight;
	private final int matureMeta;

	public FarmableStacked(Block block, int matureHeight, int matureMeta) {
		this.block = block;
		this.matureHeight = matureHeight;
		this.matureMeta = matureMeta;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {
		return BlockPosUtil.getBlock(world, pos) == block;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		BlockPos posB = new BlockPos(pos.getX(), pos.getY() + (matureHeight - 1), pos.getZ());
		if (BlockPosUtil.getBlock(world, posB) != block) {
			return null;
		}
		return new CropBlock(world, block, matureMeta, posB);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStackUtil.equals(block, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return world.setBlockState(pos, block.getStateFromMeta(0), Constants.FLAG_BLOCK_SYNCH);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

}
