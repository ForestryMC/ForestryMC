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
import net.minecraft.item.ItemStack;

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
import forestry.farming.logic.FarmableStacked;

@Plugin(pluginID = "PlantMegaPack", name = "PlantMegaPack", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.plantmegapack.description")
public class PluginPlantMegaPack extends ForestryPlugin {

	private static final String PlantMP = "plantmegapack";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(PlantMP);
	}

	@Override
	public String getFailMessage() {
		return "Plant Mega Pack not found";
	}

	@Override
	protected void registerRecipes() {

		ImmutableList<String> reedLikePlant = ImmutableList.of(
				"bambooAsper",
				"bambooFargesiaRobusta",
				"bambooGiantTimber",
				"bambooGolden",
				"bambooMoso",
				"bambooShortTassled",
				"bambooTimorBlack",
				"bambooTropicalBlue",
				"bambooWetForest"
		);

		ImmutableList<String> landCropPlant = ImmutableList.of(
				"Beet",
				"BellPepperYellow",
				"Celery",
				"Corn",
				"Cucumber",
				"Lettuce",
				"Onion",
				"Spinach",
				"Tomato"
		);

		ImmutableList<String> desertPlant = ImmutableList.of(
				"desertApachePlume",
				"desertBrittlebush",
				"desertBroadLeafGilia",
				"desertBroomSnakeweed",
				"desertKangarooPaw",
				"desertOcotillo",
				"desertPeninsulaOnion",
				"desertSeepwood",
				"desertWhiteSage"

		);

		Map<String, Integer> nonGrowingFlowers = new HashMap<String, Integer>();
		nonGrowingFlowers.put("flowerAchillea", 9); // number of flowers in the block, usually meta+1
		nonGrowingFlowers.put("flowerAlpineThistle", 1);
		nonGrowingFlowers.put("flowerAzalea", 13);
		nonGrowingFlowers.put("flowerBegonia", 9);
		nonGrowingFlowers.put("flowerBell", 10);
		nonGrowingFlowers.put("flowerBirdofParadise", 1);
		nonGrowingFlowers.put("flowerBlueStar", 1);
		nonGrowingFlowers.put("flowerBurningLove", 1);
		nonGrowingFlowers.put("flowerCandelabraAloe", 1);
		nonGrowingFlowers.put("flowerCarnation", 7);
		nonGrowingFlowers.put("flowerCelosia", 7);
		//nonGrowingFlowers.put("flowerColumbine",5); //poisonous
		nonGrowingFlowers.put("flowerDahlia", 8);
		nonGrowingFlowers.put("flowerDaisy", 8);
		nonGrowingFlowers.put("flowerDelphinium", 4);
		nonGrowingFlowers.put("flowerDottedBlazingstar", 1);
		nonGrowingFlowers.put("flowerElephantEars", 1);
		nonGrowingFlowers.put("flowerFoamFlower", 1);
		nonGrowingFlowers.put("flowerFuchsia", 1);
		nonGrowingFlowers.put("flowerGeranium", 5);
		nonGrowingFlowers.put("flowerGladiolus", 9);
		nonGrowingFlowers.put("flowerHawkweed", 4);
		nonGrowingFlowers.put("flowerHydrangea", 6);
		nonGrowingFlowers.put("flowerJacobsLadder", 1);
		nonGrowingFlowers.put("flowerLily", 5);
		nonGrowingFlowers.put("flowerLionsTail", 1);
		nonGrowingFlowers.put("flowerLupine", 10);
		nonGrowingFlowers.put("flowerMarigold", 2);
		nonGrowingFlowers.put("flowerMediterraneanSeaHolly", 1);
		//nonGrowingFlowers.put("flowerMezereon",1); //poisonous
		nonGrowingFlowers.put("flowerNemesia", 12);
		nonGrowingFlowers.put("flowerNewGuineaImpatiens", 1);
		nonGrowingFlowers.put("flowerParrotsBeak", 1);
		nonGrowingFlowers.put("flowerPeruvianLily", 1);
		nonGrowingFlowers.put("flowerPurpleConeflower", 1);
		nonGrowingFlowers.put("flowerRose", 13);
		nonGrowingFlowers.put("flowerRoseCampion", 1);
		nonGrowingFlowers.put("flowerStreamsideBluebells", 1);
		nonGrowingFlowers.put("flowerTorchLily", 1);
		nonGrowingFlowers.put("flowerTulip", 7);
		nonGrowingFlowers.put("flowerViolet", 1);
		nonGrowingFlowers.put("flowerWildCarrot", 1); //is not actually a carrot
		nonGrowingFlowers.put("flowerWildDaffodil", 1);
		nonGrowingFlowers.put("flowerWoodlandPinkroot", 1);
		nonGrowingFlowers.put("flowerYellowToadflax", 1);

		Map<String, Integer> cactusPlant = new HashMap<String, Integer>();
		cactusPlant.put("cactusArmatocereusMatucanensis", 6);
		cactusPlant.put("cactusBaseballBat", 6);
		cactusPlant.put("cactusEchinocereusMetornii", 2);
		cactusPlant.put("cactusGoldenCereus", 3);
		cactusPlant.put("cactusGoldenSaguaro", 6);
		cactusPlant.put("cactusMatucanaAureiflora", 1);
		cactusPlant.put("cactusPricklyPear", 5);
		cactusPlant.put("cactusSnowPole", 6);
		cactusPlant.put("cactusToothpick", 6);


		ImmutableList<String> waterPlant = ImmutableList.of(
				"waterKelpGiantGRN",
				"waterKelpGiantYEL"
		);

		int seedamount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
		int juiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
		for (String reedLike : reedLikePlant) {
			Block reedBlock = GameRegistry.findBlock(PlantMP, reedLike);
			ItemStack reedStack = GameRegistry.findItemStack(PlantMP, reedLike, 1);
			if (reedBlock != null && reedStack != null) {
				RecipeUtil.injectLeveledRecipe(reedStack, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
				Farmables.farmables.get("farmPoales").add(new FarmableStacked(reedBlock, 14, 4));
			}
		}

		for (String landCrop : landCropPlant) {
			Block landCropBlock = GameRegistry.findBlock(PlantMP, "crop" + landCrop);
			ItemStack seedStack = GameRegistry.findItemStack(PlantMP, "seed" + landCrop, 1);
			ItemStack foodStack = GameRegistry.findItemStack(PlantMP, "food" + landCrop, 1);
			if (landCropBlock != null) {
				if (foodStack != null) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{foodStack}, Fluids.JUICE.getFluid(juiceAmount));
				}
				if (seedStack != null) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{seedStack}, Fluids.SEEDOIL.getFluid(seedamount));
				}
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(seedStack, landCropBlock, 4));
			}
		}
		for (String dPlant : desertPlant) {
			Block desertPlantBlock = GameRegistry.findBlock(PlantMP, dPlant);
			ItemStack desertPlantStack = GameRegistry.findItemStack(PlantMP, dPlant, 1);
			if (desertPlantBlock != null && desertPlantStack != null) {
				FlowerManager.flowerRegistry.registerAcceptableFlower(desertPlantBlock, FlowerManager.FlowerTypeCacti);
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(desertPlantStack, desertPlantBlock, 4));
			}
		}

		for (Map.Entry<String, Integer> flower : nonGrowingFlowers.entrySet()) {
			Block flowerPlantBlock = GameRegistry.findBlock(PlantMP, flower.getKey());
			if (flowerPlantBlock != null) {
				for (int i = 0; i < flower.getValue(); i++) {
					FlowerManager.flowerRegistry.registerPlantableFlower(flowerPlantBlock, i, 0.75, FlowerManager.FlowerTypeVanilla);
				}
			}
		}

		for (Map.Entry<String, Integer> cPlant : cactusPlant.entrySet()) {
			Block cactusPlantBlock = GameRegistry.findBlock(PlantMP, cPlant.getKey());
			ItemStack cactusPlantStack = GameRegistry.findItemStack(PlantMP, cPlant.getKey(), 1);
			if (cactusPlantBlock != null) {
				FlowerManager.flowerRegistry.registerAcceptableFlower(cactusPlantBlock, FlowerManager.FlowerTypeCacti);
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(cactusPlantStack, cactusPlantBlock, cPlant.getValue()));
			}
		}
		for (String wPlant : waterPlant) {
			ItemStack waterPlantStack = GameRegistry.findItemStack(PlantMP, wPlant, 1);
			if (waterPlantStack != null) {
				RecipeUtil.injectLeveledRecipe(waterPlantStack, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			}
		}
	}
}
