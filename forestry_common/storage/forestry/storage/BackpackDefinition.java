/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.storage;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.storage.IBackpackDefinition;
import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;

public class BackpackDefinition implements IBackpackDefinition {
	final String name;

	final int primaryColor; // - c03384
	final int secondaryColor;

	ArrayList<ItemStack> validItems = new ArrayList<ItemStack>();

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
		return StringUtil.localize("storage.backpack." + name);
	}

	@Override
	public int getPrimaryColour() {
		return primaryColor;
	}

	@Override
	public int getSecondaryColour() {
		return secondaryColor;
	}

	public BackpackDefinition setValidItems(ArrayList<ItemStack> validItems) {
		this.validItems = validItems;
		return this;
	}

	@Override
	public void addValidItem(ItemStack validItem) {
		this.validItems.add(validItem);
	}

	@Override
	public ArrayList<ItemStack> getValidItems(EntityPlayer player) {
		return validItems;
	}

	@Override
	public boolean isValidItem(EntityPlayer player, ItemStack itemstack) {
		for (ItemStack stack : getValidItems(player))
			if (stack != null) {
				if (stack.getItemDamage() == Defaults.WILDCARD) {
					if (stack.getItem() == itemstack.getItem())
						return true;
				} else if (stack.isItemEqual(itemstack))
					return true;
			}

		return false;
	}

}
