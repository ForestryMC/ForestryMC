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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameData;

import forestry.api.apiculture.BeeManager;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.storage.IBackpackDefinition;

public class BackpackDefinition implements IBackpackDefinition {

	private final String name;

	private final int primaryColor; // - c03384
	private final int secondaryColor;

	private final Set<String> validItemStacks = new HashSet<>();
	private final Set<Integer> validOreIds = new HashSet<>();
	private final Set<Class> validItemClasses = new HashSet<>();
	private final Set<Class> validBlockClasses = new HashSet<>();

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

		String itemStackString = GameData.getItemRegistry().getNameForObject(item);

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

	public Set<String> getValidItemStacks() {
		return validItemStacks;
	}

	public Set<Integer> getValidOreIds() {
		return validOreIds;
	}

	public Set<Class> getValidBlockClasses() {
		return validBlockClasses;
	}

	public Set<Class> getValidItemClasses() {
		return validItemClasses;
	}

	@Override
	public boolean isValidItem(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		}

		Item item = itemStack.getItem();
		if (item == null) {
			return false;
		}

		String itemStackStringWild = GameData.getItemRegistry().getNameForObject(item);
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

	public static class BackpackDefinitionApiarist extends BackpackDefinition {

		public BackpackDefinitionApiarist(int primaryColor) {
			super("apiarist", primaryColor);
		}

		@Override
		public boolean isValidItem(ItemStack itemStack) {
			return BeeManager.beeRoot.isMember(itemStack);
		}
	}

	public static class BackpackDefinitionLepidopterist extends BackpackDefinition {

		public BackpackDefinitionLepidopterist(int primaryColor) {
			super("lepidopterist", primaryColor);
		}

		@Override
		public boolean isValidItem(ItemStack itemStack) {
			return ButterflyManager.butterflyRoot.isMember(itemStack);
		}
	}
}
