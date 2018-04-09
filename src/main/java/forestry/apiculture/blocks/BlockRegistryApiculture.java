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

import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.apiculture.items.ItemBlockCandle;
import forestry.apiculture.items.ItemBlockHoneyComb;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryApiculture extends BlockRegistry {
	public final BlockApiculture apiary;
	public final BlockApiculture beeHouse;
	public final BlockBase<BlockTypeApicultureTesr> beeChest;
	public final BlockBeeHives beehives;
	public final BlockCandle candle;
	public final BlockStump stump;
	public final BlockHoneyComb[] beeCombs;
	private final Map<BlockAlvearyType, BlockAlveary> alvearyBlockMap;

	public BlockRegistryApiculture() {
		apiary = new BlockApiculture(BlockTypeApiculture.APIARY);
		registerBlock(apiary, new ItemBlockForestry<>(apiary), "apiary");

		beeHouse = new BlockApiculture(BlockTypeApiculture.BEE_HOUSE);
		registerBlock(beeHouse, new ItemBlockForestry<>(beeHouse), "bee_house");

		beeChest = new BlockBase<>(BlockTypeApicultureTesr.APIARIST_CHEST, Material.WOOD);
		registerBlock(beeChest, new ItemBlockForestry<>(beeChest), "bee_chest");
		beeChest.setCreativeTab(Tabs.tabApiculture);
		beeChest.setHarvestLevel("axe", 0);

		beehives = new BlockBeeHives();
		registerBlock(beehives, new ItemBlockForestry<>(beehives), "beehives");

		candle = new BlockCandle();
		registerBlock(candle, new ItemBlockCandle(candle), "candle");
		stump = new BlockStump();
		registerBlock(stump, new ItemBlockForestry<>(stump), "stump");
		
		beeCombs = BlockHoneyComb.create();
		for(int i = 0;i < beeCombs.length;i++){
			BlockHoneyComb block = beeCombs[i];
			registerBlock(block, new ItemBlockHoneyComb(block), "bee_combs_" + i);
		}

		alvearyBlockMap = BlockAlveary.create();
		for (BlockAlveary block : alvearyBlockMap.values()) {
			registerBlock(block, new ItemBlockForestry<>(block), "alveary." + block.getAlvearyType());
		}
	}

	public BlockAlveary getAlvearyBlock(BlockAlvearyType type) {
		BlockAlveary alvearyBlock = alvearyBlockMap.get(type);
		return alvearyBlock;
	}

	public ItemStack getAlvearyBlockStack(BlockAlvearyType type) {
		BlockAlveary alvearyBlock = alvearyBlockMap.get(type);
		return new ItemStack(alvearyBlock);
	}
}
