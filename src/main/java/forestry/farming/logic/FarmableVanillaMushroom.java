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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockPosUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.plugins.PluginFarming;

public class FarmableVanillaMushroom extends FarmableGenericSapling {

	public FarmableVanillaMushroom(Block sapling, int saplingMeta) {
		super(sapling, saplingMeta);
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos) {
		Block block = BlockPosUtil.getBlock(world, pos);

		if (block != Blocks.brown_mushroom_block && block != Blocks.red_mushroom_block) {
			return null;
		}

		return new CropBlock(world, block, BlockPosUtil.getBlockMeta(world, pos), pos);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStackUtil.equals(sapling, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		int meta = 0;
		if (ItemStackUtil.equals(Blocks.red_mushroom, germling)) {
			meta = 1;
		}

		Proxies.common.addBlockPlaceEffects(world, pos, Blocks.brown_mushroom.getStateFromMeta(0));
		return world.setBlockState(pos, PluginFarming.blocks.mushroom.getStateFromMeta(meta), Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}
}
