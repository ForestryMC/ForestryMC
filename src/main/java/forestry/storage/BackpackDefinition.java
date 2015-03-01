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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import forestry.api.storage.IBackpackDefinition;
import forestry.core.utils.StackUtils;

public class BackpackDefinition implements IBackpackDefinition {

	private final String name;

	private final int primaryColor; // - c03384
	private final int secondaryColor;

	private final ArrayList<ItemStack> validItems = new ArrayList<ItemStack>();

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
	public String getName() {
		return "Update Forestry!";
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
		if (validItem.getItem() != null && !isValidItem(validItem)) {
			this.validItems.add(validItem);
		}
	}

	public void addValidItems(List<ItemStack> validItems) {
		for (ItemStack validItem : validItems) {
			addValidItem(validItem);
		}
	}

	public ArrayList<ItemStack> getValidItems() {
		return validItems;
	}

	// isValidItem can get called multiple times per tick if the player's inventory is full
	// and they are standing on multiple items.
	// It is a slow call, so we need a cache to make it fast
	private Map<ItemStack, Boolean> isValidItemCache = new LinkedHashMap<ItemStack, Boolean>() {
		@Override
		protected boolean removeEldestEntry(Map.Entry<ItemStack, Boolean> eldest) {
			return size() > 64;
		}
	};

	@Override
	public boolean isValidItem(EntityPlayer player, ItemStack itemstack) {
		return isValidItem(itemstack);
	}

	@Override
	public boolean isValidItem(ItemStack itemstack) {
		Boolean cached = isValidItemCache.get(itemstack);
		if (cached != null) {
			return cached;
		}

		for (ItemStack stack : getValidItems()) {
			if (StackUtils.isCraftingEquivalent(stack, itemstack, true, false)) {
				isValidItemCache.put(itemstack, true);
				return true;
			}
		}

		isValidItemCache.put(itemstack, false);
		return false;
	}
}
