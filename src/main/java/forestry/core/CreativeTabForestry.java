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
package forestry.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.core.Tabs;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginManager;

public class CreativeTabForestry extends CreativeTabs {

	static {
		if (PluginManager.Module.APICULTURE.isEnabled()) {
			Tabs.tabApiculture = new CreativeTabForestry(1, "apiculture");
		}

		if (PluginManager.Module.ARBORICULTURE.isEnabled()) {
			Tabs.tabArboriculture = new CreativeTabForestry(2, "arboriculture");
		}

		if (PluginManager.Module.LEPIDOPTEROLOGY.isEnabled()) {
			Tabs.tabLepidopterology = new CreativeTabForestry(3, "lepidopterology");
		}
	}

	public static final CreativeTabs tabForestry = new CreativeTabForestry(0, "forestry");

	private final int icon;

	private CreativeTabForestry(int icon, String label) {
		super(label);
		this.icon = icon;
	}

	@Override
	public ItemStack getIconItemStack() {
		Item iconItem;
		switch (icon) {
			case 1:
				iconItem = GameRegistry.findItem("Forestry", "beeDroneGE");
				break;
			case 2:
				iconItem = GameRegistry.findItem("Forestry", "sapling");
				break;
			case 3:
				iconItem = GameRegistry.findItem("Forestry", "butterflyGE");
				break;
			default:
				iconItem = GameRegistry.findItem("Forestry", "fertilizerCompound");
				break;
		}
		if (iconItem == null) {
			iconItem = PluginCore.items.wrench;
		}
		return new ItemStack(iconItem);
	}

	@Override
	public Item getTabIconItem() {
		return null; // not used due to overridden getIconItemStack
	}
}
