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
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.core.items.ItemCrated;
import forestry.core.items.ItemRegistry;
import forestry.plugins.PluginManager;

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
		crate = registerItem(new ItemCrated(null, false), "crate");
		
		// BACKPACKS
		IBackpackDefinition definition;
		
		if (PluginManager.Module.APICULTURE.isEnabled()) {
			definition = BackpackManager.definitions.get("apiarist");
			apiaristBackpack = new ItemBackpackNaturalist(BeeManager.beeRoot, definition, EnumBackpackType.APIARIST);
			apiaristBackpack.setCreativeTab(Tabs.tabApiculture);
			registerItem(apiaristBackpack, "apiaristBag");
		} else {
			apiaristBackpack = null;
		}
		
		if (PluginManager.Module.LEPIDOPTEROLOGY.isEnabled()) {
			definition = BackpackManager.definitions.get("lepidopterist");
			lepidopteristBackpack = new ItemBackpackNaturalist(ButterflyManager.butterflyRoot, definition, EnumBackpackType.APIARIST);
			lepidopteristBackpack.setCreativeTab(Tabs.tabLepidopterology);
			registerItem(lepidopteristBackpack, "lepidopteristBag");
		} else {
			lepidopteristBackpack = null;
		}
		
		definition = BackpackManager.definitions.get("miner");
		minerBackpack = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "minerBag");
		minerBackpackT2 = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "minerBagT2");
		
		definition = BackpackManager.definitions.get("digger");
		diggerBackpack = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "diggerBag");
		diggerBackpackT2 = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "diggerBagT2");
		
		definition = BackpackManager.definitions.get("forester");
		foresterBackpack = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "foresterBag");
		foresterBackpackT2 = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "foresterBagT2");
		
		definition = BackpackManager.definitions.get("hunter");
		hunterBackpack = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "hunterBag");
		hunterBackpackT2 = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "hunterBagT2");
		
		definition = BackpackManager.definitions.get("adventurer");
		adventurerBackpack = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "adventurerBag");
		adventurerBackpackT2 = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "adventurerBagT2");
		
		definition = BackpackManager.definitions.get("builder");
		builderBackpack = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "builderBag");
		builderBackpackT2 = registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "builderBagT2");
	}
}
