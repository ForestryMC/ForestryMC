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

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.plugins.compat.deprecated.PluginIC2;

public class CropRubber extends CropBlock {

	public CropRubber(World world, Block block, int meta, BlockPos position) {
		super(world, block, meta, position);
	}

	@Override
	protected Collection<ItemStack> harvestBlock(BlockPos pos) {
		Collection<ItemStack> harvested = new ArrayList<>();
		harvested.add(PluginIC2.resin.copy());
		Proxies.common.addBlockDestroyEffects(world, pos, block.getStateFromMeta(0));
		world.setBlockState(pos, block.getStateFromMeta(meta + 6), Constants.FLAG_BLOCK_SYNCH);
		return harvested;
	}

}
