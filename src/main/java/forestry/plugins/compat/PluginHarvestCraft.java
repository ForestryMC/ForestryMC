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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.ForgeRegistries;


import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.RecipeManagers;
import forestry.core.PluginFluids;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableAgingCrop;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

import static forestry.core.PluginCore.items;

@ForestryPlugin(pluginID = ForestryPluginUids.HARVESTCRAFT, name = "HarvestCraft", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.harvestcraft.description")
public class PluginHarvestCraft extends BlankForestryPlugin {

	private static final String HC = "harvestcraft";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(HC);
	}

	@Override
	public String getFailMessage() {
		return "HarvestCraft not found";
	}
	
	@Nullable
	private static ItemStack getItemStack(@Nonnull String itemName) {
		ResourceLocation key = new ResourceLocation(HC, itemName);
		if (ForgeRegistries.ITEMS.containsKey(key)) {
			return new ItemStack(ForgeRegistries.ITEMS.getValue(key),1);
		} else {
			return null;
		}
	}
	private static Block getBlock(@Nonnull String blockName) {
		ResourceLocation key = new ResourceLocation(HC, blockName);
		if (ForgeRegistries.BLOCKS.containsKey(key)) {
			return ForgeRegistries.BLOCKS.getValue(key);
		} else {
			return null;
		}
	}
		@Override
	public void registerRecipes() {

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
		int wheatamount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
		PropertyInteger plantAGE = PropertyInteger.create("age", 0, 3);
		PropertyInteger fruitAGE = PropertyInteger.create("age", 0, 2);
			
		juiceAmount = Math.max(juiceAmount, 1); // Produce at least 1 mb of juice.
		for (String berryName : berries) {
			ItemStack berry = getItemStack(berryName + "Item");
			ItemStack berrySeed = getItemStack( berryName + "seedItem");
			Block berryBlock = getBlock("pam" + berryName + "Crop");
			if (berry != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{berry}, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (berrySeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{berrySeed}, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && berrySeed != null && berryBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableAgingCrop(berrySeed, berryBlock, plantAGE, 3));
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(berrySeed, berryBlock, plantAGE, 3, 0));
			}
			plants.add(berryName);
		}

		juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		for (String fruitName : fruits) {
			ItemStack fruit = getItemStack(fruitName + "Item");
			ItemStack fruitSeed = getItemStack(fruitName + "seedItem");
			Block fruitBlock = getBlock("pam" + fruitName + "Crop");
			if (fruit != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{fruit}, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (fruitSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{fruitSeed}, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && fruitSeed != null && fruitBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableAgingCrop(fruitSeed, fruitBlock,plantAGE, 3));
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(fruitSeed,fruitBlock,plantAGE, 3, 0));
			}
			plants.add(fruitName);
		}

		juiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 2; // vegetables produce less juice
		juiceAmount = Math.max(juiceAmount, 1); // Produce at least 1 mb of juice.
		for (String vegetableName : vegetables) {
			ItemStack vegetable = getItemStack(vegetableName + "Item");
			ItemStack vegetableSeed = getItemStack(vegetableName + "seedItem");
			Block vegetableBlock = getBlock("pam" + vegetableName + "Crop");
			if (vegetable != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{vegetable}, Fluids.JUICE.getFluid(juiceAmount));
			}
			if (vegetableSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{vegetableSeed}, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && vegetableSeed != null && vegetableBlock != null) {
				Farmables.farmables.get("farmVegetables").add(new FarmableAgingCrop(vegetableSeed, vegetableBlock,plantAGE, 3));
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(vegetableSeed, vegetableBlock,plantAGE, 3, 0));
			}
			plants.add(vegetableName);
		}

		for (String grainName : grains) {
			ItemStack grain = getItemStack(grainName + "Item");
			ItemStack grainSeed = getItemStack(grainName + "seedItem");
			Block grainBlock = getBlock("pam" + grainName + "Crop");
			if (grain != null && wheatamount > 0) {
				RecipeUtil.addRecipe(items.fertilizerBio.getItemStack(wheatamount), " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', grain);
				FuelManager.moistenerResource.put(grain, new MoistenerFuel(grain, items.mouldyWheat.getItemStack(), 0, 300));
			}
			if (grainSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{grainSeed}, Fluids.SEED_OIL.getFluid(seedamount));
				RecipeManagers.moistenerManager.addRecipe(grainSeed, new ItemStack(Blocks.MYCELIUM), 5000);
			}
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && grainSeed != null && grainBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableAgingCrop(grainSeed, grainBlock,plantAGE, 3));
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(grainSeed,grainBlock,plantAGE, 3, 0));
			}
			plants.add(grainName);
		}

		for (String treeFruitName : treeFruits) {
			ItemStack treeFruit = getItemStack(treeFruitName + "Item");
			Block treeFruitBlock = getBlock("pam" + (Character.toUpperCase(treeFruitName.charAt(0)) + treeFruitName.substring(1)));
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && treeFruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(null, treeFruitBlock, fruitAGE, 2, 0));
			}
			if (treeFruit != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{treeFruit}, Fluids.JUICE.getFluid(juiceAmount));
			}
			plants.add(treeFruitName);
		}

		for (String treeName : trees) {
			Block fruitBlock = getBlock("pam" + (Character.toUpperCase(treeName.charAt(0)) + treeName.substring(1)));
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && fruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(null, fruitBlock, fruitAGE, 2, 0));
			}
			plants.add(treeName);
		}

		for (String treeName : treesSpecial) {
			Block fruitBlock = getBlock("pam" + (Character.toUpperCase(treeName.charAt(0)) + treeName.substring(1)));
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && fruitBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(null, fruitBlock, fruitAGE, 2, 0));
			}
		}

		for (String cropName : genericCrops) {
			ItemStack genericCropSeed = getItemStack(cropName + "seedItem");
			Block genericCropBlock = getBlock("pam" + cropName + "Crop");
			if (genericCropSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{genericCropSeed}, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && genericCropSeed != null && genericCropBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableAgingCrop(genericCropSeed, genericCropBlock, plantAGE, 3));
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(genericCropSeed, genericCropBlock, plantAGE, 3, 0));
			}
			plants.add(cropName);
		}
		ItemStack mustardCropSeed = getItemStack("mustard" + "seedItem");
		Block mustardCropBlock = getBlock("pam" + "mustardseeds" + "Crop");
		ItemStack mustardFruit = getItemStack("mustard" + "seedsItem");
		if (mustardCropSeed != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{mustardCropSeed}, Fluids.SEED_OIL.getFluid(seedamount));
		}
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && mustardCropSeed != null && mustardCropBlock != null) {
			Farmables.farmables.get("farmWheat").add(new FarmableAgingCrop(mustardCropSeed, mustardCropBlock, plantAGE, 3));
			Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(mustardCropSeed,mustardCropBlock, plantAGE, 3, 0));
		}
		if (mustardFruit != null) {
			RecipeUtil.addFermenterRecipes(mustardFruit, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		}

		for (String plantName : plants.build()) {
			ItemStack plant = getItemStack(plantName + "Item");
			if (plant != null) {
				RecipeUtil.addFermenterRecipes(plant, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			}
		}

		for (String cropnutName : cropNuts) {
			ItemStack cropnut = getItemStack(cropnutName + "Item");
			ItemStack cropnutSeed = getItemStack(cropnutName + "seedItem");
			Block cropnutBlock = getBlock("pam" + cropnutName + "Crop");
			if (cropnutSeed != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{cropnutSeed}, Fluids.SEED_OIL.getFluid(seedamount));
			}
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && cropnutSeed != null && cropnutBlock != null) {
				Farmables.farmables.get("farmWheat").add(new FarmableAgingCrop(cropnutSeed, cropnutBlock, plantAGE, 3));
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(cropnutSeed,cropnutBlock, plantAGE, 3, 0));
			}
			if (cropnut != null) {
				RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{cropnut}, Fluids.SEED_OIL.getFluid(12 * seedamount));
			}
		}

		for (String nutName : nuts) {
			ItemStack nut = getItemStack(nutName + "Item");
			Block nutBlock = getBlock("pam" + (Character.toUpperCase(nutName.charAt(0)) + nutName.substring(1)));
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)&& nutBlock != null) {
				Farmables.farmables.get("farmOrchard").add(new FarmableAgingCrop(null, nutBlock, fruitAGE, 2, 0));
			}
			if (nut != null) {
				RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{nut}, Fluids.SEED_OIL.getFluid(15 * seedamount));
			}
		}

		ItemStack hcHoneyItem = getItemStack("honeyItem");
		if (hcHoneyItem != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{hcHoneyItem}, Fluids.FOR_HONEY.getFluid(Constants.FLUID_PER_HONEY_DROP));
		}

		ItemStack hcBeeswaxItem = getItemStack("beeswaxItem");
		if (hcBeeswaxItem != null) {
			RecipeUtil.addRecipe(PluginFluids.items.waxCapsuleEmpty.getItemStack(ForestryAPI.activeMode.getIntegerSetting("recipe.output.capsule")), "XXX ", 'X', hcBeeswaxItem);
		}
	}

}
