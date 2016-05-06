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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.farming.PluginFarming;

public class FarmableVanillaMushroom extends FarmableGenericSapling {

	public FarmableVanillaMushroom(Block sapling, int saplingMeta) {
		super(sapling, saplingMeta);
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();

		if (block != Blocks.BROWN_MUSHROOM_BLOCK && block != Blocks.RED_MUSHROOM_BLOCK) {
			return null;
		}

		return new CropBlock(world, block, block.getMetaFromState(blockState), pos);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStackUtil.equals(sapling, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		int meta = 0;
		if (ItemStackUtil.equals(Blocks.RED_MUSHROOM, germling)) {
			meta = 1;
		}

		Proxies.common.addBlockPlaceEffects(world, pos, Blocks.BROWN_MUSHROOM.getDefaultState());
		return world.setBlockState(pos, PluginFarming.blocks.mushroom.getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}
}
