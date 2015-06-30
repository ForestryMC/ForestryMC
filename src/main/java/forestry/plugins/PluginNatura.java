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
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.farming.Farmables;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.farming.logic.FarmableGenericCrop;

@Plugin(pluginID = "Natura", name = "Natura", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.natura.description")
public class PluginNatura extends ForestryPlugin {

	private static final String NATURA = "Natura";

	public static Block logNatura;
	public static Block logWillow;

	public static Block leavesNatura;
	public static Block saplingNatura;
	public static Block saplingNaturaRare;
	public static ItemStack berryBlight;
	public static ItemStack berryDusk;
	public static ItemStack berrySky;
	public static ItemStack berrySting;
	public static ItemStack berryRasp;
	public static ItemStack berryBlue;
	public static ItemStack berryBlack;
	public static ItemStack berryMalo;
	public static ItemStack itemBarley;

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(NATURA);
	}

	@Override
	public String getFailMessage() {
		return "Natura not found";
	}

	@Override
	public void preInit() {
		super.preInit();

		logNatura = GameRegistry.findBlock(NATURA, "tree");
		logWillow = GameRegistry.findBlock(NATURA, "willow");
		leavesNatura = GameRegistry.findBlock(NATURA, "floraleaves");
		saplingNatura = GameRegistry.findBlock(NATURA, "florasapling");
		saplingNaturaRare = GameRegistry.findBlock(NATURA, "Rare Sapling");

		ArrayList<String> saplingItemKeys = new ArrayList<String>();

		if (saplingNatura != null) {
			saplingItemKeys.add("florasapling");
		}
		if (saplingNaturaRare != null) {
			saplingItemKeys.add("Rare Sapling");
		}

		for (String key : saplingItemKeys) {
			Item saplingItem = GameRegistry.findItem(NATURA, key);

			ItemStack saplingWild = new ItemStack(saplingItem, 1, Defaults.WILDCARD);
			RecipeUtil.injectLeveledRecipe(saplingWild, GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);

			String saplingName = GameData.getItemRegistry().getNameForObject(saplingItem);
			FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
		}

		berryBlight = GameRegistry.findItemStack(NATURA, "berryBlight", 1);
		berryDusk = GameRegistry.findItemStack(NATURA, "berryDusk", 1);
		berrySky = GameRegistry.findItemStack(NATURA, "berrySky", 1);
		berrySting = GameRegistry.findItemStack(NATURA, "berrySting", 1);
		berryRasp = GameRegistry.findItemStack(NATURA, "berryRasp", 1);
		berryBlue = GameRegistry.findItemStack(NATURA, "berryBlue", 1);
		berryBlack = GameRegistry.findItemStack(NATURA, "berryBlack", 1);
		berryMalo = GameRegistry.findItemStack(NATURA, "berryMalo", 1);
		itemBarley = GameRegistry.findItemStack(NATURA, "barleyFood", 1);
	}

	@Override
	protected void registerCrates() {
		super.registerCrates();

		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		if (berryBlight != null) {
			crateRegistry.registerCrate(berryBlight, "cratedNaturaBerryBlight");
		}
		if (berryDusk != null) {
			crateRegistry.registerCrate(berryDusk, "cratedNaturaBerryDusk");
		}
		if (berrySky != null) {
			crateRegistry.registerCrate(berrySky, "cratedNaturaBerrySky");
		}
		if (berrySting != null) {
			crateRegistry.registerCrate(berrySting, "cratedNaturaBerrySting");
		}
		if (berryRasp != null) {
			crateRegistry.registerCrate(berryRasp, "cratedNaturaBerryRasp");
		}
		if (berryBlue != null) {
			crateRegistry.registerCrate(berryBlue, "cratedNaturaBerryBlue");
		}
		if (berryBlack != null) {
			crateRegistry.registerCrate(berryBlack, "cratedNaturaBerryBlack");
		}
		if (berryMalo != null) {
			crateRegistry.registerCrate(berryMalo, "cratedNaturaBerryMalo");
		}
		if (itemBarley != null) {
			crateRegistry.registerCrate(itemBarley, "cratedNaturaBarley");
		}
		if (saplingNatura != null) {
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 0), "cratedNaturaSaplingRedwood");
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 1), "cratedNaturaSaplingEucalyptus");
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 2), "cratedNaturaSaplingHopseed");
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 3), "cratedNaturaSaplingSakura");
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 4), "cratedNaturaSaplingGhostwood");
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 5), "cratedNaturaSaplingBlood");
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 6), "cratedNaturaSaplingDarkwood");
			crateRegistry.registerCrate(new ItemStack(saplingNatura, 1, 7), "cratedNaturaSaplingFusewood");
		}

		if (saplingNaturaRare != null) {
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 0), "cratedNaturaSaplingMaple");
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 1), "cratedNaturaSaplingSilverbell");
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 2), "cratedNaturaSaplingPurpleheart");
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 3), "cratedNaturaSaplingTiger");
			crateRegistry.registerCrate(new ItemStack(saplingNaturaRare, 1, 4), "cratedNaturaSaplingWillow");
		}
		Item potashApple = GameRegistry.findItem(NATURA, "Natura.netherfood");
		if (potashApple != null) {
			crateRegistry.registerCrate(new ItemStack(potashApple, 1, 0), "cratedNaturaPotashApple");
		}
		Item glowShroom = GameRegistry.findItem(NATURA, "Glowshroom");
		if (glowShroom != null) {
			crateRegistry.registerCrate(new ItemStack(glowShroom, 1, 0), "cratedNaturaGreenGlowshroom");
			crateRegistry.registerCrate(new ItemStack(glowShroom, 1, 1), "cratedNaturaPurpleGlowshroom");
			crateRegistry.registerCrate(new ItemStack(glowShroom, 1, 2), "cratedNaturaBlueGlowshroom");
		}
		if (logNatura != null) {
			crateRegistry.registerCrate(new ItemStack(logNatura, 1, 0), "cratedNaturaLogEucalyptus");
			crateRegistry.registerCrate(new ItemStack(logNatura, 1, 1), "cratedNaturaLogSakura");
			crateRegistry.registerCrate(new ItemStack(logNatura, 1, 2), "cratedNaturaLogGhostwood");
			crateRegistry.registerCrate(new ItemStack(logNatura, 1, 3), "cratedNaturaLogHopseed");
			crateRegistry.registerCrate(new ItemStack(logWillow, 1, 0), "cratedNaturaLogWillow");
		}
		Item bloodWood = GameRegistry.findItem(NATURA, "bloodwood");
		if (bloodWood != null) {
			crateRegistry.registerCrate(new ItemStack(bloodWood, 1, 0), "cratedNaturaLogBloodwood");
		}
		Item darkTree = GameRegistry.findItem(NATURA, "Dark Tree");
		if (darkTree != null) {
			crateRegistry.registerCrate(new ItemStack(darkTree, 1, 0), "cratedNaturaLogDarkwood");
			crateRegistry.registerCrate(new ItemStack(darkTree, 1, 1), "cratedNaturaLogFusewood");
		}
		Item heatSand = GameRegistry.findItem(NATURA, "heatsand");
		if (heatSand != null) {
			crateRegistry.registerCrate(new ItemStack(heatSand, 1, 0), "cratedNaturaBlockHeatsand");
		}
		Item taintedSoil = GameRegistry.findItem(NATURA, "soil.tainted");
		if (taintedSoil != null) {
			crateRegistry.registerCrate(new ItemStack(taintedSoil, 1, 0), "cratedNaturaBlockTainted");
		}
	}

	@Override
	protected void registerRecipes() {
		ItemStack seedBarley = GameRegistry.findItemStack(NATURA, "seedBarley", 1);
		ItemStack seedCotton = GameRegistry.findItemStack(NATURA, "seedCotton", 1);

		ArrayList<ItemStack> seedList = new ArrayList<ItemStack>();
		if (seedBarley != null) {
			seedList.add(seedBarley);
			RecipeManagers.moistenerManager.addRecipe(seedBarley, new ItemStack(Blocks.mycelium), 5000);
		}
		if (seedCotton != null) {
			seedList.add(seedCotton);
		}

		int amount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
		for (ItemStack aSeedList : seedList) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{aSeedList}, Fluids.SEEDOIL.getFluid(amount));
		}

		Block cropBlock = GameRegistry.findBlock(NATURA, "N Crops");
		Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(seedBarley, cropBlock, 3));
		Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(seedCotton, cropBlock, 8));
		
		List<ItemStack> berries = new ArrayList<ItemStack>();
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

		amount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") / 2;
		amount = Math.max(amount, 1); // Produce at least 1 mb of juice.
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(GameRegistry.findItem(NATURA, "Natura.netherfood"), 1, 0)}, Fluids.JUICE.getFluid(amount), ForestryItem.mulch.getItemStack(), GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple"));

		amount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") / 25;
		amount = Math.max(amount, 1); // Produce at least 1 mb of juice.

		for (ItemStack berry : berries) {
			RecipeManagers.squeezerManager.addRecipe(3, new ItemStack[]{berry}, Fluids.JUICE.getFluid(amount));
		}

		if (itemBarley != null) {
			RecipeUtil.injectLeveledRecipe(itemBarley, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			if (GameMode.getGameMode().getStackSetting("recipe.output.compost.wheat").stackSize > 0) {
				ItemStack compostWheat = GameMode.getGameMode().getStackSetting("recipe.output.compost.wheat");
				Proxies.common.addRecipe(compostWheat, " X ", "X#X", " X ", '#', Blocks.dirt, 'X', itemBarley);
			}
			FuelManager.moistenerResource.put(itemBarley, new MoistenerFuel(itemBarley, ForestryItem.mouldyWheat.getItemStack(), 0, 300));
		}
	}

}
