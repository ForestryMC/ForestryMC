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
package forestry.mail.items;

import forestry.core.items.ItemRegistry;
import forestry.core.utils.OreDictUtil;
import net.minecraft.item.ItemStack;

public class ItemRegistryMail extends ItemRegistry {
	public final ItemStamps stamps;
	public final ItemLetter letters;
	public final ItemCatalogue catalogue;

	public ItemRegistryMail() {
		stamps = registerItem(new ItemStamps(), "stamps");
		letters = registerItem(new ItemLetter(), "letters");
		catalogue = registerItem(new ItemCatalogue(), "catalogue");

		for (ItemStack itemStack : letters.getEmptiedLetters()) {
			registerOreDict(OreDictUtil.EMPTIED_LETTER_ORE_DICT, itemStack);
		}
	}
}
