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
package forestry.core.items;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import forestry.core.config.ForestryBlock;

public class ItemForestryShovel extends ItemForestryTool {

	private static final Block blocksEffectiveAgainst[];

	static {
		blocksEffectiveAgainst = (new Block[] { Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel,
				Blocks.snow_layer, Blocks.snow, Blocks.clay, Blocks.farmland, ForestryBlock.soil.block() });
	}

	public ItemForestryShovel(ItemStack remnants) {
		super(blocksEffectiveAgainst, remnants);
	}

}
