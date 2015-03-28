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

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.apiculture.FlowerManager;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.farming.logic.FarmableGenericCrop;

@Plugin(pluginID = "Witchery", name = "Witchery", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.witchery.description")
public class PluginWitchery extends ForestryPlugin {

	private static final String Witch = "witchery";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(Witch);
	}

	@Override
	public String getFailMessage() {
		return "Witchery not found";
	}

	@Override
	protected void registerRecipes() {

		ImmutableList<String> flowersAccept = ImmutableList.of(
				"bloodrose",
				"plantmine",
				"glintweed"
		);

		Map<String, Integer> cropSeed = new HashMap<String, Integer>();
		//"artichoke", 4 water plant
		cropSeed.put("belladonna", 4);
		cropSeed.put("mandrake", 4);
		cropSeed.put("mindrake", 4);
		cropSeed.put("snowbell", 4);
		cropSeed.put("wolfsbane", 7);
		cropSeed.put("wormwood", 4);

		ImmutableList<String> cropDirect = ImmutableList.of(
				"garlic" //meta 5
		);
		int seedamount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");

		Item saplingItem = GameRegistry.findItem(Witch, "witchsapling");
		ItemStack saplingStack = new ItemStack(saplingItem, 1, Defaults.WILDCARD);
		RecipeUtil.injectLeveledRecipe(saplingStack, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		String saplingName = GameData.getItemRegistry().getNameForObject(saplingItem);
		FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));

		for (String flowerAcceptName : flowersAccept) {
			Block flowerBlock = GameRegistry.findBlock(Witch, flowerAcceptName);
			if (flowerBlock != null) {
				FlowerManager.flowerRegistry.registerAcceptableFlower(flowerBlock, FlowerManager.FlowerTypeVanilla);
			}
		}
		for (String cropDirectName : cropDirect) {
			Block cropDirectBlock = GameRegistry.findBlock(Witch, cropDirectName + "plant");
			ItemStack cropDirectStack = GameRegistry.findItemStack(Witch, cropDirectName, 1);
			if (cropDirectStack != null && cropDirectBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(cropDirectStack, cropDirectBlock, 5));
			}
		}
		for (Map.Entry<String, Integer> cropSeedName : cropSeed.entrySet()) {
			Block cropSeedBlock = GameRegistry.findBlock(Witch, cropSeedName.getKey());
			ItemStack cropSeedStack = GameRegistry.findItemStack(Witch, "seeds" + cropSeedName.getKey(), 1);
			if (cropSeedStack != null && cropSeedBlock != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{cropSeedStack}, Fluids.SEEDOIL.getFluid(seedamount));
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(cropSeedStack, cropSeedBlock, cropSeedName.getValue()));
			}

		}
	}
}
