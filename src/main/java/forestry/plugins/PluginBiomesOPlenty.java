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
package forestry.plugins;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.farming.Farmables;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.farming.logic.FarmableBasicFruit;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.farming.logic.FarmableGenericSapling;

@Plugin(pluginID = "BiomesOPlenty", name = "BiomesOPlenty", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.biomesoplenty.description")
public class PluginBiomesOPlenty extends ForestryPlugin {

	private static final String BoP = "BiomesOPlenty";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(BoP);
	}

	@Override
	public String getFailMessage() {
		return "BiomesOPlenty not found";
	}

	@Override
	public void doInit() {
		super.doInit();

		Block boPSaplings = GameRegistry.findBlock(BoP, "saplings");
		Block boPColorizedSaplings = GameRegistry.findBlock(BoP, "colorizedSaplings");
		Item boPTurnipSeeds = GameRegistry.findItem(BoP, "turnipSeeds");
		Block boPTurnip = GameRegistry.findBlock(BoP, "turnip");

		List<String> saplingItemKeys = new ArrayList<String>();

		if (boPSaplings != null) {
			saplingItemKeys.add("saplings");
		}
		if (boPColorizedSaplings != null) {
			saplingItemKeys.add("colorizedSaplings");
		}

		for (String key : saplingItemKeys) {
			Item saplingItem = GameRegistry.findItem(BoP, key);
			String saplingName = GameData.getItemRegistry().getNameForObject(saplingItem);
			FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
		}
		ItemStack BoPPersimmon = new ItemStack(GameRegistry.findItem(BoP, "food"), 1, 8);
		Farmables.farmables.get("farmArboreal").add(new FarmableGenericSapling(boPSaplings, 15, BoPPersimmon));

		Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(new ItemStack(boPTurnipSeeds, 1, 0), boPTurnip, 7));
		Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(boPTurnip, 7));
	}
}
