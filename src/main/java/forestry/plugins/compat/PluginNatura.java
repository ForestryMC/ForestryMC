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
package forestry.plugins.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.ModUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.NATURA, name = "Natura", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.natura.description")
public class PluginNatura extends BlankForestryPlugin {

	public static final String modId = "Natura";

	private static Block logNatura;
	private static Block logWillow;

	private static Block leavesNatura;
	private static Block saplingNatura;
	private static Block saplingNaturaRare;
	private static ItemStack berryBlight;
	private static ItemStack berryDusk;
	private static ItemStack berrySky;
	private static ItemStack berrySting;
	private static ItemStack berryRasp;
	private static ItemStack berryBlue;
	private static ItemStack berryBlack;
	private static ItemStack berryMalo;
	private static ItemStack itemBarley;

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(modId);
	}

	@Override
	public String getFailMessage() {
		return "Natura not found";
	}

	@Override
	public void preInit() {
		super.preInit();

		logNatura = GameRegistry.findBlock(modId, "tree");
		logWillow = GameRegistry.findBlock(modId, "willow");
		leavesNatura = GameRegistry.findBlock(modId, "floraleaves");
		saplingNatura = GameRegistry.findBlock(modId, "florasapling");
		saplingNaturaRare = GameRegistry.findBlock(modId, "Rare Sapling");

		ArrayList<String> saplingItemKeys = new ArrayList<>();

		if (saplingNatura != null) {
			saplingItemKeys.add("florasapling");
		}
		if (saplingNaturaRare != null) {
			saplingItemKeys.add("Rare Sapling");
		}

		for (String key : saplingItemKeys) {
			Item saplingItem = GameRegistry.findItem(modId, key);

			ItemStack saplingWild = new ItemStack(saplingItem, 1, OreDictionary.WILDCARD_VALUE);
			RecipeUtil.addFermenterRecipes(saplingWild, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);

			String saplingName = ItemStackUtil.getItemNameFromRegistryAsString(saplingItem);
			FMLInterModComms.sendMessage(Constants.MOD_ID, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
		}

		Item netherBerry = GameRegistry.findItem(modId, "berry.nether");
		Item berry = GameRegistry.findItem(modId, "berry");
		Item barley = GameRegistry.findItem(modId, "barleyFood");
		
		berryBlight = new ItemStack(netherBerry, 1, 0);
		berryDusk = new ItemStack(netherBerry, 1, 1);
		berrySky = new ItemStack(netherBerry, 1, 2);
		berrySting = new ItemStack(netherBerry, 1, 3);
		berryRasp = new ItemStack(berry, 1, 0);
		berryBlue = new ItemStack(berry, 1, 1);
		berryBlack = new ItemStack(berry, 1, 2);
		berryMalo = new ItemStack(berry, 1, 3);
		itemBarley = new ItemStack(barley);
	}

	@Override
	public void registerCrates() {
		super.registerCrates();

		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		if (berryBlight != null) {
			crateRegistry.registerCrate(berryBlight);
		}
		if (berryDusk != null) {
			crateRegistry.registerCrate(berryDusk);
		}
		if (berrySky != null) {
			crateRegistry.registerCrate(berrySky);
		}
		if (berrySting != null) {
			crateRegistry.registerCrate(berrySting);
		}
		if (berryRasp != null) {
			crateRegistry.registerCrate(berryRasp);
		}
		if (berryBlue != null) {
			crateRegistry.registerCrate(berryBlue);
		}
		if (berryBlack != null) {
			crateRegistry.registerCrate(berryBlack);
		}
		if (berryMalo != null) {
			crateRegistry.registerCrate(berryMalo);
		}
		if (itemBarley != null) {
			crateRegistry.registerCrate(itemBarley);
		}
		if (saplingNatura != null) {
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 0));
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 1));
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 2));
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 3));
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 4));
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 5));
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 6));
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 7));
		}

		if (saplingNaturaRare != null) {
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 0));
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 1));
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 2));
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 3));
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 4));
		}
		Item potashApple = GameRegistry.findItem(modId, "Natura.netherfood");
		if (potashApple != null) {
			crateRegistry.registerCrate(new ItemStack(potashApple, 1, 0));
		}
		Item glowShroom = GameRegistry.findItem(modId, "Glowshroom");
		if (glowShroom != null) {
			crateRegistry.registerCrate(new ItemStack(glowShroom, 1, 0));
			crateRegistry.registerCrate(new ItemStack(glowShroom, 1, 1));
			crateRegistry.registerCrate(new ItemStack(glowShroom, 1, 2));
		}
		if (logNatura != null) {
			crateRegistry.registerCrate(new ItemStack(logNatura, 1, 0));
			crateRegistry.registerCrate(new ItemStack(logNatura, 1, 1));
			crateRegistry.registerCrate(new ItemStack(logNatura, 1, 2));
			crateRegistry.registerCrate(new ItemStack(logNatura, 1, 3));
			crateRegistry.registerCrate(new ItemStack(logWillow, 1, 0));
		}
		Item bloodWood = GameRegistry.findItem(modId, "bloodwood");
		if (bloodWood != null) {
			crateRegistry.registerCrate(new ItemStack(bloodWood, 1, 0));
		}
		Item darkTree = GameRegistry.findItem(modId, "Dark Tree");
		if (darkTree != null) {
			crateRegistry.registerCrate(new ItemStack(darkTree, 1, 0));
			crateRegistry.registerCrate(new ItemStack(darkTree, 1, 1));
		}
		Item heatSand = GameRegistry.findItem(modId, "heatsand");
		if (heatSand != null) {
			crateRegistry.registerCrate(new ItemStack(heatSand, 1, 0));
		}
		Item taintedSoil = GameRegistry.findItem(modId, "soil.tainted");
		if (taintedSoil != null) {
			crateRegistry.registerCrate(new ItemStack(taintedSoil, 1, 0));
		}
	}

	@Override
	public void registerRecipes() {
		Item seed = GameRegistry.findItem(modId, "barley.seed");
		ItemStack seedBarley = new ItemStack(seed, 1, 0);
		ItemStack seedCotton = new ItemStack(seed, 1, 1);

		ArrayList<ItemStack> seedList = new ArrayList<>();
		if (seedBarley != null) {
			seedList.add(seedBarley);
			RecipeManagers.moistenerManager.addRecipe(seedBarley, new ItemStack(Blocks.MYCELIUM), 5000);
		}
		if (seedCotton != null) {
			seedList.add(seedCotton);
		}

		int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		for (ItemStack aSeedList : seedList) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{aSeedList}, Fluids.SEED_OIL.getFluid(amount));
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			Block cropBlock = GameRegistry.findBlock(modId, "N Crops");
			//TODO: Natura for 1.9
			//			Farmables.farmables.get("farmWheat").add(new FarmableHandPlanted(seedBarley, cropBlock, 3));
			//			Farmables.farmables.get("farmWheat").add(new FarmableHandPlanted(seedCotton, cropBlock, 8));
		}
		
		List<ItemStack> berries = new ArrayList<>();
		if (berryBlight != null) {
			berries.add(berryBlight);
		}
		if (berryDusk != null) {
			berries.add(berryDusk);
		}
		if (berrySky != null) {
			berries.add(berrySky);
		}
		if (berrySting != null) {
			berries.add(berrySting);
		}
		if (berryRasp != null) {
			berries.add(berryRasp);
		}
		if (berryBlue != null) {
			berries.add(berryBlue);
		}
		if (berryBlack != null) {
			berries.add(berryBlack);
		}
		if (berryMalo != null) {
			berries.add(berryMalo);
		}

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 2;
		amount = Math.max(amount, 1); // Produce at least 1 mb of juice.
		ItemStack netherFood = new ItemStack(GameRegistry.findItem(modId, "Natura.netherfood"), 1, 0);
		ItemStack mulch = PluginCore.items.mulch.getItemStack();
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{netherFood}, Fluids.JUICE.getFluid(amount), mulch, ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple"));

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 25;
		amount = Math.max(amount, 1); // Produce at least 1 mb of juice.

		for (ItemStack berry : berries) {
			RecipeManagers.squeezerManager.addRecipe(3, new ItemStack[]{berry}, Fluids.JUICE.getFluid(amount));
		}

		if (itemBarley != null) {
			RecipeUtil.addFermenterRecipes(itemBarley, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			int compostWheatAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
			if (compostWheatAmount > 0) {
				ItemStack compostWheat = PluginCore.items.fertilizerBio.getItemStack(compostWheatAmount);
				RecipeUtil.addRecipe(compostWheat, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', itemBarley);
			}
			FuelManager.moistenerResource.put(itemBarley, new MoistenerFuel(itemBarley, PluginCore.items.mouldyWheat.getItemStack(), 0, 300));
		}
	}

}
