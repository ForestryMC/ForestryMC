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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.storage.ICrateRegistry;
import forestry.core.items.ItemCrated;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;

public class CrateRegistry implements ICrateRegistry {

	private static void registerCrate(ItemStack stack, boolean useOreDict) {
		if (stack == null || stack.getItem() == null) {
			Log.warning("Tried to make a crate without an item");
			return;
		}

		String itemName = ItemStackUtil.getStringForItemStack(stack).replace(':', '.');
		String crateName = "crated." + itemName;
		ItemCrated crate = new ItemCrated(stack, useOreDict);
		crate.setUnlocalizedName(crateName);
		GameRegistry.registerItem(crate, crateName);
		Proxies.common.registerItem(crate);
		PluginStorage.registerCrate(crate);
	}

	@Override
	public void registerCrate(Item item) {
		registerCrate(new ItemStack(item), false);
	}

	@Override
	public void registerCrateUsingOreDict(Item item) {
		registerCrate(new ItemStack(item), true);
	}

	@Override
	public void registerCrate(Block block) {
		registerCrate(new ItemStack(block), false);
	}

	@Override
	public void registerCrateUsingOreDict(Block block) {
		registerCrate(new ItemStack(block), true);
	}

	@Override
	public void registerCrate(ItemStack stack) {
		registerCrate(stack, false);
	}

	@Override
	public void registerCrateUsingOreDict(ItemStack stack) {
		registerCrate(stack, true);
	}
}
