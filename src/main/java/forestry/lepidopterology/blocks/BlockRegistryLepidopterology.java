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
package forestry.lepidopterology.blocks;

import net.minecraft.item.Item;

import forestry.api.core.ItemGroups;
import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockBase;
import forestry.core.items.ItemBlockForestry;

public class BlockRegistryLepidopterology extends BlockRegistry {
	public final BlockLepidopterology butterflyChest;
	public final BlockCocoon cocoon;
	public final BlockSolidCocoon solidCocoon;

	public BlockRegistryLepidopterology() {
		butterflyChest = new BlockLepidopterology(BlockTypeLepidopterologyTesr.LEPICHEST);
		registerBlock(butterflyChest, new ItemBlockBase<>(butterflyChest, new Item.Properties().group(ItemGroups.tabLepidopterology), BlockTypeLepidopterologyTesr.LEPICHEST), "butterfly_chest");

		cocoon = new BlockCocoon();
		registerBlock(cocoon, new ItemBlockForestry<>(cocoon), "cocoon");

		solidCocoon = new BlockSolidCocoon();
		registerBlock(solidCocoon, new ItemBlockForestry<>(solidCocoon), "cocoon.solid");
	}
}
