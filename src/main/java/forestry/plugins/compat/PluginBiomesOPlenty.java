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

import forestry.api.farming.Farmables;
import forestry.farming.logic.FarmableSapling;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.core.ForestryAPI;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.BIOMES_O_PLENTY, name = "BiomesOPlenty", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.plugin.biomesoplenty.description")
public class PluginBiomesOPlenty extends BlankForestryPlugin {

	private static final String BoP = "BiomesOPlenty";

	private static Block saplings;
	private static Block colorizedSaplings;
	private static Item food;
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

		saplings = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BoP, "saplings"));
		colorizedSaplings = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BoP, "colorizedSaplings"));
		food = ForgeRegistries.ITEMS.getValue(new ResourceLocation(BoP, "food"));

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			addFlowers();
		}
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			addFarmCrops();
		}
	}

	@Override
	public void registerRecipes() {
		super.registerRecipes();

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {
			addFermenterRecipes();
			addSqueezerRecipes();
		}
	}

	private static void addFarmCrops() {
		List<String> saplingItemKeys = new ArrayList<>();
		saplingItemKeys.add("sapling_0");
		saplingItemKeys.add("sapling_1");
		saplingItemKeys.add("sapling_2");

		for (String key : saplingItemKeys) {
			Item saplingItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(BoP, key));
			if (saplingItem == null) continue;
			Farmables.farmables.put("farmArboreal", new FarmableSapling(
					new ItemStack(saplingItem),
					new ItemStack[] {}
			));
		}

		Block boPTurnip = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BoP, "turnip"));
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING) && boPTurnip != null) {
			Item boPTurnipSeeds = ForgeRegistries.ITEMS.getValue(new ResourceLocation(BoP, "turnipSeeds"));
			ItemStack boPTurnipSeedStack = new ItemStack(boPTurnipSeeds, 1, 0);
			if (boPTurnipSeeds != null) {
				//TODO BoP for 1.9: Add farmable saplings
				//				Farmables.farmables.get("farmVegetables").add(new FarmableHandPlanted(boPTurnipSeedStack, boPTurnip, 7));
				if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{boPTurnipSeedStack}, Fluids.SEED_OIL.getFluid(amount));
				}
			}

			//TODO BoP for 1.9: Add farmables
			//			Farmables.farmables.get("farmOrchard").add(new FarmableBase(boPTurnip, 7));
		}
	}

	private static void addFermenterRecipes() {
		int saplingYield = ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling");

		if (saplings != Blocks.AIR) {
			RecipeUtil.addFermenterRecipes(new ItemStack(saplings, 1, OreDictionary.WILDCARD_VALUE), saplingYield, Fluids.BIOMASS);
		}

		if (colorizedSaplings != Blocks.AIR) {
			RecipeUtil.addFermenterRecipes(new ItemStack(colorizedSaplings, 1, OreDictionary.WILDCARD_VALUE), saplingYield, Fluids.BIOMASS);
		}
	}

	private static void addSqueezerRecipes() {
		ItemStack mulch = new ItemStack(PluginCore.items.mulch);

		Item pinecone = ForgeRegistries.ITEMS.getValue(new ResourceLocation(BoP, "pinecone"));
		if (pinecone != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(pinecone)}, Fluids.SEED_OIL.getFluid(3 * amount));
		}

		if (food != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(food)}, Fluids.JUICE.getFluid(50), mulch, 5);
		}

		Item persimmon = ForgeRegistries.ITEMS.getValue(new ResourceLocation(BoP, "persimmon"));
		if (persimmon != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(persimmon)}, Fluids.JUICE.getFluid(200), mulch, 20);
		}
	}

	private static void addFlowers() {
//		Block flowers = Block.REGISTRY.getObject(new ResourceLocation(BoP, "flowers"));
//		if (flowers != null && flowers != Blocks.AIR) {
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 0, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //Clover
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 1, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeMushrooms);        //Swampflower
//			//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 2, 1.0, FlowerManager.FlowerTypeNether); 		//Deathbloom
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 3, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //GlowFlower
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 4, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //Blue Hydrangea
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 5, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeJungle);        //Orange Cosmos
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 6, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //Pink Daffodil
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 7, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //WildFlower
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 8, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //Violet
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 9, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        // White Anemone
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 10, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    // Waterlily
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 11, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //EnderLotus (does not actually spawn in the end)
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 12, 1.0, FlowerManager.FlowerTypeCacti);        //Bromeliad
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 13, 1.0, FlowerManager.FlowerTypeNether);        // EyeBulb
//			//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 14, 1.0, FlowerManager.FlowerTypeNether);		// Unlisted top of the eyebulb
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, 15, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    // Dandelion Puff
//		}
//
//		Block flowers2 = Block.REGISTRY.getObject(new ResourceLocation(BoP, "flowers2"));
//		if (flowers2 != null && flowers2 != Blocks.AIR) {
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 0, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeJungle);        //Pink Hibiscus
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 1, 1.0, FlowerManager.FlowerTypeVanilla);    //Lily of the Valley
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 2, 1.0, FlowerManager.FlowerTypeNether);        // Burning Blososm
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 3, 1.0, FlowerManager.FlowerTypeVanilla);    // Lavender
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 4, 1.0, FlowerManager.FlowerTypeVanilla);    // Goldenrod
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 5, 1.0, FlowerManager.FlowerTypeVanilla);    //Bluebells
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 6, 1.0, FlowerManager.FlowerTypeVanilla);    //Miner's delight
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 7, 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow); //Icy Iris
//			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, 8, 1.0, FlowerManager.FlowerTypeVanilla);    // Rose
//		}
//		// Toadstool 0 Portobello 1 Blue Milk cap 2 Glowshroom 3 flat mushroom 4 shadow shroom 5       'plants:12' tiny cactus
//		Block mushrooms = Block.REGISTRY.getObject(new ResourceLocation(BoP, "mushrooms"));
//		if (mushrooms != null && mushrooms != Blocks.AIR) {
//			FlowerManager.flowerRegistry.registerPlantableFlower(mushrooms, OreDictionary.WILDCARD_VALUE, 1.0, FlowerManager.FlowerTypeMushrooms);
//			FlowerManager.flowerRegistry.registerPlantableFlower(mushrooms, 3, 1.0, FlowerManager.FlowerTypeNether);
//		}
//		Block plants = Block.REGISTRY.getObject(new ResourceLocation(BoP, "plants"));
//		if (plants != null && plants != Blocks.AIR) {
//			FlowerManager.flowerRegistry.registerPlantableFlower(plants, 12, 1.0, FlowerManager.FlowerTypeCacti);
//		}
	}

	@Override
	public void registerBackpackItems() {
		// most blocks are covered by the oreDictionary

		addBlocksToBackpack(BackpackManager.MINER_UID,
				"driedDirt",
				"overgrownNetherrack",
				"cragRock",
				"ashStone"
		);

		addBlocksToBackpack(BackpackManager.DIGGER_UID,
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

		addBlocksToBackpack(BackpackManager.FORESTER_UID,
				"petals"
		);
	}

	private static void addBlocksToBackpack(String backpackUid, String... blockNames) {
		for (String blockName : blockNames) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(BoP, blockName));
			if (block != null) {
				Item item = Item.getItemFromBlock(block);
				if (item != null) {
					ItemStack blockStack = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
					BackpackManager.backpackInterface.addItemToForestryBackpack(backpackUid, blockStack);
				} else {
					Log.warning("Could not find an item for block: {}", blockName);
				}
			} else {
				Log.warning("Missing block: {}", blockName);
			}
		}
	}

}
