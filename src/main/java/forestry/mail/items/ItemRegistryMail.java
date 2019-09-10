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

import com.google.common.collect.ImmutableTable;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.item.ItemStack;

import forestry.core.items.ItemRegistry;

public class ItemRegistryMail extends ItemRegistry {
	public final Map<EnumStampDefinition, ItemStamp> stamps = new EnumMap<>(EnumStampDefinition.class);
	public final ImmutableTable<ItemLetter.Size, ItemLetter.State, ItemLetter> letters;
	public final ItemCatalogue catalogue;

	public ItemRegistryMail() {
		for (EnumStampDefinition def : EnumStampDefinition.VALUES) {
			ItemStamp stamp = new ItemStamp(def);
			registerItem(stamp, "stamp_" + def.getUid());
			stamps.put(def, stamp);
		}

		ImmutableTable.Builder<ItemLetter.Size, ItemLetter.State, ItemLetter> builder = ImmutableTable.builder();
		for (ItemLetter.Size size : ItemLetter.Size.values()) {
			for (ItemLetter.State state : ItemLetter.State.values()) {
				ItemLetter letter = new ItemLetter(size, state);
				registerItem(letter, "letter_" + size.name().toLowerCase(Locale.ENGLISH) + "_" + state.name().toLowerCase(Locale.ENGLISH));
				builder.put(size, state, letter);
			}
		}
		letters = builder.build();

		catalogue = registerItem(new ItemCatalogue(), "catalogue");

		//TODO tags
		//		for (ItemStack itemStack : letters.getEmptiedLetters()) {
		//			registerOreDict(OreDictUtil.EMPTIED_LETTER_ORE_DICT, itemStack);
		//		}
	}


	public ItemStack getStamp(EnumStampDefinition stampInfo, int amount) {
		return new ItemStack(stamps.get(stampInfo), amount);
	}

	public ItemStack getLetter(ItemLetter.Size size, ItemLetter.State state, int amount) {
		return new ItemStack(letters.get(size, state), amount);
	}
}
