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

public class ItemForestryPickaxe extends ItemForestryTool {

	private static Block blocksEffectiveAgainst[];

	static {
		blocksEffectiveAgainst = (new Block[] { Blocks.cobblestone, Blocks.stone, Blocks.sandstone, Blocks.mossy_cobblestone, Blocks.iron_ore, Blocks.iron_block,
				Blocks.coal_ore, Blocks.gold_block, Blocks.brick_block, Blocks.nether_brick, Blocks.netherrack, Blocks.gold_ore, Blocks.diamond_ore, Blocks.diamond_block,
				Blocks.ice, Blocks.netherrack, Blocks.lapis_ore, Blocks.lapis_block, ForestryBlock.resources, ForestryBlock.beehives, ForestryBlock.engine,
				ForestryBlock.factoryTESR, ForestryBlock.factoryPlain });
	}

	public ItemForestryPickaxe(ItemStack remnants) {
		super(blocksEffectiveAgainst, remnants);
	}

}
