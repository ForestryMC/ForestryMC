/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.core.ItemInterface;
import forestry.api.core.Tabs;
import forestry.core.utils.StringUtil;

public class CreativeTabForestry extends CreativeTabs {

	static {
		Tabs.tabApiculture = new CreativeTabForestry(1, "apiculture");
		Tabs.tabArboriculture = new CreativeTabForestry(2, "arboriculture");
		Tabs.tabLepidopterology = new CreativeTabForestry(3, "lepidopterology");
	}
	public static final CreativeTabs tabForestry = new CreativeTabForestry(0, "forestry");

	private final int icon;

	public CreativeTabForestry(int icon, String label) {
		super(label);
		this.icon = icon;
	}

	@Override
	public ItemStack getIconItemStack() {
		switch (icon) {
		case 1:
			return ItemInterface.getItem("beeDroneGE");
		case 2:
			return ItemInterface.getItem("sapling");
		case 3:
			return ItemInterface.getItem("butterflyGE");
		case 0:
		default:
			return ItemInterface.getItem("fertilizerCompound");
		}
	}

	@Override
	public String getTranslatedTabLabel() {
		return StringUtil.localize("itemGroup." + this.getTabLabel());
	}

	@Override
	public Item getTabIconItem() {
		return null; // not used due to overridden getIconItemStack
	}
}
