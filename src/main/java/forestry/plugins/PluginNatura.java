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

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;

@Plugin(pluginID = "Natura",
		name = "Natura",
		author = "SirSengir",
		url = Defaults.URL,
		description = "Adds support for Natura seeds, saplings, and berries.")
public class PluginNatura extends ForestryPlugin {

	private static final String NATURA = "Natura";

	public static Block logNatura;
	public static Block logWillow;

	public static Block leavesNatura;
	public static Block saplingNatura;
	public static Block saplingNaturaRare;

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(NATURA);
	}

	@Override
	public String getFailMessage() {
		return "Natura not found";
	}

	@Override
	public void doInit() {
		super.doInit();

		logNatura = GameRegistry.findBlock(NATURA, "tree");
		logWillow = GameRegistry.findBlock(NATURA, "willow");
		leavesNatura = GameRegistry.findBlock(NATURA, "floraleaves");
		saplingNatura = GameRegistry.findBlock(NATURA, "florasapling");
		saplingNaturaRare = GameRegistry.findBlock(NATURA, "Rare Sapling");

		ArrayList<String> saplingItemKeys = new ArrayList<String>();

		if (saplingNatura != null)
			saplingItemKeys.add("florasapling");
		if (saplingNaturaRare != null)
			saplingItemKeys.add("Rare Sapling");

		for (String key : saplingItemKeys) {
			Item saplingItem = GameRegistry.findItem(NATURA, key);
			String saplingName = GameData.getItemRegistry().getNameForObject(saplingItem);
			FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
		}
	}

	@Override
	protected void registerRecipes() {
		ItemStack seedBarley = GameRegistry.findItemStack(NATURA, "seedBarley", 1);
		ItemStack seedCotton = GameRegistry.findItemStack(NATURA, "seedCotton", 1);

		ArrayList<ItemStack> seedList = new ArrayList<ItemStack>();
		if (seedBarley != null)
			seedList.add(seedBarley);
		if (seedCotton != null)
			seedList.add(seedCotton);

		int amount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
		for (int i = 0; i < seedList.size(); ++i)
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{seedList.get(i)},
					LiquidHelper.getLiquid(Defaults.LIQUID_SEEDOIL, amount));

		ItemStack berryBlight = GameRegistry.findItemStack(NATURA, "berryBlight", 1);
		ItemStack berryDusk = GameRegistry.findItemStack(NATURA, "berryDusk", 1);
		ItemStack berrySky = GameRegistry.findItemStack(NATURA, "berrySting", 1);
		ItemStack berrySting = GameRegistry.findItemStack(NATURA, "berrySting", 1);
		ItemStack berryRasp = GameRegistry.findItemStack(NATURA, "berryRasp", 1);
		ItemStack berryBlue = GameRegistry.findItemStack(NATURA, "berryBlue", 1);
		ItemStack berryBlack = GameRegistry.findItemStack(NATURA, "berryBlack", 1);
		ItemStack berryMalo = GameRegistry.findItemStack(NATURA, "berryMalo", 1);

		ArrayList<ItemStack> berries = new ArrayList<ItemStack>();
		if (berryBlight != null)
			berries.add(berryBlight);
		if (berryDusk != null)
			berries.add(berryDusk);
		if (berrySky != null)
			berries.add(berrySky);
		if (berrySting != null)
			berries.add(berrySting);
		if (berryRasp != null)
			berries.add(berryRasp);
		if (berryBlue != null)
			berries.add(berryBlue);
		if (berryBlack != null)
			berries.add(berryBlack);
		if (berryMalo != null)
			berries.add(berryMalo);

		amount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") / 25;
		amount = (amount > 1) ? amount : 1; // Produce at least 1 mb of juice.
		for (int i = 0; i < berries.size(); ++i)
			RecipeManagers.squeezerManager.addRecipe(3, new ItemStack[]{berries.get(i)},
					LiquidHelper.getLiquid(Defaults.LIQUID_JUICE, amount));
	}

}
