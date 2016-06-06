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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.storage.ICrateRegistry;
import forestry.core.items.ItemCrated;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;

public class CrateRegistry implements ICrateRegistry {

	private static void registerCrate(@Nonnull ItemStack stack, @Nullable String oreDictName) {
		if (stack == null || stack.getItem() == null) {
			Log.warning("Tried to make a crate without an item");
			return;
		}

		String crateName;
		if (oreDictName != null) {
			crateName = "crated." + oreDictName;
		} else {
			String itemName = ItemStackUtil.getStringForItemStack(stack).replace(':', '.');
			crateName = "crated." + itemName;
		}

		ItemCrated crate = new ItemCrated(stack, oreDictName);
		crate.setUnlocalizedName(crateName);
		crate.setRegistryName(crateName);

		GameRegistry.register(crate);
		Proxies.common.registerItem(crate);
		PluginStorage.registerCrate(crate);
	}

	@Override
	public void registerCrate(@Nonnull String oreDictName) {
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
	public void registerCrate(@Nonnull Block block) {
		registerCrate(new ItemStack(block), null);
	}

	@Override
	public void registerCrate(@Nonnull Item item) {
		registerCrate(new ItemStack(item), null);
	}

	@Override
	public void registerCrate(@Nonnull ItemStack stack) {
		registerCrate(stack, null);
	}
}
