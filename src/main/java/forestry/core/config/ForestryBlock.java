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
package forestry.core.config;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.proxy.Proxies;
import forestry.plugins.PluginManager;
import forestry.plugins.PluginManager.Stage;

/**
 * Allows direct access to Forestry's blocks. Will be populated during BaseMod.load().
 *
 * All of this stuff is metadata sensitive which is not reflected here!
 *
 * Make sure to only reference it in modsLoaded() or later.
 *
 * @author SirSengir
 */
public enum ForestryBlock {

	/**
	 * 0 - Humus 1 - Bog Earth
	 */
	soil,
	/**
	 * 0 - Apatite Ore 1 - Copper Ore 2 - Tin Ore
	 */
	resources,
	resourceStorage,
	/**
	 * 0 - Legacy 1 - Forest Hive 2 - Meadows Hive
	 */
	beehives,
	mushroom,
	candle,
	stump,
	glass,
	planks1,
	planks2,
	fireproofPlanks1,
	fireproofPlanks2,
	slabs1,
	slabs2,
	slabs3,
	slabs4,
	log1,
	log2,
	log3,
	log4,
	log5,
	log6,
	log7,
	log8,
	fireproofLog1,
	fireproofLog2,
	fireproofLog3,
	fireproofLog4,
	fireproofLog5,
	fireproofLog6,
	fireproofLog7,
	fireproofLog8,
	fences1,
	fences2,
	stairs,
	saplingGE,
	leaves,
	pods,
	arboriculture,
	alveary,
	farm,
	core,
	apiculture,
	mail,
	engine,
	factoryTESR,
	factoryPlain,
	lepidopterology;
	private Block block;

	public void registerBlock(Block block, Class<? extends ItemBlock> itemClass, String name) {
		if (!EnumSet.of(Stage.PRE_INIT).contains(PluginManager.getStage())) {
			throw new RuntimeException("Tried to register Block outside of Pre-Init");
		}
		this.block = block;
		block.setBlockName("for." + name);
		Proxies.common.registerBlock(block, itemClass);
	}

	public boolean isItemEqual(ItemStack stack) {
		return stack != null && isBlockEqual(Block.getBlockFromItem(stack.getItem()));
	}

	public boolean isBlockEqual(Block i) {
		return i != null && Block.isEqualTo(block, i);
	}

	public boolean isBlockEqual(World world, int x, int y, int z) {
		return isBlockEqual(world.getBlock(x, y, z));
	}

	public Item item() {
		return Item.getItemFromBlock(block);
	}

	public Block block() {
		return block;
	}

	public ItemStack getWildcard() {
		return getItemStack(1, OreDictionary.WILDCARD_VALUE);
	}

	public ItemStack getItemStack() {
		return getItemStack(1, 0);
	}

	public ItemStack getItemStack(int qty) {
		return getItemStack(qty, 0);
	}

	public ItemStack getItemStack(int qty, int meta) {
		if (block == null) {
			return null;
		}
		return new ItemStack(block, qty, meta);
	}

	public boolean setBlock(World world, int x, int y, int z, int meta, int flag) {
		return world.setBlock(x, y, z, block, meta, flag);
	}
}
