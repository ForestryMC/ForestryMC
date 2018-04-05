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

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmRegistry;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.ModuleCore;
import forestry.core.ModuleFluids;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.HARVESTCRAFT, name = "HarvestCraft", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.module.harvestcraft.description")
public class PluginHarvestCraft extends CompatPlugin {

	public PluginHarvestCraft() {
		super("HarvestCraft", "harvestcraft");
	}

	@Override
	public void registerRecipes() {
		boolean farmingModuleEnabled = ModuleHelper.isEnabled(ForestryModuleUids.FARMING);
		ImmutableList<String> berries = ImmutableList.of(
				"cranberry",
				"blackberry",
				"blueberry",
				"raspberry",
				"strawberry"
		);

		ImmutableList<String> fruits = ImmutableList.of(
				"pineapple",
				"cactusfruit",
				"cantaloupe",
				"grape",
				"kiwi",
				"chilipepper"
		);

		ImmutableList<String> treeFruits = ImmutableList.of(
				"banana",
				"dragonfruit",
				"lemon",
				"lime",
				"mango",
				"orange",
				"papaya",
				"peach",
				"pear",
				"plum",
				"pomegranate",
				"starfruit",
				"apricot",
				"date",
				"fig",
				"grapefruit",
				"persimmon",
				"avocado",
				"coconut",
				"durian"
		);

		ImmutableList<String> trees = ImmutableList.of(
				"nutmeg",
				"olive",
				"peppercorn"
		);

		ImmutableList<String> treesSpecial = ImmutableList.of(
				"cinnamon",
				"maple",
				"paperbark",
				"vanillabean",
				"apple" // to prevent apples from getting double registered
		);

		ImmutableList<String> herbs = ImmutableList.of(
				"garlic"
		);

		ImmutableList<String> spices = ImmutableList.of(
				"ginger",
				"spiceleaf"
				//"mustardseed" Mustard is inconsistent and annoying.
		);

		ImmutableList<String> vegetables = ImmutableList.of(
				"asparagus",
				"bean",
				"beet",
				"broccoli",
				"cauliflower",
				"celery",
				"leek",
				"lettuce",
				"onion",
				"parsnip",
				"radish",
				"rutabaga",
				"scallion",
				"soybean",
				"sweetpotato",
				"turnip",
				"whitemushroom",
				"artichoke",
				"bellpepper",
				"brusselsprout",
				"cabbage",
				"corn",
				"cucumber",
				"eggplant",
				"okra",
				"peas",
				"rhubarb",
				"seaweed",
				"tomato",
				"wintersquash",
				"zucchini",
				"bambooshoot",
				"spinach"
		);

		ImmutableList<String> grains = ImmutableList.of(
				"barley",
				"oats",
				"rye"
		);

		ImmutableList<String> cropNuts = ImmutableList.of(
				"peanut"
		);

		ImmutableList<String> nuts = ImmutableList.of(
				"walnut",
				"almond",
				"cashew",
				"chestnut",
				"pecan",
				"pistachio",
				"cherry" //Cherries in forestry make seed oil
		);

		ImmutableList.Builder<String> genericCropsBuilder = ImmutableList.builder();
		genericCropsBuilder.add(
				"cotton",
				"rice",
				"tealeaf",
				"coffeebean",
				"candleberry"
		);
		genericCropsBuilder.addAll(herbs);
		genericCropsBuilder.addAll(spices);

		IFarmRegistry farmRegistry = ForestryAPI.farmRegistry;
		
		ImmutableList<String> genericCrops = genericCropsBuilder.build();

		ImmutableList.Builder<String> plants = ImmutableList.builder();

		int juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 25;
		int seedamount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		int wheatamount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
		PropertyInteger plantAGE = PropertyInteger.create("age", 0, 3);
		PropertyInteger fruitAGE = PropertyInteger.create("age", 0, 2);

		juiceAmount = Math.max(juiceAmount, 1); // Produce at least 1 mb of juice.
		for (String berryName : berries) {
			ItemStack berry = getItemStack(berryName + "item");
			ItemStack berrySeed = getItemStack( berryName + "seeditem");
			Block berryBlock = getBlock("pam" + berryName + "crop");
			if (berry != null) {
				RecipeManagers.squeezerManager.addRecipe(10, berry, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (berrySeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, berrySeed, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (farmingModuleEnabled && berrySeed != null && berryBlock != null) {
				farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(berrySeed, berryBlock, plantAGE, 3, 0));
			}
			plants.add(berryName);
		}

		juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		for (String fruitName : fruits) {
			ItemStack fruit = getItemStack(fruitName + "item");
			ItemStack fruitSeed = getItemStack(fruitName + "seeditem");
			Block fruitBlock = getBlock("pam" + fruitName + "crop");
			if (fruit != null) {
				RecipeManagers.squeezerManager.addRecipe(10, fruit, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (fruitSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, fruitSeed, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (farmingModuleEnabled && fruitSeed != null && fruitBlock != null) {
				farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(fruitSeed, fruitBlock, plantAGE, 3, 0));
			}
			plants.add(fruitName);
		}

		juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 2; // vegetables produce less juice
		juiceAmount = Math.max(juiceAmount, 1); // Produce at least 1 mb of juice.
		for (String vegetableName : vegetables) {
			ItemStack vegetable = getItemStack(vegetableName + "item");
			ItemStack vegetableSeed = getItemStack(vegetableName + "seeditem");
			Block vegetableBlock = getBlock("pam" + vegetableName + "crop");
			if (vegetable != null) {
				RecipeManagers.squeezerManager.addRecipe(10, vegetable, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (vegetableSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, vegetableSeed, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (farmingModuleEnabled && vegetableSeed != null && vegetableBlock != null) {
				farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(vegetableSeed, vegetableBlock, plantAGE, 3, 0));
			}
			plants.add(vegetableName);
		}

		ItemRegistryCore coreItems = ModuleCore.getItems();
		for (String grainName : grains) {
			ItemStack grain = getItemStack(grainName + "item");
			ItemStack grainSeed = getItemStack(grainName + "seeditem");
			Block grainBlock = getBlock("pam" + grainName + "crop");
			if (grain != null && wheatamount > 0) {
				RecipeUtil.addRecipe("pam_compost_" + grainName, coreItems.compost.getItemStack(wheatamount), " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', grain);
				FuelManager.moistenerResource.put(grain, new MoistenerFuel(grain, coreItems.mouldyWheat.getItemStack(), 0, 300));
			}
			if (grainSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, grainSeed, Fluids.SEED_OIL.getFluid(seedamount));
				RecipeManagers.moistenerManager.addRecipe(grainSeed, new ItemStack(Blocks.MYCELIUM), 5000);
			}
			if (farmingModuleEnabled && grainSeed != null && grainBlock != null) {
				farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(grainSeed, grainBlock, plantAGE, 3, 0));
			}
			plants.add(grainName);
		}

		for (String treeFruitName : treeFruits) {
			ItemStack treeFruit = getItemStack(treeFruitName + "item");
			Block treeFruitBlock = getBlock("pam" + treeFruitName);
			if (farmingModuleEnabled && treeFruitBlock != null) {
				farmRegistry.registerFarmables("farmOrchard", new FarmableAgingCrop(ItemStack.EMPTY, treeFruitBlock, fruitAGE, 2, 0));
			}
			if (treeFruit != null) {
				RecipeManagers.squeezerManager.addRecipe(10, treeFruit, Fluids.JUICE.getFluid(juiceAmount));
			}
			plants.add(treeFruitName);
		}

		for (String treeName : trees) {
			Block fruitBlock = getBlock("pam" + treeName);
			if (farmingModuleEnabled && fruitBlock != null) {
				farmRegistry.registerFarmables("farmOrchard", new FarmableAgingCrop(ItemStack.EMPTY, fruitBlock, fruitAGE, 2, 0));
			}
			plants.add(treeName);
		}

		for (String treeName : treesSpecial) {
			Block fruitBlock = getBlock("pam" + treeName);
			if (farmingModuleEnabled && fruitBlock != null) {
				farmRegistry.registerFarmables("farmOrchard", new FarmableAgingCrop(ItemStack.EMPTY, fruitBlock, fruitAGE, 2, 0));
			}
		}

		for (String cropName : genericCrops) {
			String seedPrefix = cropName;
			if (seedPrefix.equals("tealeaf")) {
				seedPrefix = "tea";
			}
			if (seedPrefix.equals("coffeebean")) {
				seedPrefix = "coffee";
			}
			ItemStack genericCropSeed = getItemStack(seedPrefix + "seeditem");
			Block genericCropBlock = getBlock("pam" + cropName + "crop");
			if (genericCropSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, genericCropSeed, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (farmingModuleEnabled && genericCropSeed != null && genericCropBlock != null) {
				farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(genericCropSeed, genericCropBlock, plantAGE, 3, 0));
			}
			plants.add(cropName);
		}
		ItemStack mustardCropSeed = getItemStack("mustard" + "seeditem");
		Block mustardCropBlock = getBlock("pam" + "mustardseeds" + "crop");
		ItemStack mustardFruit = getItemStack("mustard" + "seedsitem");
		if (mustardCropSeed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, mustardCropSeed, Fluids.SEED_OIL.getFluid(seedamount));
		}
		if (farmingModuleEnabled && mustardCropSeed != null && mustardCropBlock != null) {
			farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(mustardCropSeed, mustardCropBlock, plantAGE, 3, 0));
		}
		if (mustardFruit != null) {
			RecipeUtil.addFermenterRecipes(mustardFruit, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		}

		for (String plantName : plants.build()) {
			ItemStack plant = getItemStack(plantName + "item");
			if (plant != null) {
				RecipeUtil.addFermenterRecipes(plant, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			}
		}

		for (String cropnutName : cropNuts) {
			ItemStack cropnut = getItemStack(cropnutName + "item");
			ItemStack cropnutSeed = getItemStack(cropnutName + "seeditem");
			Block cropnutBlock = getBlock("pam" + cropnutName + "crop");
			if (cropnutSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, cropnutSeed, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (farmingModuleEnabled && cropnutSeed != null && cropnutBlock != null) {
				farmRegistry.registerFarmables("farmCrops", new FarmableAgingCrop(cropnutSeed, cropnutBlock, plantAGE, 3, 0));
			}
			if (cropnut != null) {
				RecipeManagers.squeezerManager.addRecipe(20, cropnut, Fluids.SEED_OIL.getFluid(12 * seedamount));
			}
		}

		for (String nutName : nuts) {
			ItemStack nut = getItemStack(nutName + "item");
			Block nutBlock = getBlock("pam" + nutName);
			if (farmingModuleEnabled && nutBlock != null) {
				farmRegistry.registerFarmables("farmOrchard", new FarmableAgingCrop(ItemStack.EMPTY, nutBlock, fruitAGE, 2, 0));
			}
			if (nut != null) {
				RecipeManagers.squeezerManager.addRecipe(20, nut, Fluids.SEED_OIL.getFluid(15 * seedamount));
			}
		}
			
		if(ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemStack hcHoneyItem = getItemStack("honeyitem");
			if (hcHoneyItem != null) {
				RecipeManagers.squeezerManager.addRecipe(10, hcHoneyItem, Fluids.FOR_HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP));
			}
	
			ItemStack hcBeeswaxItem = getItemStack("beeswaxitem");
			if (hcBeeswaxItem != null) {
				RecipeUtil.addRecipe("pam_wax_capsule", ModuleFluids.getItems().waxCapsuleEmpty.getItemStack(ForestryAPI.activeMode.getIntegerSetting("recipe.output.capsule")), "XXX ", 'X', hcBeeswaxItem);
			}
		}
	}

}
