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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.farming.logic.FarmableBasicFruit;
import forestry.farming.logic.FarmableGenericCrop;

@Plugin(pluginID = "HarvestCraft", name = "HarvestCraft", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.harvestcraft.description")
public class PluginHarvestCraft extends ForestryPlugin {

	private static final String HC = "harvestcraft";

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(HC);
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
				"spiceleaf",
				"mustardseed"
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
				"tea",
				"coffee",
				"candleberry"
		);
		genericCropsBuilder.addAll(herbs);
		genericCropsBuilder.addAll(spices);

		ImmutableList<String> genericCrops = genericCropsBuilder.build();

		ImmutableList.Builder<String> plants = ImmutableList.builder();

		int juiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") / 25;
		int seedamount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
		ItemStack wheatamount = GameMode.getGameMode().getStackSetting("recipe.output.compost.wheat");

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
			if (berrySeed != null && berryBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(berrySeed, berryBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(berryBlock, 7));
			}
			plants.add(berryName);
		}

		juiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
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
			if (fruitSeed != null && fruitBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(fruitSeed, fruitBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(fruitBlock, 7));
			}
			plants.add(fruitName);
		}

		juiceAmount = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") / 2; // vegetables produce less juice
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
			if (vegetableSeed != null && vegetableBlock != null) {
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
				Proxies.common.addRecipe(wheatamount, " X ", "X#X", " X ", '#', Blocks.dirt, 'X', grain);
			}
			if (grainSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{grainSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (grainSeed != null && grainBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(grainSeed, grainBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(grainBlock, 7));
			}
			plants.add(grainName);
		}

		for (String treeFruitName : treeFruits) {
			ItemStack treeFruit = GameRegistry.findItemStack(HC, treeFruitName + "Item", 1);
			Block treeFruitBlock = GameRegistry.findBlock(HC, "pam" + (Character.toUpperCase(treeFruitName.charAt(0)) + treeFruitName.substring(1)));
			if (treeFruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(treeFruitBlock, 2));
			}
			if (treeFruit != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{treeFruit}, Fluids.JUICE.getFluid(juiceAmount));
			}
			plants.add(treeFruitName);
		}

		for (String treeName : trees) {
			Block fruitBlock = GameRegistry.findBlock(HC, "pam" + (Character.toUpperCase(treeName.charAt(0)) + treeName.substring(1)));
			if (fruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(fruitBlock, 2));
			}
			plants.add(treeName);
		}

		for (String treeName : treesSpecial) {
			Block fruitBlock = GameRegistry.findBlock(HC, "pam" + (Character.toUpperCase(treeName.charAt(0)) + treeName.substring(1)));
			if (fruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(fruitBlock, 2));
			}
		}

		for (String cropName : genericCrops) {
			ItemStack genericCropSeed = GameRegistry.findItemStack(HC, cropName + "seedItem", 1);
			Block genericCropBlock = GameRegistry.findBlock(HC, "pam" + cropName + "Crop");
			if (genericCropSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{genericCropSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (genericCropSeed != null && genericCropBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(genericCropSeed, genericCropBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(genericCropBlock, 7));
			}
			plants.add(cropName);
		}

		for (String plantName : plants.build()) {
			ItemStack plant = GameRegistry.findItemStack(HC, plantName + "Item", 1);
			if (plant != null) {
				RecipeUtil.injectLeveledRecipe(plant, GameMode.getGameMode().getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			}
		}

		for (String cropnutName : cropNuts) {
			ItemStack cropnut = GameRegistry.findItemStack(HC, cropnutName + "Item", 1);
			ItemStack cropnutSeed = GameRegistry.findItemStack(HC, cropnutName + "seedItem", 1);
			Block cropnutBlock = GameRegistry.findBlock(HC, "pam" + cropnutName + "Crop");
			if (cropnutSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{cropnutSeed}, Fluids.SEEDOIL.getFluid(seedamount));
			}
			if (cropnutSeed != null && cropnutBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableGenericCrop(cropnutSeed, cropnutBlock, 7));
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(cropnutBlock, 7));
			}
			if (cropnut != null) {
				RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{cropnut}, Fluids.SEEDOIL.getFluid(3 * seedamount));
			}
		}

		for (String nutName : nuts) {
			ItemStack nut = GameRegistry.findItemStack(HC, nutName + "Item", 1);
			Block nutBlock = GameRegistry.findBlock(HC, "pam" + (Character.toUpperCase(nutName.charAt(0)) + nutName.substring(1)));
			if (nutBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(nutBlock, 2));
			}
			if (nut != null) {
				RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{nut}, Fluids.SEEDOIL.getFluid(3 * seedamount));
			}
		}

		ItemStack hcHoneyItem = GameRegistry.findItemStack(HC, "honeyItem", 1);
		if (hcHoneyItem != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{hcHoneyItem}, Fluids.HONEY.getFluid(Defaults.FLUID_PER_HONEY_DROP));
		}

		ItemStack hcBeeswaxItem = GameRegistry.findItemStack(HC, "beeswaxItem", 1);
		if (hcBeeswaxItem != null) {
			Proxies.common.addRecipe(GameMode.getGameMode().getStackSetting("recipe.output.capsule"), "XXX ", 'X', hcBeeswaxItem);
		}
	}

}
