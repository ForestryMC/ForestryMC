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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.apiculture.FlowerManager;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.farming.logic.FarmableBasicFruit;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.farming.logic.FarmableGenericSapling;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginManager;

@Plugin(pluginID = "BiomesOPlenty", name = "BiomesOPlenty", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.biomesoplenty.description")
public class PluginBiomesOPlenty extends ForestryPlugin {

	private static final String BoP = "BiomesOPlenty";

	private static Block saplings;
	private static Block colorizedSaplings;
	private static Item food;
	private static Item misc;
	private static ItemStack persimmon;
	private static int amount;

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(BoP);
	}

	@Override
	public String getFailMessage() {
		return "BiomesOPlenty not found";
	}

	@Override
	public void doInit() {
		super.doInit();

		saplings = GameRegistry.findBlock(BoP, "saplings");
		colorizedSaplings = GameRegistry.findBlock(BoP, "colorizedSaplings");
		food = GameRegistry.findItem(BoP, "food");
		if (food != null) {
			persimmon = new ItemStack(food, 1, 8);
		}

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			addFlowers();
		}
		if (PluginManager.Module.FARMING.isEnabled()) {
			addFarmCrops();
		}
	}

	@Override
	protected void registerRecipes() {
		super.registerRecipes();

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		if (PluginManager.Module.FACTORY.isEnabled()) {
			addFermenterRecipes();
			addSqueezerRecipes();
		}
	}

	private static void addFarmCrops() {
		List<String> saplingItemKeys = new ArrayList<>();

		if (saplings != null) {
			saplingItemKeys.add("saplings");
		}
		if (colorizedSaplings != null) {
			saplingItemKeys.add("colorizedSaplings");
		}

		for (String key : saplingItemKeys) {
			Item saplingItem = GameRegistry.findItem(BoP, key);
			String saplingName = GameData.getItemRegistry().getNameForObject(saplingItem);
			FMLInterModComms.sendMessage(Constants.MOD, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
		}

		if (saplings != null && persimmon != null) {
			Farmables.farmables.get("farmArboreal").add(new FarmableGenericSapling(saplings, 15, persimmon));
		}

		Block boPTurnip = GameRegistry.findBlock(BoP, "turnip");
		if (boPTurnip != null) {
			Item boPTurnipSeeds = GameRegistry.findItem(BoP, "turnipSeeds");
			ItemStack boPTurnipSeedStack = new ItemStack(boPTurnipSeeds, 1, 0);
			if (boPTurnipSeeds != null) {
				Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(boPTurnipSeedStack, boPTurnip, 7));
				if (PluginManager.Module.FACTORY.isEnabled()) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{boPTurnipSeedStack}, Fluids.SEEDOIL.getFluid(amount));
				}
			}

			Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(boPTurnip, 7));
		}
	}

	private static void addFermenterRecipes() {
		int saplingYield = ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling");

		if (saplings != null) {
			RecipeUtil.addFermenterRecipes(new ItemStack(saplings, 1, OreDictionary.WILDCARD_VALUE), saplingYield, Fluids.BIOMASS);
		}

		if (colorizedSaplings != null) {
			RecipeUtil.addFermenterRecipes(new ItemStack(colorizedSaplings, 1, OreDictionary.WILDCARD_VALUE), saplingYield, Fluids.BIOMASS);
		}
	}

	private static void addSqueezerRecipes() {
		ItemStack mulch = new ItemStack(PluginCore.items.mulch);

		misc = GameRegistry.findItem(BoP, "misc");
		if (misc != null) {
			ItemStack pinecone = new ItemStack(misc, 1, 13);
			if (pinecone != null) {
				RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{pinecone}, Fluids.SEEDOIL.getFluid(3* amount));
			}
		}

		if (food != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(food)}, Fluids.JUICE.getFluid(50), mulch, 5);
		}

		if (persimmon != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{persimmon}, Fluids.JUICE.getFluid(200), mulch, 20);
		}
	}

	private static void addFlowers() {
		Block flowers = GameRegistry.findBlock(BoP, "flowers");
		if (flowers != null) {
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 0, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);		//Clover
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 1, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeMushrooms);		//Swampflower
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 2, 1.0, FlowerManager.FlowerTypeNether); 		//Deathbloom
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 3, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow); 	//GlowFlower
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 4, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);		//Blue Hydrangea
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 5, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeJungle);		//Orange Cosmos
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 6, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);		//Pink Daffodil
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 7, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);		//WildFlower
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 8, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);	//Violet
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 9, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);		// White Anemone
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 10, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);	// Waterlily
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 11, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);	//EnderLotus (does not actually spawn in the end)
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 12, 1.0, FlowerManager.FlowerTypeCacti);		//Bromeliad
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 13, 1.0, FlowerManager.FlowerTypeNether);		// EyeBulb
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 14, 1.0, FlowerManager.FlowerTypeNether);		// Unlisted top of the eyebulb
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 15, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);	// Dandelion Puff
		}

		Block flowers2 = GameRegistry.findBlock(BoP, "flowers2");
		if (flowers2 != null) {
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 0, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeJungle);		//Pink Hibiscus
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 1, 1.0, FlowerManager.FlowerTypeVanilla);	//Lily of the Valley
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 2, 1.0, FlowerManager.FlowerTypeNether);		// Burning Blososm
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 3, 1.0, FlowerManager.FlowerTypeVanilla);	// Lavender
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 4, 1.0, FlowerManager.FlowerTypeVanilla);	// Goldenrod
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 5, 1.0, FlowerManager.FlowerTypeVanilla);	//Bluebells
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 6, 1.0, FlowerManager.FlowerTypeVanilla);	//Miner's delight
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 7, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow); //Icy Iris
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 8, 1.0, FlowerManager.FlowerTypeVanilla);	// Rose
		}
		// Toadstool 0 Portobello 1 Blue Milk cap 2 Glowshroom 3 flat mushroom 4 shadow shroom 5       'plants:12' tiny cactus
		Block mushrooms = GameRegistry.findBlock(BoP, "mushrooms");
		if (mushrooms != null) {
			FlowerManager.flowerRegistry.registerPlantableFlower(mushrooms, OreDictionary.WILDCARD_VALUE, 1.0, FlowerManager.FlowerTypeMushrooms);
			FlowerManager.flowerRegistry.registerPlantableFlower(mushrooms, 3, 1.0, FlowerManager.FlowerTypeNether);
		}
		Block plants = GameRegistry.findBlock(BoP, "plants");
		if (plants != null) {
			FlowerManager.flowerRegistry.registerPlantableFlower(plants, 12, 1.0, FlowerManager.FlowerTypeCacti);
		}
	}

	@Override
	protected void registerBackpackItems() {
		// most blocks are covered by the oreDictionary

		final int MINER = 0;
		final int DIGGER = 1;
		final int FORESTER = 2;

		addBlocksToBackpack(MINER,
				"driedDirt",
				"overgrownNetherrack",
				"cragRock",
				"ashStone"
		);

		addBlocksToBackpack(DIGGER,
				"bopGrass",
				"newBopGrass",
				"longGrass",
				"mud",
				"hardDirt",
				"hardSand",
				"originGrass",
				"ash",
				"newBopDirt"
		);

		addBlocksToBackpack(FORESTER,
				"petals"
		);
	}

	private static void addBlocksToBackpack(int backpackId, String... blockNames) {
		for (String blockName : blockNames) {
			Block block = GameRegistry.findBlock(BoP, blockName);
			if (block != null) {
				ItemStack blockStack = new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE);
				BackpackManager.backpackItems[backpackId].add(blockStack);
			} else {
				Log.warning("Missing block: ", blockName);
			}
		}
	}

}
