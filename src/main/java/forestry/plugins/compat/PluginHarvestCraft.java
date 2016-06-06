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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableBasicFruit;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "HarvestCraft", name = "HarvestCraft", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.harvestcraft.description")
public class PluginHarvestCraft extends ForestryPlugin {

	private static final String HC = "harvestcraft";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(HC);
	}

	@Override
	public String getFailMessage() {
		return "HarvestCraft not found";
	}

	@Override
	protected void registerRecipes() {

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
				"durian",
				"gooseberry"
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
				"spiceleaf",
				"curryleaf"
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
				"spinach",
				"waterchestnut"
		);

		ImmutableList<String> grains = ImmutableList.of(
				"barley",
				"oats",
				"rye"
		);

		ImmutableList<String> cropNuts = ImmutableList.of(
				"peanut",
				"sesameseeds"
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
				"tea",
				"coffee",
				"candleberry"
		);
		genericCropsBuilder.addAll(herbs);
		genericCropsBuilder.addAll(spices);

		ImmutableList<String> genericCrops = genericCropsBuilder.build();

		ImmutableList.Builder<String> plants = ImmutableList.builder();

		int juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 25;
		int seedamount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		ItemStack wheatamount = ForestryAPI.activeMode.getStackSetting("recipe.output.compost.wheat");

		juiceAmount = Math.max(juiceAmount, 1); // Produce at least 1 mb of juice.
		for (String berryName : berries) {
			ItemStack berry = GameRegistry.findItemStack(HC, berryName + "Item", 1);
			ItemStack berrySeed = GameRegistry.findItemStack(HC, berryName + "seedItem", 1);
			Block berryBlock = GameRegistry.findBlock(HC, "pam" + berryName + "Crop");
			if (berry != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{berry}, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (berrySeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{berrySeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (PluginManager.Module.FARMING.isEnabled() && berrySeed != null && berryBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(berrySeed, berryBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(berryBlock, 7));
			}
			plants.add(berryName);
		}

		juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		for (String fruitName : fruits) {
			ItemStack fruit = GameRegistry.findItemStack(HC, fruitName + "Item", 1);
			ItemStack fruitSeed = GameRegistry.findItemStack(HC, fruitName + "seedItem", 1);
			Block fruitBlock = GameRegistry.findBlock(HC, "pam" + fruitName + "Crop");
			if (fruit != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{fruit}, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (fruitSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{fruitSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (PluginManager.Module.FARMING.isEnabled() && fruitSeed != null && fruitBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(fruitSeed, fruitBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(fruitBlock, 7));
			}
			plants.add(fruitName);
		}

		juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 2; // vegetables produce less juice
		juiceAmount = Math.max(juiceAmount, 1); // Produce at least 1 mb of juice.
		for (String vegetableName : vegetables) {
			ItemStack vegetable = GameRegistry.findItemStack(HC, vegetableName + "Item", 1);
			ItemStack vegetableSeed = GameRegistry.findItemStack(HC, vegetableName + "seedItem", 1);
			Block vegetableBlock = GameRegistry.findBlock(HC, "pam" + vegetableName + "Crop");
			if (vegetable != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{vegetable}, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (vegetableSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{vegetableSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (PluginManager.Module.FARMING.isEnabled() && vegetableSeed != null && vegetableBlock != null) {
				Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(vegetableSeed, vegetableBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(vegetableBlock, 7));
			}
			plants.add(vegetableName);
		}

		for (String grainName : grains) {
			ItemStack grain = GameRegistry.findItemStack(HC, grainName + "Item", 1);
			ItemStack grainSeed = GameRegistry.findItemStack(HC, grainName + "seedItem", 1);
			Block grainBlock = GameRegistry.findBlock(HC, "pam" + grainName + "Crop");
			if (grain != null && wheatamount.stackSize > 0) {
				RecipeUtil.addRecipe(wheatamount, " X ", "X#X", " X ", '#', Blocks.dirt, 'X', grain);
				FuelManager.moistenerResource.put(grain, new MoistenerFuel(grain, PluginCore.items.mouldyWheat.getItemStack(), 0, 300));
			}
			if (grainSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{grainSeed}, Fluids.SEEDOIL.getFluid(seedamount));
				RecipeManagers.moistenerManager.addRecipe(grainSeed, new ItemStack(Blocks.mycelium), 5000);
			}
			if (PluginManager.Module.FARMING.isEnabled() && grainSeed != null && grainBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(grainSeed, grainBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(grainBlock, 7));
			}
			plants.add(grainName);
		}

		for (String treeFruitName : treeFruits) {
			ItemStack treeFruit = GameRegistry.findItemStack(HC, treeFruitName + "Item", 1);
			Block treeFruitBlock = GameRegistry.findBlock(HC, "pam" + (Character.toUpperCase(treeFruitName.charAt(0)) + treeFruitName.substring(1)));
			if (PluginManager.Module.FARMING.isEnabled() && treeFruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(treeFruitBlock, 2));
			}
			if (treeFruit != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{treeFruit}, Fluids.JUICE.getFluid(juiceAmount));
			}
			plants.add(treeFruitName);
		}

		for (String treeName : trees) {
			Block fruitBlock = GameRegistry.findBlock(HC, "pam" + (Character.toUpperCase(treeName.charAt(0)) + treeName.substring(1)));
			if (PluginManager.Module.FARMING.isEnabled() && fruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(fruitBlock, 2));
			}
			plants.add(treeName);
		}

		for (String treeName : treesSpecial) {
			Block fruitBlock = GameRegistry.findBlock(HC, "pam" + (Character.toUpperCase(treeName.charAt(0)) + treeName.substring(1)));
			if (PluginManager.Module.FARMING.isEnabled() && fruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(fruitBlock, 2));
			}
		}

		for (String cropName : genericCrops) {
			ItemStack genericCropSeed = GameRegistry.findItemStack(HC, cropName + "seedItem", 1);
			Block genericCropBlock = GameRegistry.findBlock(HC, "pam" + cropName + "Crop");
			if (genericCropSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{genericCropSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (PluginManager.Module.FARMING.isEnabled() && genericCropSeed != null && genericCropBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(genericCropSeed, genericCropBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(genericCropBlock, 7));
			}
			plants.add(cropName);
		}
		ItemStack mustardCropSeed = GameRegistry.findItemStack(HC, "mustard" + "seedItem", 1);
		Block mustardCropBlock = GameRegistry.findBlock(HC, "pam" + "mustardseeds" + "Crop");
		ItemStack mustardFruit = GameRegistry.findItemStack(HC, "mustard" + "seedsItem", 1);
		if (mustardCropSeed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{mustardCropSeed}, Fluids.SEEDOIL.getFluid(seedamount));
		}
		if (PluginManager.Module.FARMING.isEnabled() && mustardCropSeed != null && mustardCropBlock != null) {
			Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(mustardCropSeed, mustardCropBlock, 7));
			Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(mustardCropBlock, 7));
		}
		if (mustardFruit != null) {
			RecipeUtil.addFermenterRecipes(mustardFruit, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		}

		for (String plantName : plants.build()) {
			ItemStack plant = GameRegistry.findItemStack(HC, plantName + "Item", 1);
			if (plant != null) {
				RecipeUtil.addFermenterRecipes(plant, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			}
		}

		for (String cropnutName : cropNuts) {
			ItemStack cropnut = GameRegistry.findItemStack(HC, cropnutName + "Item", 1);
			ItemStack cropnutSeed = GameRegistry.findItemStack(HC, cropnutName + "seedItem", 1);
			Block cropnutBlock = GameRegistry.findBlock(HC, "pam" + cropnutName + "Crop");
			if (cropnutSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{cropnutSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (PluginManager.Module.FARMING.isEnabled() && cropnutSeed != null && cropnutBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(cropnutSeed, cropnutBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(cropnutBlock, 7));
			}
			if (cropnut != null) {
				RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{cropnut}, Fluids.SEEDOIL.getFluid(12 * seedamount));
			}
		}

		for (String nutName : nuts) {
			ItemStack nut = GameRegistry.findItemStack(HC, nutName + "Item", 1);
			Block nutBlock = GameRegistry.findBlock(HC, "pam" + (Character.toUpperCase(nutName.charAt(0)) + nutName.substring(1)));
			if (PluginManager.Module.FARMING.isEnabled() && nutBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(nutBlock, 2));
			}
			if (nut != null) {
				RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{nut}, Fluids.SEEDOIL.getFluid(15 * seedamount));
			}
		}

		ItemStack hcHoneyItem = GameRegistry.findItemStack(HC, "honeyItem", 1);
		if (hcHoneyItem != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{hcHoneyItem}, Fluids.HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP));
		}

		ItemStack hcBeeswaxItem = GameRegistry.findItemStack(HC, "beeswaxItem", 1);
		if (hcBeeswaxItem != null) {
			RecipeUtil.addRecipe(ForestryAPI.activeMode.getStackSetting("recipe.output.capsule"), "XXX ", 'X', hcBeeswaxItem);
		}
	}

}
