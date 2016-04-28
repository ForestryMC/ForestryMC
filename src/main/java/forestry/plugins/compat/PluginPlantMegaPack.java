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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.apiculture.FlowerManager;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableBasicFruit;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.farming.logic.FarmableStacked;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "PlantMegaPack", name = "PlantMegaPack", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.plantmegapack.description")
public class PluginPlantMegaPack extends ForestryPlugin {

	private static final String PlantMP = "plantmegapack";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(PlantMP);
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
				"BellPepperOrange",
				"BellPepperRed",
				"BellPepperYellow",
				"Broccoli",
				"Cassava",
				"Celery",
				"Corn",
				"Cucumber",
				"Eggplant",
				"GreenBean",
				"Leek",
				"Lettuce",
				"Onion",
				"Sorrel",
				"Spinach",
				"Tomato"
		);

		ImmutableMap<String, Integer> desertPlant = ImmutableMap.<String, Integer>builder()
				.put("cactusArmatocereusMatucanensis", 6)
				.put("cactusBaseballBat", 6)
				.put("cactusEchinocereusMetornii", 2)
				.put("cactusGoldenCereus", 3)
				.put("cactusGoldenSaguaro", 6)
				.put("cactusMatucanaAureiflora", 1)
				.put("cactusPricklyPear", 5)
				.put("cactusSnowPole", 6)
				.put("cactusToothpick", 6)

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
				.put("floatingWaterHyacinth", 1)
				.put("floatingWaterLily", 13)
				.build();

		ImmutableMap<String, Integer> floatingWaterPlant = ImmutableMap.<String, Integer>builder()
				.put("cropSacredLotus", 4)
				.put("cropWatercress", 4)
				.put("cropTaro", 4)
				.put("cropWasabi", 4)
				.put("cropLaksaLeaf", 4)
				.put("cropCentella", 4)
				.put("cropRice", 4)
				.put("cropWaterSpinach", 4)
				.put("cropWildRice", 4)

				.put("immersedArrowArum", 4)
				.put("immersedCommonReed", 4)
				.put("immersedDuckPotato", 4)
				.put("immersedEuropeanBurReed", 4)
				.put("immersedGreySedge", 4)
				.put("immersedSimplestemBurReed", 4)
				.put("immersedSoftstemBulrush", 4)
				.put("immersedWaterMannagrass", 4)
				.put("immersedYellowFlag", 4)
				.build();

		ImmutableMap<String, Integer> fungusPlant = ImmutableMap.<String, Integer>builder()
				.put("fungusBlackPowderpuff", 1)
				.put("fungusChanterelle", 1)
				//.put("fungusDeathCap", 1) poisonous
				.put("fungusGiantClub", 1)
				.put("fungusParasol", 2)
				.put("fungusStinkhorn", 1)
				.put("fungusWeepingMilkCap", 1)
				.put("fungusWoodBlewit", 1)
				//.put("fungusWoollyGomphus", 2) poisonous
				.build();

		ImmutableMap<String, Integer> junglePlant = ImmutableMap.<String, Integer>builder()
				.put("jungleConeHeadedGuzmania", 2)
				.put("jungleDevilsTongue", 6)
				.put("jungleHoneySpurge", 1)
				.put("jungleJungleLantern", 6)
				.put("jungleLazarusBell", 2)
				.put("jungleLobsterClaws", 6)
				.put("jungleLollipopPlant", 2)
				.put("jungleMadagascarPalm", 6)
				.put("junglePalmLily", 6)
				.put("junglePanamaQueen", 2)
				.put("junglePorteaAlatisepala", 2)
				.put("jungleRacinaeaFraseri", 6)
				.put("jungleRicheaDracophylla", 2)
				//.put("jungleShellflower", 4) 2 tall needs specialcase
				.put("jungleSilverVase", 2)
				.put("jungleStaghornClubmoss", 2)
				.put("jungleTorchGinger", 2)
				.put("jungleVoodooLily", 6)
				.put("leafyEmeraldPhilodendron", 2) //jungle plant
				.build();

		ImmutableMap<String, Integer> vanillaPlant = ImmutableMap.<String, Integer>builder()
				.put("beachHighTideBush", 6)
				.put("beachSeaLavender", 4)
				.put("beachSeaSandwort", 4)
				.put("leafyDustyMiller", 1) //beach plant

				.put("fernCretanBrake", 1)
				//.put("fernDwarfPalmetto", 0) doesn't grow
				.put("fernHayScented", 2)
				.put("fernKangaroo", 2)
				.put("fernMaidenhairSpleenwort", 3)
				.put("fernOstrich", 6)
				.put("fernScalyTree", 6)
				.put("fernSword", 6)
				.put("fernWoodsia", 2)

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
				.put("leafyColeus", 1) //forest plant
				.put("leafyStonecrop", 1) //forest plant

				.put("grassBlueWheatgrass", 1)
				.put("grassCord", 6)
				.put("grassFountain", 2)
				.put("grassMeadow", 2)
				.put("grassMeadowFoxtail", 1)
				.put("grassPrairie", 1)
				.put("grassSilverMoor", 2)
				.put("grassSwitch", 6)

				.put("mountainAlpineArmeria", 2)
				.put("mountainAlpineBellflower", 2)
				.put("mountainBistort", 2)
				.put("mountainEdelweiss", 2)
				.put("mountainHouseleek", 2)
				.put("mountainIris", 1)
				.put("mountainNorthernWillowherb", 2)
				.put("mountainScarletTrumpet", 2)
				.put("mountainYellowBellflower", 1)
				.put("leafyKrisPlant", 2) // mountain plaint

				.put("plainsPrairieBrome", 6) //skips some metas
				.put("plainsPrairieSage", 4)
				.put("plainsReedCanaryGrass", 6) //skips some metas
				.put("plainsShortrayFleabane", 4)
				.put("plainsSmallPasqueFlower", 4)
				.put("plainsSmoothAster", 4)
				.put("plainsThreeFloweredAvens", 4)
				.put("leafyPricklyLettuce", 1) //plains plant
				//.put("leafyStingingNettle", 2) plains plant, poisonous

				.put("savannaButterflyWeed", 2)
				.put("savannaHoaryVervain", 2)
				.put("savannaLeadplant", 2)
				.put("savannaMarcela", 2)
				.put("savannaNorthernBedstraw", 1)
				.put("savannaPiersonsMilkVetch", 2)
				.put("savannaPropellerPlant", 2)
				.put("savannaShonaCabbage", 2)
				.put("savannaTexasTickseed", 2)
				.put("leafyPaleYucca", 2) //savanna Plant
				.put("leafyDevilsShoestring", 2)

				.put("shrubAlpineCurrant", 2)
				.put("shrubBarberry", 2)
				.put("shrubBoxwood", 2)
				.put("shrubButterfly", 2)
				.put("shrubCanyonCreekAbelia", 2)
				.put("shrubCapeJasmine", 2)
				.put("shrubCedarCylinder", 6)
				.put("shrubCedarGlobe", 2)
				.put("shrubCedarPyramid", 6)
				.put("shrubCypressGreen", 1)
				.put("shrubCypressYellow", 1)
				.put("shrubDaphne", 2)
				//.put("shrubDwarfElder", 4) poisonous, also grows 2 tall at meta 4
				.put("shrubHolly", 2)
				.put("shrubHummingbirdBush", 2)
				.put("shrubJuniperSavin", 2)
				.put("shrubKerria", 2)
				.put("shrubLavender", 2)
				.put("shrubLindera", 3)
				.put("shrubMeadowsweet", 2)
				.put("shrubMottlecah", 2)
				.put("shrubNinebark", 2)
				.put("shrubSargentViburnum", 2)
				//.put("shrubSpicebush",2) poisonous
				.put("shrubWeepingForsythia", 2)
				.put("shrubWinterberry", 2)
				.put("shrubWolfWillow", 2)

				.put("wetlandsCattails", 6)
				.put("wetlandsClubrush", 2)
				.put("wetlandsCommonRush", 2)
				.put("wetlandsFloweringRush", 2)
				.put("wetlandsPickerelweed", 2)
				.put("wetlandsReedMannagrass", 2)
				.put("wetlandsSwampMilkweed", 2)
				.put("wetlandsWaterHorsetail", 1)
				.put("wetlandsWhiteTurtlehead", 2)
				.build();

		ImmutableMap<String, Integer> berryBushPlant = ImmutableMap.<String, Integer>builder()
				.put("Beauty", 4)
				.put("Black", 4)
				.put("Blue", 4)
				.put("Elder", 4)
				.put("Goose", 4)
				.put("Huckle", 4)
				.put("Orange", 4)
				.put("Snow", 4)
				.put("Straw", 4)
				.build();

		ImmutableList<String> waterPlant = ImmutableList.of(
				"oceanCommonEelgrass",
				"oceanCoralWeed",
				"oceanMozuku",
				"oceanRockweed",
				"oceanSeaGrapes",
				"oceanSeaLettuce",
				"oceanTangle",
				"riverAmazonSword",
				"riverCanadianWaterweed",
				"riverCoonsTail",
				"riverEelgrass",
				"riverWaterWisteria",
				"riverWrightsWaternymph",
				"waterKelpGiantGRN",
				"waterKelpGiantYEL"
		);

		ImmutableMap<String, String> specialCaseTwoTall = ImmutableMap.<String, String>builder()
				.put("grassSeaOats", FlowerManager.FlowerTypeVanilla)
				.put("jungleShellflower", FlowerManager.FlowerTypeJungle)
				.build();

		int seedamount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		int juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");

		for (String reedLike : reedLikePlant) {
			Block reedBlock = GameRegistry.findBlock(PlantMP, reedLike);
			ItemStack reedStack = GameRegistry.findItemStack(PlantMP, reedLike, 1);
			if (reedBlock != null && reedStack != null) {
				RecipeUtil.addFermenterRecipes(reedStack, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
				if (PluginManager.Module.FARMING.isEnabled()) {
					Farmables.farmables.get("farmPoales").add(new FarmableStacked(reedBlock, 14, 4));
				}
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
				if (PluginManager.Module.FARMING.isEnabled()) {
					Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(seedStack, landCropBlock, 4));
					Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(landCropBlock, 4));
				}
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

		for (Map.Entry<String, Integer> wPlant : floatingWaterPlant.entrySet()) {
			Block waterPlantBlock = GameRegistry.findBlock(PlantMP, wPlant.getKey());
			if (PluginManager.Module.FARMING.isEnabled() && waterPlantBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(waterPlantBlock, wPlant.getValue()));
			}
		}

		juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 25;
		juiceAmount = Math.max(juiceAmount, 1); // Produce at least 1 mb of juice.

		for (Map.Entry<String, Integer> berryBushCrop : berryBushPlant.entrySet()) {
			Block berryBushCropBlock = GameRegistry.findBlock(PlantMP, "berrybush" + berryBushCrop.getKey());
			ItemStack berryBushStack = GameRegistry.findItemStack(PlantMP, "berrybush" + berryBushCrop.getKey(), 1);
			ItemStack foodStack = GameRegistry.findItemStack(PlantMP, "berries" + berryBushCrop.getKey(), 1);
			if (berryBushCropBlock != null) {
				if (foodStack != null) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{foodStack}, Fluids.JUICE.getFluid(juiceAmount));
				}
				if (PluginManager.Module.FARMING.isEnabled()) {
					Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(berryBushStack, berryBushCropBlock, berryBushCrop.getValue()));
					Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(berryBushCropBlock, berryBushCrop.getValue()));
				}
			}
		}

		addMetaFlower(desertPlant, false, FlowerManager.FlowerTypeCacti);
		addMetaFlower(junglePlant, false, FlowerManager.FlowerTypeJungle);
		addMetaFlower(vanillaPlant, true, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);
		addMetaFlower(fungusPlant, true, FlowerManager.FlowerTypeMushrooms);

		for (Map.Entry<String, String> special : specialCaseTwoTall.entrySet()) {
			Block plantBlock = GameRegistry.findBlock(PlantMP, special.getKey());
			ItemStack specialStack = GameRegistry.findItemStack(PlantMP, special.getKey(), 1);
			if (plantBlock != null && plantBlock != null) {
				FlowerManager.flowerRegistry.registerAcceptableFlower(plantBlock, special.getValue());
				FlowerManager.flowerRegistry.registerPlantableFlower(plantBlock, 0, 0.75, special.getValue());
				if (PluginManager.Module.FARMING.isEnabled()) {
					Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(specialStack, plantBlock, 4));
				}
			}
		}

		for (String wPlant : waterPlant) {
			ItemStack waterPlantStack = GameRegistry.findItemStack(PlantMP, wPlant, 1);
			if (waterPlantStack != null) {
				RecipeUtil.addFermenterRecipes(waterPlantStack, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			}
		}
	}

	private static void addMetaFlower(ImmutableMap<String, Integer> flowerMap, boolean plantable, String... flowertype) {
		for (Map.Entry<String, Integer> flower : flowerMap.entrySet()) {
			Block flowerBlock = GameRegistry.findBlock(PlantMP, flower.getKey());
			ItemStack flowerStack = GameRegistry.findItemStack(PlantMP, flower.getKey(), 1);
			FlowerManager.flowerRegistry.registerAcceptableFlower(flowerBlock, flowertype);
			if (plantable && flowerBlock != null) {
				FlowerManager.flowerRegistry.registerPlantableFlower(flowerBlock, 0, 0.75, flowertype);
			}
			if (PluginManager.Module.FARMING.isEnabled() && flowerStack != null && flowerBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(flowerStack, flowerBlock, flower.getValue()));
				if (flower.getValue() < 5) {
					Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(flowerBlock, flower.getValue()));
				}
			}
		}
	}
}
