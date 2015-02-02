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

import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.vect.Vect;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Collection;

public class CropBasicFruit extends Crop {

	protected final Block block;
	protected final int meta;

	public CropBasicFruit(World world, Block block, int meta, Vect position) {
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
		Collection<ItemStack> harvested = block.getDrops(world, pos.x, pos.y, pos.z, meta, 0);
		Proxies.common.addBlockDestroyEffects(world, pos.x, pos.y, pos.z, block, 0);
		world.setBlock(pos.x, pos.y, pos.z, block, 0, Defaults.FLAG_BLOCK_SYNCH);
		return harvested;
	}

	@Override
	public String toString() {
		return String.format("CropBasicFruit [ position: [ %s ]; block: %s; meta: %s ]", position.toString(), block.getUnlocalizedName(), meta);
	}
}
