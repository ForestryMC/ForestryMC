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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.core.items.ItemRegistry;
import forestry.core.utils.OreDictUtil;

public class ItemRegistryArboriculture extends ItemRegistry {
	public final ItemGermlingGE sapling;
	public final ItemGermlingGE pollenFertile;
	public final ItemCharcoal charcoal;
	public final ItemGrafter grafter;
	public final ItemGrafter grafterProven;

	public ItemRegistryArboriculture() {
		sapling = registerItem(new ItemGermlingGE(EnumGermlingType.SAPLING), "sapling");
		OreDictionary.registerOre(OreDictUtil.TREE_SAPLING, sapling.getWildcard());
		
		pollenFertile = registerItem(new ItemGermlingGE(EnumGermlingType.POLLEN), "pollenFertile");

		charcoal = registerItem(new ItemCharcoal(), "charcoal");
		registerOreDict(OreDictUtil.CHARCOAL, new ItemStack(charcoal, 1, OreDictionary.WILDCARD_VALUE));
		registerOreDict(OreDictUtil.CHARCOAL, new ItemStack(Items.COAL, 1, 1));

		grafter = registerItem(new ItemGrafter(9), "grafter");
		grafterProven = registerItem(new ItemGrafter(149), "grafterProven");
	}
}
