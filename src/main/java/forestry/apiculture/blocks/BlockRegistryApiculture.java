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

import forestry.api.core.Tabs;
import forestry.apiculture.items.ItemBlockCandle;
import forestry.core.blocks.BlockBase;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryApiculture extends BlockRegistry {
	public final BlockApiculture apiculture;
	public final BlockBase apicultureChest;
	public final BlockBeehives beehives;
	public final BlockCandle candle;
	public final BlockStump stump;
	public final BlockAlveary alveary;

	public BlockRegistryApiculture() {
		apiculture = registerBlock(new BlockApiculture(), ItemBlockForestry.class, "apiculture");

		apicultureChest = registerBlock(new BlockBase(true), ItemBlockForestry.class, "apicultureChest");
		apicultureChest.setCreativeTab(Tabs.tabApiculture);
		apicultureChest.setHarvestLevel("axe", 0);

		beehives = registerBlock(new BlockBeehives(), ItemBlockForestry.class, "beehives");

		candle = registerBlock(new BlockCandle(), ItemBlockCandle.class, "candle");
		stump = registerBlock(new BlockStump(), ItemBlockForestry.class, "stump");

		alveary = registerBlock(new BlockAlveary(), ItemBlockForestry.class, "alveary");
	}
}
