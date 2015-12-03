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
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.vect.Vect;
import forestry.plugins.PluginFarming;

public class FarmableVanillaMushroom extends FarmableGenericSapling {

	public FarmableVanillaMushroom(Block sapling, int saplingMeta) {
		super(sapling, saplingMeta);
	}

	@Override
	public ICrop getCropAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);

		if (block != Blocks.brown_mushroom_block && block != Blocks.red_mushroom_block) {
			return null;
		}

		return new CropBlock(world, block, world.getBlockMetadata(x, y, z), new Vect(x, y, z));
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStackUtil.equals(sapling, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, int x, int y, int z) {
		int meta = 0;
		if (ItemStackUtil.equals(Blocks.red_mushroom, germling)) {
			meta = 1;
		}

		Proxies.common.addBlockPlaceEffects(world, x, y, z, Blocks.brown_mushroom, 0);
		return world.setBlock(x, y, z, PluginFarming.blocks.mushroom, meta, Constants.FLAG_BLOCK_SYNCH_AND_UPDATE);
	}
}
