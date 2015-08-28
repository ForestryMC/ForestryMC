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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.vect.Vect;

public class CropBasicAgriCraft extends Crop {

	private final Block block;
	private final int meta;

	public CropBasicAgriCraft(World world, Block block, int meta, Vect position) {
		super(world, position);
		this.block = block;
		this.meta = meta;
	}

	@Override
	protected boolean isCrop(Vect pos) {
		return getBlock(pos) == block && getBlockMeta(pos) == meta;
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		IBlockState state = world.getBlockState(pos.pos);
		List<ItemStack> harvest = block.getDrops(world, pos.pos, block.getStateFromMeta(meta), 0);
		if (harvest.size() > 1) {
			harvest.remove(1); //AgriCraft returns cropsticks in 0, seeds in 1 in getDrops, removing since harvesting doesn't return them.
		}
		harvest.remove(0);
		Proxies.common.addBlockDestroyEffects(world, pos.pos, Blocks.melon_block.getStateFromMeta(0));
		world.setBlockState(pos.pos, block.getStateFromMeta(0), Defaults.FLAG_BLOCK_SYNCH);
		return harvest;
	}

	@Override
	public String toString() {
		return String.format("CropBasicAgriCraft [ position: [ %s ]; block: %s; meta: %s ]", position.toString(), block.getUnlocalizedName(), meta);
	}
}
