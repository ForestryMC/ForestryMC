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

import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.core.items.ItemRegistry;

public class ItemRegistryArboriculture extends ItemRegistry {
	public final ItemGermlingGE sapling;
	public final ItemGermlingGE pollenFertile;
	public final ItemGrafter grafter;
	public final ItemGrafter grafterProven;

	public ItemRegistryArboriculture() {
		sapling = registerItem(new ItemGermlingGE(EnumGermlingType.SAPLING), "sapling");
		pollenFertile = registerItem(new ItemGermlingGE(EnumGermlingType.POLLEN), "pollen_fertile");

		grafter = registerItem(new ItemGrafter(9), "grafter");
		grafterProven = registerItem(new ItemGrafter(149), "grafter_proven");
	}
}
