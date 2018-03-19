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

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.apiculture.FlowerManager;
import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.Log;
import forestry.farming.FarmRegistry;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.farming.logic.farmables.FarmableBush;
import forestry.farming.logic.farmables.FarmableSapling;
import forestry.modules.ForestryModuleUids;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.BIOMES_O_PLENTY, name = "BiomesOPlenty", author = "Nirek", url = Constants.URL, unlocalizedDescription = "for.module.biomesoplenty.description")
public class PluginBiomesOPlenty extends CompatPlugin {

	public static final String MOD_ID = "biomesoplenty";

	private static final ArrayList<ItemStack> logs = new ArrayList<>();
	private static final ArrayList<ItemStack> saplings = new ArrayList<>();
	private static final ArrayList<ItemStack> dirts = new ArrayList<>();
	private static final ArrayList<ItemStack> ores = new ArrayList<>();
	private static final ArrayList<ItemStack> gems = new ArrayList<>();

	@GameRegistry.ItemStackHolder("biomesoplenty:pinecone")
	public static final ItemStack PINECONE = null;
	@GameRegistry.ItemStackHolder("biomesoplenty:berries")
	public static final ItemStack BERRIES = null;
	@GameRegistry.ItemStackHolder("biomesoplenty:pear")
	public static final ItemStack PEAR = null;
	@GameRegistry.ItemStackHolder("biomesoplenty:peach")
	public static final ItemStack PEACH = null;
	@GameRegistry.ItemStackHolder("biomesoplenty:persimmon")
	public static final ItemStack PERSIMMON = null;

	private static final IForgeRegistry<Item> REGISTRY = ForgeRegistries.ITEMS;

	public PluginBiomesOPlenty() {
		super("BiomesOPlenty", "biomesoplenty");
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	/* Must be run this early or crates will not be registered correctly*/
	public void registerItems(RegistryEvent<Item> itemRegistryEvent) {
		REGISTRY.forEach(item -> {
			final ResourceLocation registryName = item.getRegistryName();
			if (registryName == null || !registryName.getResourceDomain().equals(MOD_ID)) {
				return;
			}

			final String itemName = registryName.getResourcePath();

			if (itemName.matches("^sapling_\\d$")) {
				consumeSubItems(item, "saplings", saplings);

				RecipeUtil.addFermenterRecipes(
						new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE),
						ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"),
						Fluids.BIOMASS
				);
				return;
			}

			if (itemName.matches("^log_\\d$")) {
				consumeSubItems(item, "logs", logs);
				return;
			}

			if (itemName.matches("grass|dirt|white_sand|dried_sand|ash_block|mud")) {
				consumeSubItems(item, "dirts", dirts);
				return;
			}

			if (itemName.matches("crystal|biome_ore|gem_ore")) {
				consumeSubItems(item, "ores", ores);
				return;
			}

			if (itemName.matches("gem")) {
				consumeSubItems(item, "gems", gems);
				return;
			}
		});

		registerFarmableSaplings(saplings);

		if (ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.APICULTURE))) {
			addFlowers();
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		Iterables.concat(
				dirts, logs, saplings, gems
		).forEach(crateRegistry::registerCrate);
	}

	@Override
	public void registerRecipes() {
		int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		ItemStack seed = getItemStack("turnip_seeds");
		RecipeManagers.squeezerManager.addRecipe(10, NonNullList.from(seed, seed), Fluids.SEED_OIL.getFluid(amount));
		RecipeManagers.moistenerManager.addRecipe(seed, new ItemStack(Blocks.MYCELIUM), 5000);
		RecipeManagers.squeezerManager.addRecipe(10, NonNullList.from(PINECONE, PINECONE), Fluids.SEED_OIL.getFluid(amount));
		if (ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING))) {
			Block block = getBlock("turnip_block");

			FarmRegistry.getInstance().registerFarmables("farmCrops",
					new FarmableAgingCrop(
							seed,
							block,
							((BlockCrops) block).AGE,
							7
					)
			);

			Block berryBush = Block.getBlockFromItem(getItem("plant_0"));
			IBlockState berryState = Block.getBlockFromItem(getItem("plant_0")).getStateFromMeta(5);
			IBlockState bushState = Block.getBlockFromItem(getItem("plant_0")).getStateFromMeta(2);
			FarmRegistry.getInstance().registerFarmables("farmOrchard", new FarmableBush(bushState, berryState));
		}

		ItemRegistryCore coreItems = ModuleCore.getItems();

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 2;
		final int juiceAmount = Math.max(amount, 1); // Produce at least 1 mb of juice.
		ItemStack mulch = coreItems.mulch.getItemStack();
		for (ItemStack fruit : new ItemStack[]{PEAR, PEACH, PERSIMMON}) {
			RecipeManagers.squeezerManager.addRecipe(
					10, NonNullList.from(fruit, fruit),
					Fluids.JUICE.getFluid(juiceAmount), mulch,
					ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple"));
		}

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 25;
		amount = Math.max(amount, 1); // Produce at least 1 mb of juice.
		RecipeManagers.squeezerManager.addRecipe(3, NonNullList.from(BERRIES, BERRIES), Fluids.JUICE.getFluid(amount));

		RecipeUtil.addFermenterRecipes(getItemStack("turnip"), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);

	}

	private static void addFlowers() {
		Block flower_0 = Block.REGISTRY.getObject(new ResourceLocation(MOD_ID, "flower_0"));
		if (flower_0 != null && flower_0 != Blocks.AIR) {
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(0), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //Clover
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(1), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeMushrooms);        //Swampflower
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(2), 1.0, FlowerManager.FlowerTypeNether);        //Deathbloom
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(3), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //GlowFlower
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(4), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //Blue Hydrangea
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(5), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow, FlowerManager.FlowerTypeJungle);        //Orange Cosmos
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(6), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //Pink Daffodil
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(7), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        //WildFlower
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(8), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //Violet
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(9), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);        // White Anemone
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(10), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow);    //EnderLotus (does not actually spawn in the end)
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(11), 1.0, FlowerManager.FlowerTypeCacti);        //Bromeliad
			//					FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(12), 1.0, FlowerManager.FlowerTypeVanilla);        // Wilted Lily
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(13), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeJungle);        //Pink Hibiscus
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(14), 1.0, FlowerManager.FlowerTypeVanilla);    //Lily of the Valley
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_0.getStateFromMeta(15), 1.0, FlowerManager.FlowerTypeNether);        // Burning Blososm
		}

		Block flower_1 = Block.REGISTRY.getObject(new ResourceLocation(MOD_ID, "flower_1"));
		if (flower_1 != null && flower_1 != Blocks.AIR) {
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_1.getStateFromMeta(0), 1.0, FlowerManager.FlowerTypeVanilla);    // Lavender
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_1.getStateFromMeta(1), 1.0, FlowerManager.FlowerTypeVanilla);    // Goldenrod
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_1.getStateFromMeta(2), 1.0, FlowerManager.FlowerTypeVanilla);    //Bluebells
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_1.getStateFromMeta(3), 1.0, FlowerManager.FlowerTypeVanilla);    //Miner's delight
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_1.getStateFromMeta(4), 1.0, FlowerManager.FlowerTypeVanilla, FlowerManager.FlowerTypeSnow); //Icy Iris
			FlowerManager.flowerRegistry.registerPlantableFlower(flower_1.getStateFromMeta(5), 1.0, FlowerManager.FlowerTypeVanilla);    // Rose
		}
		// Toadstool 0 Portobello 1 Blue Milk cap 2 Glowshroom 3 flat mushroom 4 shadow shroom 5       'plants:12' tiny cactus
		Block mushroom = Block.REGISTRY.getObject(new ResourceLocation(MOD_ID, "mushroom"));
		if (mushroom != null && mushroom != Blocks.AIR) {
			for (int i = 0; i < 6; i++) {
				if (i == 3) {
					continue;
				}
				FlowerManager.flowerRegistry.registerPlantableFlower(mushroom.getStateFromMeta(i), 1.0, FlowerManager.FlowerTypeMushrooms);
			}
			FlowerManager.flowerRegistry.registerPlantableFlower(mushroom.getStateFromMeta(3), 1.0, FlowerManager.FlowerTypeNether);
		}
		Block plants = Block.REGISTRY.getObject(new ResourceLocation(MOD_ID, "plants"));
		if (plants != null && plants != Blocks.AIR) {
			FlowerManager.flowerRegistry.registerPlantableFlower(plants.getStateFromMeta(12), 1.0, FlowerManager.FlowerTypeCacti);
		}
	}

	private void consumeSubItems(Item item, String groupName, Collection<ItemStack> consumer) {
		complexConsumeSubItems(item, groupName, consumer::add);
	}

	private void complexConsumeSubItems(Item item, String groupName, Consumer<ItemStack> consumer) {
		final NonNullList<ItemStack> subItems = NonNullList.create();
		item.getSubItems(CreativeTabs.SEARCH, subItems);

		// Fallback if item is not returned in sub items
		if (subItems.isEmpty()) {
			final ItemStack itemStack = new ItemStack(item, 1);
			final String subItemName = itemStack.getUnlocalizedName();
			Log.info("[PluginBiomesOPleany] Adding '{}' to list of {}", subItemName, groupName);
			consumer.accept(itemStack);
			return;
		}

		subItems.forEach(itemStack -> {
			final String subItemName = itemStack.getUnlocalizedName();
			Log.info("[PluginBiomesOPlenty] Adding '{}' to list of {}", subItemName, groupName);
			consumer.accept(itemStack);
		});
	}

	private void registerFarmableSaplings(List<ItemStack> saplings) {
		for (ItemStack sapling : saplings) {
			String sapling_name = sapling.getUnlocalizedName().split("\\.")[2];
			switch (sapling_name) {
				case "yellow_autumn_sapling":
				case "orange_autumn_sapling":
				case "dead_sapling":
				case "maple_sapling":
					ForestryAPI.farmRegistry.registerFarmables("farmArboreal", new FarmableSapling(sapling, PERSIMMON));
					break;
				case "fir_sapling":
				case "pine_sapling":
					ForestryAPI.farmRegistry.registerFarmables("farmArboreal", new FarmableSapling(sapling, PINECONE));
					break;
				case "willow_sapling":
					ForestryAPI.farmRegistry.registerFarmables("farmArboreal", new FarmableSapling(sapling, PEAR));
					break;
				case "mahogany_sapling":
					ForestryAPI.farmRegistry.registerFarmables("farmArboreal", new FarmableSapling(sapling, PEACH));
					break;
				case "origin_sapling":
					ForestryAPI.farmRegistry.registerFarmables("farmArboreal", new FarmableSapling(sapling, Items.APPLE));
					break;
				case "redwood_sapling":
				case "sacred_oak_sapling":        //too big to safely farm
					break;
				default:
					ForestryAPI.farmRegistry.registerFarmables("farmArboreal", new FarmableSapling(sapling, new ItemStack[0]));
					break;
			}

		}

	}
}
