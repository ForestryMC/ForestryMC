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
package forestry.storage.items;

import net.minecraft.item.Item;

import forestry.api.apiculture.BeeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.items.ItemCrated;
import forestry.core.items.ItemRegistry;
import forestry.plugins.ForestryPluginUids;

public class ItemRegistryStorage extends ItemRegistry {
	public final ItemCrated crate;
	public final ItemBackpackNaturalist apiaristBackpack;
	public final ItemBackpackNaturalist lepidopteristBackpack;
	public final Item minerBackpack;
	public final Item minerBackpackT2;
	public final Item diggerBackpack;
	public final Item diggerBackpackT2;
	public final Item foresterBackpack;
	public final Item foresterBackpackT2;
	public final Item hunterBackpack;
	public final Item hunterBackpackT2;
	public final Item adventurerBackpack;
	public final Item adventurerBackpackT2;
	public final Item builderBackpack;
	public final Item builderBackpackT2;

	public ItemRegistryStorage() {
		// CRATE
		crate = registerItem(new ItemCrated(null, null), "crate");
		
		// BACKPACKS
		IBackpackDefinition definition;
		
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			definition = BackpackManager.backpackInterface.getBackpack("apiarist");
			apiaristBackpack = new ItemBackpackNaturalist(BeeManager.beeRoot, definition, EnumBackpackType.NATURALIST);
			apiaristBackpack.setCreativeTab(Tabs.tabApiculture);
			registerItem(apiaristBackpack, "apiaristBag");
		} else {
			apiaristBackpack = null;
		}
		
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			definition = BackpackManager.backpackInterface.getBackpack("lepidopterist");
			lepidopteristBackpack = new ItemBackpackNaturalist(ButterflyManager.butterflyRoot, definition, EnumBackpackType.NATURALIST);
			lepidopteristBackpack.setCreativeTab(Tabs.tabLepidopterology);
			registerItem(lepidopteristBackpack, "lepidopteristBag");
		} else {
			lepidopteristBackpack = null;
		}
		
		definition = BackpackManager.backpackInterface.getBackpack(BackpackManager.MINER_UID);
		minerBackpack = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.NORMAL), "minerBag");
		minerBackpackT2 = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.WOVEN), "minerBagT2");
		
		definition = BackpackManager.backpackInterface.getBackpack(BackpackManager.DIGGER_UID);
		diggerBackpack = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.NORMAL), "diggerBag");
		diggerBackpackT2 = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.WOVEN), "diggerBagT2");
		
		definition = BackpackManager.backpackInterface.getBackpack(BackpackManager.FORESTER_UID);
		foresterBackpack = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.NORMAL), "foresterBag");
		foresterBackpackT2 = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.WOVEN), "foresterBagT2");
		
		definition = BackpackManager.backpackInterface.getBackpack(BackpackManager.HUNTER_UID);
		hunterBackpack = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.NORMAL), "hunterBag");
		hunterBackpackT2 = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.WOVEN), "hunterBagT2");
		
		definition = BackpackManager.backpackInterface.getBackpack(BackpackManager.ADVENTURER_UID);
		adventurerBackpack = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.NORMAL), "adventurerBag");
		adventurerBackpackT2 = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.WOVEN), "adventurerBagT2");
		
		definition = BackpackManager.backpackInterface.getBackpack(BackpackManager.BUILDER_UID);
		builderBackpack = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.NORMAL), "builderBag");
		builderBackpackT2 = registerItem(BackpackManager.backpackInterface.createBackpack(definition, EnumBackpackType.WOVEN), "builderBagT2");
	}
}
