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

import javax.annotation.Nullable;

import forestry.api.apiculture.BeeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackInterface;
import forestry.core.items.ItemCrated;
import forestry.core.items.ItemRegistry;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemRegistryStorage extends ItemRegistry {
	public final ItemCrated crate;
	@Nullable
	public final Item apiaristBackpack;
	@Nullable
	public final Item lepidopteristBackpack;

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
		crate = registerItem(new ItemCrated(ItemStack.EMPTY, null), "crate");

		// BACKPACKS
		IBackpackInterface backpackInterface = BackpackManager.backpackInterface;

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			apiaristBackpack = registerItem(backpackInterface.createNaturalistBackpack("apiarist", BeeManager.beeRoot), "apiarist_bag");
			apiaristBackpack.setCreativeTab(Tabs.tabApiculture);
		} else {
			apiaristBackpack = null;
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			lepidopteristBackpack = registerItem(backpackInterface.createNaturalistBackpack("lepidopterist", ButterflyManager.butterflyRoot), "lepidopterist_bag");
			lepidopteristBackpack.setCreativeTab(Tabs.tabLepidopterology);
		} else {
			lepidopteristBackpack = null;
		}

		minerBackpack = registerItem(backpackInterface.createBackpack(BackpackManager.MINER_UID, EnumBackpackType.NORMAL), "miner_bag");
		minerBackpackT2 = registerItem(backpackInterface.createBackpack(BackpackManager.MINER_UID, EnumBackpackType.WOVEN), "miner_bag_t2");

		diggerBackpack = registerItem(backpackInterface.createBackpack(BackpackManager.DIGGER_UID, EnumBackpackType.NORMAL), "digger_bag");
		diggerBackpackT2 = registerItem(backpackInterface.createBackpack(BackpackManager.DIGGER_UID, EnumBackpackType.WOVEN), "digger_bag_t2");

		foresterBackpack = registerItem(backpackInterface.createBackpack(BackpackManager.FORESTER_UID, EnumBackpackType.NORMAL), "forester_bag");
		foresterBackpackT2 = registerItem(backpackInterface.createBackpack(BackpackManager.FORESTER_UID, EnumBackpackType.WOVEN), "forester_bag_t2");

		hunterBackpack = registerItem(backpackInterface.createBackpack(BackpackManager.HUNTER_UID, EnumBackpackType.NORMAL), "hunter_bag");
		hunterBackpackT2 = registerItem(backpackInterface.createBackpack(BackpackManager.HUNTER_UID, EnumBackpackType.WOVEN), "hunter_bag_t2");

		adventurerBackpack = registerItem(backpackInterface.createBackpack(BackpackManager.ADVENTURER_UID, EnumBackpackType.NORMAL), "adventurer_bag");
		adventurerBackpackT2 = registerItem(backpackInterface.createBackpack(BackpackManager.ADVENTURER_UID, EnumBackpackType.WOVEN), "adventurer_bag_t2");

		builderBackpack = registerItem(backpackInterface.createBackpack(BackpackManager.BUILDER_UID, EnumBackpackType.NORMAL), "builder_bag");
		builderBackpackT2 = registerItem(backpackInterface.createBackpack(BackpackManager.BUILDER_UID, EnumBackpackType.WOVEN), "builder_bag_t2");
	}
}
