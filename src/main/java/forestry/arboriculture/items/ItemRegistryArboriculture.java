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
package forestry.arboriculture.items;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.core.items.ItemRegistry;

public class ItemRegistryArboriculture extends ItemRegistry {
	public final ItemGermlingGE sapling;
	public final ItemGermlingGE pollenFertile;
	public final ItemTreealyzer treealyzer;
	public final ItemGrafter grafter;
	public final ItemGrafter grafterProven;

	public ItemRegistryArboriculture() {
		sapling = registerItem(new ItemGermlingGE(EnumGermlingType.SAPLING), "sapling");
		OreDictionary.registerOre("treeSapling", sapling.getWildcard());
		
		pollenFertile = registerItem(new ItemGermlingGE(EnumGermlingType.POLLEN), "pollenFertile");
		treealyzer = registerItem(new ItemTreealyzer(), "treealyzer");
		grafter = registerItem(new ItemGrafter(4), "grafter");
		grafterProven = registerItem(new ItemGrafter(149), "grafterProven");
	}
}
