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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

@ForestryPlugin(pluginID = ForestryPluginUids.NATURA, name = "Natura", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.natura.description")
public class PluginNatura extends BlankForestryPlugin {

	private static final String NATURA = "Natura";

	@Nullable
	private static Block logNatura;
	@Nullable
	private static Block logWillow;
	@Nullable
	private static Block leavesNatura;
	@Nullable
	private static Block saplingNatura;
	@Nullable
	private static Block saplingNaturaRare;

	private static ItemStack berryBlight = ItemStack.EMPTY;
	private static ItemStack berryDusk = ItemStack.EMPTY;
	private static ItemStack berrySky = ItemStack.EMPTY;
	private static ItemStack berrySting = ItemStack.EMPTY;
	private static ItemStack berryRasp = ItemStack.EMPTY;
	private static ItemStack berryBlue = ItemStack.EMPTY;
	private static ItemStack berryBlack = ItemStack.EMPTY;
	private static ItemStack berryMalo = ItemStack.EMPTY;
	private static ItemStack itemBarley = ItemStack.EMPTY;

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(NATURA);
	}

	@Override
	public String getFailMessage() {
		return "Natura not found";
	}

	@Override
	public void preInit() {
		super.preInit();

		logNatura = getBlock("tree");
		logWillow = getBlock("willow");
		leavesNatura = getBlock("floraleaves");
		saplingNatura = getBlock("florasapling");
		saplingNaturaRare = getBlock("Rare Sapling");

		ArrayList<String> saplingItemKeys = new ArrayList<>();

		if (saplingNatura != null) {
			saplingItemKeys.add("florasapling");
		}
		if (saplingNaturaRare != null) {
			saplingItemKeys.add("Rare Sapling");
		}

		for (String key : saplingItemKeys) {
			ItemStack saplingWild = getItemStack(key, OreDictionary.WILDCARD_VALUE);
			if (!saplingWild.isEmpty()) {
				RecipeUtil.addFermenterRecipes(saplingWild, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);

				String saplingName = ItemStackUtil.getItemNameFromRegistryAsString(saplingWild.getItem());
				FMLInterModComms.sendMessage(Constants.MOD_ID, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
			}
		}

		berryBlight = getItemStack("berry.nether", 0);
		berryDusk = getItemStack("berry.nether", 1);
		berrySky = getItemStack("berry.nether", 2);
		berrySting = getItemStack("berry.nether", 3);
		berryRasp = getItemStack("berry", 0);
		berryBlue = getItemStack("berry", 1);
		berryBlack = getItemStack("berry", 2);
		berryMalo = getItemStack("berry", 3);
		itemBarley = getItemStack("barleyFood", 0);
	}

	@Override
	public void registerCrates() {
		super.registerCrates();

		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		if (!berryBlight.isEmpty()) {
			crateRegistry.registerCrate(berryBlight);
		}
		if (!berryDusk.isEmpty()) {
			crateRegistry.registerCrate(berryDusk);
		}
		if (!berrySky.isEmpty()) {
			crateRegistry.registerCrate(berrySky);
		}
		if (!berrySting.isEmpty()) {
			crateRegistry.registerCrate(berrySting);
		}
		if (!berryRasp.isEmpty()) {
			crateRegistry.registerCrate(berryRasp);
		}
		if (!berryBlue.isEmpty()) {
			crateRegistry.registerCrate(berryBlue);
		}
		if (!berryBlack.isEmpty()) {
			crateRegistry.registerCrate(berryBlack);
		}
		if (!berryMalo.isEmpty()) {
			crateRegistry.registerCrate(berryMalo);
		}
		if (!itemBarley.isEmpty()) {
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
		ItemStack potashApple = getItemStack("Natura.netherfood", 0);
		if (!potashApple.isEmpty()) {
			crateRegistry.registerCrate(potashApple);
		}
		Item glowShroom = getItem("Glowshroom");
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
		}
		if (logWillow != null) {
			crateRegistry.registerCrate(new ItemStack(logWillow, 1, 0));
		}

		Item bloodWood = getItem("bloodwood");
		if (bloodWood != null) {
			crateRegistry.registerCrate(new ItemStack(bloodWood, 1, 0));
		}
		Item darkTree = getItem("Dark Tree");
		if (darkTree != null) {
			crateRegistry.registerCrate(new ItemStack(darkTree, 1, 0));
			crateRegistry.registerCrate(new ItemStack(darkTree, 1, 1));
		}
		Item heatSand = getItem("heatsand");
		if (heatSand != null) {
			crateRegistry.registerCrate(new ItemStack(heatSand, 1, 0));
		}
		Item taintedSoil = getItem("soil.tainted");
		if (taintedSoil != null) {
			crateRegistry.registerCrate(new ItemStack(taintedSoil, 1, 0));
		}
	}

	@Override
	public void registerRecipes() {
		int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");

		Item seed = getItem("barley.seed");
		if (seed != null) {
			ItemStack seedBarley = new ItemStack(seed, 1, 0);
			ItemStack seedCotton = new ItemStack(seed, 1, 1);

			ArrayList<ItemStack> seedList = new ArrayList<>();
			if (!seedBarley.isEmpty()) {
				seedList.add(seedBarley);
				RecipeManagers.moistenerManager.addRecipe(seedBarley, new ItemStack(Blocks.MYCELIUM), 5000);
			}
			if (!seedCotton.isEmpty()) {
				seedList.add(seedCotton);
			}

			for (ItemStack aSeedList : seedList) {
				RecipeManagers.squeezerManager.addRecipe(10, aSeedList, Fluids.SEED_OIL.getFluid(amount));
			}
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			Block cropBlock = getBlock("N Crops");
			//TODO: Natura for 1.9
			//			Farmables.farmables.get("farmWheat").add(new FarmableHandPlanted(seedBarley, cropBlock, 3));
			//			Farmables.farmables.get("farmWheat").add(new FarmableHandPlanted(seedCotton, cropBlock, 8));
		}

		List<ItemStack> berries = new ArrayList<>();
		if (!berryBlight.isEmpty()) {
			berries.add(berryBlight);
		}
		if (!berryDusk.isEmpty()) {
			berries.add(berryDusk);
		}
		if (!berrySky.isEmpty()) {
			berries.add(berrySky);
		}
		if (!berrySting.isEmpty()) {
			berries.add(berrySting);
		}
		if (!berryRasp.isEmpty()) {
			berries.add(berryRasp);
		}
		if (!berryBlue.isEmpty()) {
			berries.add(berryBlue);
		}
		if (!berryBlack.isEmpty()) {
			berries.add(berryBlack);
		}
		if (!berryMalo.isEmpty()) {
			berries.add(berryMalo);
		}

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 2;
		amount = Math.max(amount, 1); // Produce at least 1 mb of juice.
		ItemStack netherFood = getItemStack("Natura.netherfood", 0);
		ItemStack mulch = PluginCore.getItems().mulch.getItemStack();
		RecipeManagers.squeezerManager.addRecipe(10, netherFood, Fluids.JUICE.getFluid(amount), mulch, ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple"));

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 25;
		amount = Math.max(amount, 1); // Produce at least 1 mb of juice.

		for (ItemStack berry : berries) {
			RecipeManagers.squeezerManager.addRecipe(3, berry, Fluids.JUICE.getFluid(amount));
		}

		if (!itemBarley.isEmpty()) {
			RecipeUtil.addFermenterRecipes(itemBarley, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			int compostWheatAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
			if (compostWheatAmount > 0) {
				ItemStack compostWheat = PluginCore.getItems().compost.getItemStack(compostWheatAmount);
				RecipeUtil.addRecipe("natura_compost_wheat", compostWheat, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', itemBarley);
			}
			FuelManager.moistenerResource.put(itemBarley, new MoistenerFuel(itemBarley, PluginCore.getItems().mouldyWheat.getItemStack(), 0, 300));
		}
	}

	@Nullable
	private static Block getBlock(String blockName) {
		ResourceLocation key = new ResourceLocation(NATURA, blockName);
		if (ForgeRegistries.BLOCKS.containsKey(key)) {
			return ForgeRegistries.BLOCKS.getValue(key);
		} else {
			return null;
		}
	}

	@Nullable
	private static Item getItem(String itemName) {
		ResourceLocation key = new ResourceLocation(NATURA, itemName);
		if (ForgeRegistries.ITEMS.containsKey(key)) {
			return ForgeRegistries.ITEMS.getValue(key);
		} else {
			return null;
		}
	}

	private static ItemStack getItemStack(String itemName, int meta) {
		ResourceLocation key = new ResourceLocation(NATURA, itemName);
		if (ForgeRegistries.ITEMS.containsKey(key)) {
			return new ItemStack(ForgeRegistries.ITEMS.getValue(key), 1, meta);
		} else {
			return ItemStack.EMPTY;
		}
	}
}
