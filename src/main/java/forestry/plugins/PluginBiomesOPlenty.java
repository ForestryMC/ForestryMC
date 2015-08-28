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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.apiculture.FlowerManager;
import forestry.api.farming.Farmables;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.farming.logic.FarmableBasicFruit;
import forestry.farming.logic.FarmableGenericCrop;
import forestry.farming.logic.FarmableGenericSapling;

@Plugin(pluginID = "BiomesOPlenty", name = "BiomesOPlenty", author = "Nirek", url = Defaults.URL, unlocalizedDescription = "for.plugin.biomesoplenty.description")
public class PluginBiomesOPlenty extends ForestryPlugin {

	private static final String BoP = "BiomesOPlenty";

	private static Block saplings;
	private static Block colorizedSaplings;
	private static Item food;
	private static ItemStack persimmon;

	@Override
	public boolean isAvailable() {
		return Proxies.common.isModLoaded(BoP);
	}

	@Override
	public String getFailMessage() {
		return "BiomesOPlenty not found";
	}

	@Override
	protected void preInit() {
		super.preInit();
		addFlowers();
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

		addFarmCrops();
		addFermenterRecipes();
		addSqueezerRecipes();
		addBlocksToBackpack();
	}

	private static void addFarmCrops() {
		List<String> saplingItemKeys = new ArrayList<String>();

		if (saplings != null) {
			saplingItemKeys.add("saplings");
		}
		if (colorizedSaplings != null) {
			saplingItemKeys.add("colorizedSaplings");
		}

		for (String key : saplingItemKeys) {
			Item saplingItem = GameRegistry.findItem(BoP, key);
			String saplingName = GameData.getItemRegistry().getNameForObject(saplingItem).toString();
			FMLInterModComms.sendMessage(Defaults.MOD, "add-farmable-sapling", String.format("farmArboreal@%s.-1", saplingName));
		}

		if (saplings != null && persimmon != null) {
			Farmables.farmables.get("farmArboreal").add(new FarmableGenericSapling(saplings, 15, persimmon));
		}

		Block boPTurnip = GameRegistry.findBlock(BoP, "turnip");
		if (boPTurnip != null) {
			Item boPTurnipSeeds = GameRegistry.findItem(BoP, "turnipSeeds");
			if (boPTurnipSeeds != null) {
				Farmables.farmables.get("farmVegetables").add(new FarmableGenericCrop(new ItemStack(boPTurnipSeeds, 1, 0), boPTurnip, 7));
			}

			Farmables.farmables.get("farmOrchard").add(new FarmableBasicFruit(boPTurnip, 7));
		}
	}

	private static void addFermenterRecipes() {
		int saplingYield = GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling");

		if (saplings != null) {
			RecipeUtil.injectLeveledRecipe(new ItemStack(saplings, 1, OreDictionary.WILDCARD_VALUE), saplingYield, Fluids.BIOMASS);
		}

		if (colorizedSaplings != null) {
			RecipeUtil.injectLeveledRecipe(new ItemStack(colorizedSaplings, 1, OreDictionary.WILDCARD_VALUE), saplingYield, Fluids.BIOMASS);
		}
	}

	private static void addSqueezerRecipes() {
		if (food != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(food)}, Fluids.JUICE.getFluid(50), ForestryItem.mulch.getItemStack(), 5);
		}

		if (persimmon != null) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{persimmon}, Fluids.JUICE.getFluid(200), ForestryItem.mulch.getItemStack(), 20);
		}
	}

	private static void addFlowers() {
		Block flowers = GameRegistry.findBlock(BoP, "flowers");
		if (flowers != null) {
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers, OreDictionary.WILDCARD_VALUE, 1.0, FlowerManager.FlowerTypeVanilla);
		}

		Block flowers2 = GameRegistry.findBlock(BoP, "flowers2");
		if (flowers2 != null) {
			FlowerManager.flowerRegistry.registerPlantableFlower(flowers2, OreDictionary.WILDCARD_VALUE, 1.0, FlowerManager.FlowerTypeVanilla);
		}
	}

	private static void addBlocksToBackpack() {
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
			addBlockToBackpack(backpackId, blockName, OreDictionary.WILDCARD_VALUE);
		}
	}

	private static void addBlockToBackpack(int backpackId, String blockName, int meta) {
		Block block = GameRegistry.findBlock(BoP, blockName);
		if (block != null) {
			BackpackManager.backpackItems[backpackId].add(new ItemStack(block, 1, meta));
		} else {
			Proxies.log.warning("Missing block: ", blockName);
		}
	}

}
