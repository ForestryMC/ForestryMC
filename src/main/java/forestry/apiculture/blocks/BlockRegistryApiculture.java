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
package forestry.apiculture.blocks;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.ToolType;

import forestry.api.apiculture.hives.IHiveRegistry;
import forestry.api.core.ItemGroups;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.ItemBlockCandle;
import forestry.apiculture.items.ItemBlockHoneyComb;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockBase;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockWallForestry;

public class BlockRegistryApiculture extends BlockRegistry {
	public final BlockApiculture apiary;
	public final BlockApiculture beeHouse;
	public final BlockBase<BlockTypeApicultureTesr> beeChest;
	public final Map<IHiveRegistry.HiveType, BlockBeeHive> beehives = new EnumMap<>(IHiveRegistry.HiveType.class);
	public final BlockCandle candle;
	public final BlockCandleWall candleWall;
	public final BlockStump stump;
	public final BlockStumpWall stumpWall;
	public final Map<EnumHoneyComb, BlockHoneyComb> beeCombs = new EnumMap<>(EnumHoneyComb.class);
	public final Map<BlockAlvearyType, BlockAlveary> alvearyBlockMap = new EnumMap<>(BlockAlvearyType.class);

	public BlockRegistryApiculture() {
		apiary = new BlockApiculture(BlockTypeApiculture.APIARY);
		registerBlock(apiary, new ItemBlockForestry<>(apiary, new Item.Properties().group(ItemGroups.tabApiculture)), "apiary");

		beeHouse = new BlockApiculture(BlockTypeApiculture.BEE_HOUSE);
		registerBlock(beeHouse, new ItemBlockForestry<>(beeHouse, new Item.Properties().group(ItemGroups.tabApiculture)), "bee_house");

		beeChest = new BlockBase<>(BlockTypeApicultureTesr.APIARIST_CHEST, Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).harvestLevel(0));
		registerBlock(beeChest, new ItemBlockBase<>(beeChest, new Item.Properties().group(ItemGroups.tabApiculture), BlockTypeApicultureTesr.APIARIST_CHEST), "bee_chest");

		//TODO tag?
		for (IHiveRegistry.HiveType type : IHiveRegistry.HiveType.VALUES) {
			BlockBeeHive hive = new BlockBeeHive(type);
			registerBlock(hive, new ItemBlockForestry<>(hive, new Item.Properties().group(type == IHiveRegistry.HiveType.SWARM ? null : ItemGroups.tabApiculture)), "beehive_" + type.getName());
			beehives.put(type, hive);
		}

		candle = new BlockCandle();
		candleWall = new BlockCandleWall();
		registerBlock(candle, new ItemBlockCandle(candle, candleWall), "candle");
		registerBlock(candleWall, "candle_wall");
		stump = new BlockStump();
		stumpWall = new BlockStumpWall();
		registerBlock(stump, new ItemBlockWallForestry<>(stump, stumpWall, new Item.Properties().group(ItemGroups.tabApiculture)), "stump");
		registerBlock(stumpWall, "stump_wall");

		for (EnumHoneyComb type : EnumHoneyComb.VALUES) {
			BlockHoneyComb block = new BlockHoneyComb(type);
			registerBlock(block, new ItemBlockHoneyComb(block), "block_bee_comb_" + type.getName());
		}    //TODO tag?

		for (BlockAlvearyType type : BlockAlvearyType.VALUES) {
			BlockAlveary block = new BlockAlveary(type);
			registerBlock(block, new ItemBlockForestry<>(block, new Item.Properties().group(ItemGroups.tabApiculture)), "alveary_" + block.getType());
			alvearyBlockMap.put(type, block);
		}
	}

	public ItemStack getCombBlock(EnumHoneyComb honeyComb) {
		return new ItemStack(beeCombs.get(honeyComb));
	}

	public BlockAlveary getAlvearyBlock(BlockAlvearyType type) {
		return alvearyBlockMap.get(type);
	}

	public ItemStack getAlvearyBlockStack(BlockAlvearyType type) {
		BlockAlveary alvearyBlock = alvearyBlockMap.get(type);
		return new ItemStack(alvearyBlock);
	}
}
