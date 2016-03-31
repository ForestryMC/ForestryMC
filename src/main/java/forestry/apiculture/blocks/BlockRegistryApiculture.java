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

import net.minecraft.item.ItemStack;

import forestry.api.core.Tabs;
import forestry.apiculture.items.ItemBlockCandle;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryApiculture extends BlockRegistry {
	public final BlockApiculture apiculture;
	public final BlockBase<BlockTypeApicultureTesr> apicultureChest;
	public final BlockBeeHives beehives;
	public final BlockCandle candle;
	public final BlockStump stump;
	private final Map<BlockAlvearyType, BlockAlveary> alvearyBlockMap;

	public BlockRegistryApiculture() {
		apiculture = registerBlock(new BlockApiculture(), ItemBlockForestry.class, "apiculture");

		apicultureChest = registerBlock(new BlockBase<>(BlockTypeApicultureTesr.class), ItemBlockForestry.class, "apicultureChest");
		apicultureChest.setCreativeTab(Tabs.tabApiculture);
		apicultureChest.setHarvestLevel("axe", 0);

		beehives = registerBlock(new BlockBeeHives(), ItemBlockForestry.class, "beehives");

		candle = registerBlock(new BlockCandle(), ItemBlockCandle.class, "candle");
		stump = registerBlock(new BlockStump(), ItemBlockForestry.class, "stump");

		alvearyBlockMap = BlockAlveary.create();
		for (BlockAlveary block : alvearyBlockMap.values()) {
			registerBlock(block, new ItemBlockForestry(block), "alveary." + block.getAlvearyType());
		}
	}

	public ItemStack getAlvearyBlock(BlockAlvearyType type) {
		BlockAlveary alvearyBlock = alvearyBlockMap.get(type);
		return new ItemStack(alvearyBlock);
	}
}
