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
import com.google.common.collect.ImmutableMap;

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

		ImmutableMap<String, Integer> desertPlant = ImmutableMap.<String, Integer>builder()
				.put("desertApachePlume", 4)
				.put("desertBrittlebush", 4)
				.put("desertBroadLeafGilia", 4)
				.put("desertBroomSnakeweed", 4)
				.put("desertKangarooPaw", 4)
				.put("desertOcotillo", 4)
				.put("desertPeninsulaOnion", 4)
				.put("desertSeepwood", 4)
				.put("desertWhiteSage", 4)
				.build();

		ImmutableMap<String, Integer> nonGrowingFlowers = ImmutableMap.<String, Integer>builder()
				.put("flowerAchillea", 9) // number of flowers in the block, usually meta+1
				.put("flowerAlpineThistle", 1)
				.put("flowerAzalea", 13)
				.put("flowerBegonia", 9)
				.put("flowerBell", 10)
				.put("flowerBirdofParadise", 1)
				.put("flowerBlueStar", 1)
				.put("flowerBurningLove", 1)
				.put("flowerCandelabraAloe", 1)
				.put("flowerCarnation", 7)
				.put("flowerCelosia", 7)
						//.put("flowerColumbine", 5) //poisonous
				.put("flowerDahlia", 8)
				.put("flowerDaisy", 8)
				.put("flowerDelphinium", 4)
				.put("flowerDottedBlazingstar", 1)
				.put("flowerElephantEars", 1)
				.put("flowerFoamFlower", 1)
				.put("flowerFuchsia", 1)
				.put("flowerGeranium", 5)
				.put("flowerGladiolus", 9)
				.put("flowerHawkweed", 4)
				.put("flowerHydrangea", 6)
				.put("flowerJacobsLadder", 1)
				.put("flowerLily", 5)
				.put("flowerLionsTail", 1)
				.put("flowerLupine", 10)
				.put("flowerMarigold", 2)
				.put("flowerMediterraneanSeaHolly", 1)
						//.put("flowerMezereon", 1) //poisonous
				.put("flowerNemesia", 12)
				.put("flowerNewGuineaImpatiens", 1)
				.put("flowerParrotsBeak", 1)
				.put("flowerPeruvianLily", 1)
				.put("flowerPurpleConeflower", 1)
				.put("flowerRose", 13)
				.put("flowerRoseCampion", 1)
				.put("flowerStreamsideBluebells", 1)
				.put("flowerTorchLily", 1)
				.put("flowerTulip", 7)
				.put("flowerViolet", 1)
				.put("flowerWildCarrot", 1) //is not actually a carrot
				.put("flowerWildDaffodil", 1)
				.put("flowerWoodlandPinkroot", 1)
				.put("flowerYellowToadflax", 1)
				.build();

		ImmutableMap<String, Integer> cactusPlant = ImmutableMap.<String, Integer>builder()
				.put("cactusArmatocereusMatucanensis", 6)
				.put("cactusBaseballBat", 6)
				.put("cactusEchinocereusMetornii", 2)
				.put("cactusGoldenCereus", 3)
				.put("cactusGoldenSaguaro", 6)
				.put("cactusMatucanaAureiflora", 1)
				.put("cactusPricklyPear", 5)
				.put("cactusSnowPole", 6)
				.put("cactusToothpick", 6)
				.build();
		ImmutableMap<String, Integer> forestPlant = ImmutableMap.<String, Integer>builder()
				.put("forestArcticGentian", 1)
				.put("forestAustralianBugle", 2)
				.put("forestBroadleafMeadowsweet", 2)
				.put("forestDeadnettle", 2)
				.put("forestDeceivingTrillium", 2)
				.put("forestFairySlipper", 2)
				.put("forestHorseweed", 2)
				.put("forestKneelingAngelica", 2)
						//.put("forestLilyoftheValley", //poisonous
				.put("forestNorthernPitcherPlant", 2)
				.put("forestPinesap", 2)
				.put("forestRedHelleborine", 2)
				.put("forestSalal", 2)
				.put("forestVanillaLeaf", 1)
				.put("forestWesternWallflower", 2)
				.put("forestWildColumbine", 2)
				.put("forestWildMint", 2)
				.put("forestWolfsFootClubmoss", 1)
				.build();
		ImmutableMap<String, Integer> plainsPlant = ImmutableMap.<String, Integer>builder()
				.put("plainsPrairieBrome", 6) //skips some metas
				.put("plainsPrairieSage", 4)
				.put("plainsReedCanaryGrass", 6) //skips some metas
				.put("plainsShortrayFleabane", 4)
				.put("plainsSmallPasqueFlower", 4)
				.put("plainsSmoothAster", 4)
				.put("plainsThreeFloweredAvens", 4)
				.build();

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

		for (Map.Entry<String, Integer> flower : nonGrowingFlowers.entrySet()) {
			Block flowerPlantBlock = GameRegistry.findBlock(PlantMP, flower.getKey());
			if (flowerPlantBlock != null) {
				for (int i = 0; i < flower.getValue(); i++) {
					FlowerManager.flowerRegistry.registerPlantableFlower(flowerPlantBlock, i, 0.75, FlowerManager.FlowerTypeVanilla);
				}
			}
		}

		addMetaFlower(cactusPlant, FlowerManager.FlowerTypeCacti, false);
		addMetaFlower(desertPlant, FlowerManager.FlowerTypeCacti, false);

		addMetaFlower(forestPlant, FlowerManager.FlowerTypeVanilla, true);
		addMetaFlower(plainsPlant, FlowerManager.FlowerTypeVanilla, true);

		for (String wPlant : waterPlant) {
			ItemStack waterPlantStack = GameRegistry.findItemStack(PlantMP, wPlant, 1);
			if (waterPlantStack != null) {
				RecipeUtil.injectLeveledRecipe(waterPlantStack, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			}
		}
	}

	private static void addMetaFlower(ImmutableMap<String, Integer> flowerMap, String flowertype, boolean plantable) {
		for (Map.Entry<String, Integer> flower : flowerMap.entrySet()) {
			Block flowerBlock = GameRegistry.findBlock(PlantMP, flower.getKey());
			ItemStack flowerStack = GameRegistry.findItemStack(PlantMP, flower.getKey(), 1);
			FlowerManager.flowerRegistry.registerAcceptableFlower(flowerBlock, flowertype);
			if (plantable && flowerBlock != null) {
				FlowerManager.flowerRegistry.registerPlantableFlower(flowerBlock, 0, 0.75, flowertype);
			}
			if (flowerStack != null && flowerBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(flowerStack, flowerBlock, flower.getValue()));
			}
		}
	}
}
