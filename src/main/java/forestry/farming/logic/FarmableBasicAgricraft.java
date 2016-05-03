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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.config.Constants;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.vect.Vect;

public class FarmableBasicAgricraft implements IFarmable {

	private final Block block;
	private final int matureMeta;

	public FarmableBasicAgricraft(Block block, int matureMeta) {
		this.block = block;
		this.matureMeta = matureMeta;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {
		return BlockUtil.getBlock(world, pos) == block;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		if (BlockUtil.getBlock(world, pos) != block) {
			return null;
		}
		if (BlockUtil.getBlockMetadata(world, pos) != matureMeta) {
			return null;
		}
		return new CropBasicAgriCraft(world, block, matureMeta, new Vect(pos));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStackUtil.equals(block, itemstack);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return world.setBlockState(pos, block.getDefaultState(), Constants.FLAG_BLOCK_SYNCH);
	}

}
