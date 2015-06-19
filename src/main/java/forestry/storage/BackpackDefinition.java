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
package forestry.storage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.storage.IBackpackDefinition;

public class BackpackDefinition implements IBackpackDefinition {

	private final String name;

	private final int primaryColor; // - c03384
	private final int secondaryColor;

	private final List<String> validItemStacks = new ArrayList<String>();
	private final List<Integer> validOreIds = new ArrayList<Integer>();
	private final List<Class> validItemClasses = new ArrayList<Class>();
	private final List<Class> validBlockClasses = new ArrayList<Class>();

	public BackpackDefinition(String name, int primaryColor) {
		this(name, primaryColor, 0xffffff);
	}

	public BackpackDefinition(String name, int primaryColor, int secondaryColor) {
		this.name = name;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
	}

	@Override
	public String getKey() {
		return name;
	}

	@Override
	public String getName(ItemStack backpack) {
		Item item = backpack.getItem();
		String display = ("" + StatCollector.translateToLocal(item.getUnlocalizedNameInefficiently(backpack) + ".name")).trim();

		if (backpack.stackTagCompound != null && backpack.stackTagCompound.hasKey("display", 10)) {
			NBTTagCompound nbt = backpack.stackTagCompound.getCompoundTag("display");

			if (nbt.hasKey("Name", 8)) {
				display = nbt.getString("Name");
			}
		}

		return display;
	}

	@Override
	public int getPrimaryColour() {
		return primaryColor;
	}

	@Override
	public int getSecondaryColour() {
		return secondaryColor;
	}

	@Override
	public void addValidItem(ItemStack validItem) {
		if (validItem == null) {
			return;
		}

		Item item = validItem.getItem();
		if (item == null) {
			return;
		}

		String itemStackString = item.delegate.name();

		int meta = validItem.getItemDamage();
		if (meta != OreDictionary.WILDCARD_VALUE) {
			itemStackString = itemStackString + ':' + meta;
		}

		this.validItemStacks.add(itemStackString);
	}

	public void clearAllValid() {
		validItemStacks.clear();
		validOreIds.clear();
	}

	@Override
	public void addValidItems(List<ItemStack> validItems) {
		for (ItemStack validItem : validItems) {
			addValidItem(validItem);
		}
	}

	public void addValidOreDictName(String oreDictName) {
//		if (OreDictionary.doesOreNameExist(oreDictName)) { //TODO: add this back when using a forge version that supports it
			int oreId = OreDictionary.getOreID(oreDictName);
			validOreIds.add(oreId);
//		}
	}

	public void addValidOreDictNames(List<String> oreDictNames) {
		for (String oreDictName : oreDictNames) {
			addValidOreDictName(oreDictName);
		}
	}

	public void addValidItemClass(Class itemClass) {
		if (itemClass != null) {
			validItemClasses.add(itemClass);
		}
	}

	public void addValidItemClasses(List<Class> itemClasses) {
		for (Class itemClass : itemClasses) {
			addValidItemClass(itemClass);
		}
	}

	public void addValidBlockClass(Class blockClass) {
		if (blockClass != null) {
			validBlockClasses.add(blockClass);
		}
	}

	public void addValidBlockClasses(List<Class> blockClasses) {
		for (Class blockClass : blockClasses) {
			addValidBlockClass(blockClass);
		}
	}

	public List<String> getValidItemStacks() {
		return validItemStacks;
	}

	public List<Integer> getValidOreIds() {
		return validOreIds;
	}

	public List<Class> getValidBlockClasses() {
		return validBlockClasses;
	}

	public List<Class> getValidItemClasses() {
		return validItemClasses;
	}

	// isValidItem can get called multiple times per tick if the player's inventory is full
	// and they are standing on multiple items.
	// It is a slow call, so we need a cache to make it fast
	private final Map<ItemStack, Boolean> isValidItemCache = new ValidItemCache();

	@Override
	public boolean isValidItem(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		}

		Boolean isValid = isValidItemCache.get(itemStack);
		if (isValid != null) {
			return isValid;
		}

		isValid = isValidItemUncached(itemStack);
		isValidItemCache.put(itemStack, isValid);
		return isValid;
	}

	private boolean isValidItemUncached(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item == null) {
			return false;
		}

		String itemStackStringWild = item.delegate.name();
		if (validItemStacks.contains(itemStackStringWild)) {
			return true;
		}

		int meta = itemStack.getItemDamage();
		if (meta != OreDictionary.WILDCARD_VALUE) {
			String itemStackString = itemStackStringWild + ':' + meta;
			if (validItemStacks.contains(itemStackString)) {
				return true;
			}
		}

		int[] oreIds = OreDictionary.getOreIDs(itemStack);
		for (int oreId : oreIds) {
			if (validOreIds.contains(oreId)) {
				validItemStacks.add(itemStackStringWild);
				return true;
			}
		}

		for (Class itemClass : validItemClasses) {
			if (itemClass.isInstance(item)) {
				validItemStacks.add(itemStackStringWild);
				return true;
			}
		}

		Block block = Block.getBlockFromItem(item);
		if (block != null) {
			for (Class blockClass : validBlockClasses) {
				if (blockClass.isInstance(block)) {
					validItemStacks.add(itemStackStringWild);
					return true;
				}
			}
		}

		return false;
	}

	private static class ValidItemCache extends LinkedHashMap<ItemStack, Boolean> {
		@Override
		protected boolean removeEldestEntry(Map.Entry<ItemStack, Boolean> eldest) {
			return size() > 64;
		}
	}
}
