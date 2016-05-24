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
package forestry.core.items;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import forestry.core.proxy.Proxies;
import forestry.core.utils.OreDictUtil;
import forestry.plugins.PluginManager;

public abstract class ItemRegistry {
	protected static <T extends Item> T registerItem(T item, String name) {
		if (PluginManager.getStage() != PluginManager.Stage.REGISTER) {
			throw new RuntimeException("Tried to register Item outside of REGISTER");
		}
		item.setUnlocalizedName("for." + name);
		GameRegistry.registerItem(item, name);
		Proxies.common.registerItem(item);
		return item;
	}

	protected static void registerOreDict(String oreDictName, ItemStack itemStack) {
		OreDictionary.registerOre(oreDictName, itemStack);
	}

	@Nonnull
	public static ItemStack createItemForOreName(String oreName) {
		Item oreItem = registerItem(new ItemForestry(), oreName);
		OreDictionary.registerOre(oreName, oreItem);
		return OreDictUtil.getFirstSuitableOre(oreName);
	}

}
