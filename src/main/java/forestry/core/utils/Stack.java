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
package forestry.core.utils;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Stack {
	private final String name;
	private final int meta;

	public Stack(String name, int meta) {
		this.name = name;
		this.meta = meta;
	}

	public static List<Stack> parseStackStrings(String itemStackStrings, int missingMetaValue) {
		String[] parts = itemStackStrings.split("(\\s*;\\s*)+");
		return parseStackStrings(parts, missingMetaValue);
	}

	public static List<Stack> parseStackStrings(String[] parts, int missingMetaValue) {

		List<Stack> stacks = new ArrayList<>();

		for (String itemStackString : parts) {
			Stack stack = parseStackString(itemStackString, missingMetaValue);
			if (stack != null) {
				stacks.add(stack);
			}
		}

		return stacks;
	}

	@Nullable
	public static Stack parseStackString(String stackString, int missingMetaValue) {
		stackString = stackString.trim();
		if (stackString.isEmpty()) {
			return null;
		}

		String[] parts = stackString.split(":+");

		if (parts.length != 2 && parts.length != 3) {
			Log.warning("Stack string (" + stackString + ") isn't formatted properly. Suitable formats are <modId>:<name>, <modId>:<name>:<meta> or <modId>:<name>:*, e.g. IC2:blockWall:*");
			return null;
		}

		String name = parts[0] + ':' + parts[1];
		int meta;

		if (parts.length == 2) {
			meta = missingMetaValue;
		} else {
			try {
				meta = parts[2].equals("*") ? OreDictionary.WILDCARD_VALUE : NumberFormat.getIntegerInstance().parse(parts[2]).intValue();
			} catch (ParseException e) {
				Log.warning("ItemStack string (" + stackString + ") has improperly formatted metadata. Suitable metadata are integer values or *.");
				return null;
			}
		}

		return new Stack(name, meta);
	}

	@Nullable
	public Item getItem() {
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
		if (item == null) {
			Log.warning("Failed to find item for (" + name + ") in the Forge registry.");
		}
		return item;
	}

	@Nullable
	public Block getBlock() {
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
		if (block == null) {
			Log.warning("Failed to find block for (" + name + ") in the Forge registry.");
		}
		return block;
	}

	public int getMeta() {
		return meta;
	}
}
