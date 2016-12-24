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

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.config.Constants;
import forestry.lepidopterology.genetics.Butterfly;
import forestry.lepidopterology.genetics.ButterflyDefinition;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CreativeTabForestry extends CreativeTabs {

	static {
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			Tabs.tabAgriculture = new CreativeTabForestry(1, "agriculture");
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			Tabs.tabApiculture = new CreativeTabForestry(2, "apiculture");
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.ARBORICULTURE)) {
			Tabs.tabArboriculture = new CreativeTabForestry(3, "arboriculture");
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			Tabs.tabLepidopterology = new CreativeTabForestry(4, "lepidopterology");
		}
	}

	public static final CreativeTabs tabForestry = new CreativeTabForestry(0, Constants.MOD_ID);

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
				iconItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Constants.MOD_ID, "ffarm"));
				break;
			case 2:
				return BeeManager.beeRoot.getMemberStack(new Bee(BeeDefinition.FOREST.getGenome()), EnumBeeType.DRONE);
			case 3:
				return TreeManager.treeRoot.getMemberStack(new Tree(TreeDefinition.Oak.getGenome()), EnumGermlingType.SAPLING);
			case 4:
				return ButterflyManager.butterflyRoot.getMemberStack(new Butterfly(ButterflyDefinition.Brimstone.getGenome()), EnumFlutterType.BUTTERFLY);
			default:
				iconItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Constants.MOD_ID, "fertilizerCompound"));
				break;
		}
		if (iconItem == null) {
			iconItem = PluginCore.items.wrench;
		}
		return new ItemStack(iconItem);
	}

	@Override
	public ItemStack getTabIconItem() {
		return getIconItemStack();
	}
}
