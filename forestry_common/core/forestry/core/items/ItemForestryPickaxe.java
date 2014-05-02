/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
