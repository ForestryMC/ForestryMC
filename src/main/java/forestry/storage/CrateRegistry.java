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

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.api.storage.ICrateRegistry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.MigrationHelper;
import forestry.storage.items.ItemCrated;

public class CrateRegistry implements ICrateRegistry {

	private static void registerCrate(ItemStack stack, @Nullable String oreDictName) {
		if (stack.isEmpty()) {
			Log.error("Tried to make a crate without an item");
			return;
		}

		String crateName;
		if (oreDictName != null) {
			crateName = "crated." + oreDictName;
		} else {
			String stringForItemStack = ItemStackUtil.getStringForItemStack(stack);
			if (stringForItemStack == null) {
				Log.error("Could not get string name for itemStack {}", stack);
				return;
			}
			String itemName = stringForItemStack.replace(':', '.');
			crateName = "crated." + itemName;
		}

		ItemCrated crate = new ItemCrated(stack, oreDictName);
		crate.setTranslationKey(crateName);
		crate.setRegistryName(crateName);

		MigrationHelper.addItemName(crateName);

		ForgeRegistries.ITEMS.register(crate);
		Proxies.common.registerItem(crate);
		ModuleCrates.registerCrate(crate);
	}

	@Override
	public void registerCrate(String oreDictName) {
		if (ModuleCrates.cratesRejectedOreDict.contains(oreDictName)) {
			return;
		}
		if (OreDictionary.doesOreNameExist(oreDictName)) {
			for (ItemStack stack : OreDictionary.getOres(oreDictName)) {
				if (stack != null) {
					registerCrate(stack, oreDictName);
					break;
				}
			}
		}
	}

	@Override
	public void registerCrate(Block block) {
		registerCrate(new ItemStack(block), null);
	}

	@Override
	public void registerCrate(Item item) {
		registerCrate(new ItemStack(item), null);
	}

	@Override
	public void registerCrate(ItemStack stack) {
		Collection<ItemStack> testStacks = ModuleCrates.cratesRejectedItem.get(stack.getItem());
		for (ItemStack testStack : testStacks) {
			if (ItemStackUtil.areItemStacksEqualIgnoreCount(stack, testStack)) {
				return;
			}
		}
		registerCrate(stack, null);
	}

	@Override
	public void blacklistCrate(ItemStack stack) {
		ModuleCrates.cratesRejectedItem.put(stack.getItem(), stack);
	}
}
