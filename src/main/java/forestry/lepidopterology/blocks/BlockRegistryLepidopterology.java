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

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockForestry;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;

public class BlockRegistryLepidopterology extends BlockRegistry {
	public final BlockLepidopterology butterflyChest;
	public final BlockCocoon cocoon;
	public final BlockSolidCocoon solidCocoon;

	public BlockRegistryLepidopterology() {
		butterflyChest = new BlockLepidopterology(BlockTypeLepidopterologyTesr.LEPICHEST);
		registerBlock(butterflyChest, new ItemBlockForestry<>(butterflyChest), "butterfly_chest");

		ButterflyAlleles.registerCocoonAlleles();
		cocoon = new BlockCocoon();
		registerBlock(cocoon, new ItemBlockForestry<>(cocoon), "cocoon");

		solidCocoon = new BlockSolidCocoon();
		registerBlock(solidCocoon, new ItemBlockForestry<>(solidCocoon), "cocoon.solid");
	}
}
