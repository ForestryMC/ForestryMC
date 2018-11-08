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
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmRegistry;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.farming.FarmRegistry;
import forestry.farming.logic.ForestryFarmIdentifier;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.farming.logic.farmables.FarmableSapling;
import forestry.farming.logic.farmables.FarmableVanillaMushroom;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.NATURA, name = "Natura", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.natura.description")
public class PluginNatura extends BlankForestryModule {

	public static final String MOD_ID = "natura";

	@ObjectHolder("natura:nether_green_large_glowshroom")
	private static final Block greenLargeGlowshroomBlock = null;
	@ObjectHolder("natura:nether_blue_large_glowshroom")
	private static final Block blueLargeGlowshroomBlock = null;
	@ObjectHolder("natura:nether_purple_large_glowshroom")
	private static final Block purpleLargeGlowshroomBlock = null;

	private static final ArrayList<ItemStack> fruits = new ArrayList<>();
	private static final ArrayList<ItemStack> soups = new ArrayList<>();
	private static final ArrayList<ItemStack> berries = new ArrayList<>();
	private static final ArrayList<ItemStack> edibles = new ArrayList<>();
	private static final ArrayList<ItemStack> seeds = new ArrayList<>();
	private static final ArrayList<ItemStack> logs = new ArrayList<>();
	private static final ArrayList<ItemStack> saplings = new ArrayList<>();
	private static final ArrayList<ItemStack> shrooms = new ArrayList<>();
	private static final ArrayList<ItemStack> materials = new ArrayList<>();
	private static final ArrayList<ItemStack> crops = new ArrayList<>();
	private static final ArrayList<ItemStack> cropBlocks = new ArrayList<>();

	public PluginNatura() {
		super();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(MOD_ID);
	}

	@Override
	public String getFailMessage() {
		return "Natura not found";
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent<Item> itemRegistryEvent) {
		final ItemStack potashApple = findPotashAppleInRegistry();

		boolean hasMushroom = blueLargeGlowshroomBlock != null && greenLargeGlowshroomBlock != null && purpleLargeGlowshroomBlock != null;
		ForgeRegistries.ITEMS.forEach(item -> {
			final ResourceLocation registryName = item.getRegistryName();
			if (registryName == null || !registryName.getNamespace().equals(MOD_ID)) {
				return;
			}

			final String itemName = registryName.getPath();

			if (itemName.matches("^.*_sapling\\d?$")) {
				consumeSubItems(item, "saplings", saplings);

				RecipeUtil.addFermenterRecipes(
					new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE),
					ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"),
					Fluids.BIOMASS
				);

				ItemStack[] windfall = new ItemStack[]{};

				// add potash apple as windfall for darkwood (nether_sapling:2)
				if (itemName.endsWith("nether_sapling")) {
					windfall = new ItemStack[]{potashApple};
				}

				FarmRegistry.getInstance().registerFarmables(ForestryFarmIdentifier.ARBOREAL, new FarmableSapling(
					new ItemStack(item),
					windfall
				));
				return;
			}

			if (hasMushroom && itemName.matches("^.*nether_glowshroom\\d?$")) {

				complexConsumeSubItems(item, "shrooms", subItem -> {

					shrooms.add(subItem);

					// find large shroom block that matches subItem
					final Block largeShroomBlock;
					if (subItem.getMetadata() == blueLargeGlowshroomBlock.damageDropped(blueLargeGlowshroomBlock.getDefaultState())) {
						largeShroomBlock = blueLargeGlowshroomBlock;
					} else if (subItem.getMetadata() == greenLargeGlowshroomBlock.damageDropped(greenLargeGlowshroomBlock.getDefaultState())) {
						largeShroomBlock = greenLargeGlowshroomBlock;
					} else {
						largeShroomBlock = purpleLargeGlowshroomBlock;
					}

					// block representing planted glowshroom
					final Block smallShroomBlock = Block.getBlockFromItem(subItem.getItem());

					FarmRegistry.getInstance().registerFarmables(ForestryFarmIdentifier.SHROOM, new FarmableVanillaMushroom(
						subItem,
						smallShroomBlock.getStateFromMeta(subItem.getMetadata()),
						largeShroomBlock
					));

				});

				return;
			}

			if (itemName.matches("^edibles|.*fruit_item|soups$")) {
				complexConsumeSubItems(item, "edibles", subitem -> {
					final String subItemName = subitem.getTranslationKey();
					if (subItemName.matches("^.*berry$")) {
						berries.add(subitem);
						return;
					}

					if (subItemName.matches("^.*(fruit_item|apple)$")) {
						fruits.add(subitem);
						return;
					}

					if (subItemName.matches("^.*stew$")) {
						soups.add(subitem);
						return;
					}

					edibles.add(subitem);
				});
			}

			if (itemName.matches("^.*_seeds$")) {
				consumeSubItems(item, "seeds", seeds);
				return;
			}

			if (itemName.matches("^.*_logs\\d?$")) {
				consumeSubItems(item, "logs", logs);
				return;
			}

			if (itemName.matches("^.*_crop\\d?$")) {
				consumeSubItems(item, "cropBlocks", cropBlocks);
				return;
			}

			if (itemName.matches("^materials$")) {
				complexConsumeSubItems(item, "materials", subitem -> {
					final String subItemName = subitem.getTranslationKey();

					if (subItemName.matches("^.*(barley|cotton)$")) {
						crops.add(subitem);
						return;
					}
					materials.add(subitem);
				});
			}
		});
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
			final String subItemName = itemStack.getTranslationKey();
			Log.info("[PluginNatura] Adding '{}' to list of {}", subItemName, groupName);
			consumer.accept(itemStack);
			return;
		}

		subItems.forEach(itemStack -> {
			final String subItemName = itemStack.getTranslationKey();
			Log.info("[PluginNatura] Adding '{}' to list of {}", subItemName, groupName);
			consumer.accept(itemStack);
		});
	}

	private ItemStack findPotashAppleInRegistry() {
		Item edibleItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID, "edibles"));
		if (edibleItem != null) {
			NonNullList<ItemStack> edibleSubItems = NonNullList.create();
			edibleItem.getSubItems(CreativeTabs.SEARCH, edibleSubItems);
			for (ItemStack edibleSubItem : edibleSubItems) {
				if (edibleSubItem.getTranslationKey().endsWith("potashapple")) {
					return edibleSubItem;

				}
			}
		}

		return ItemStack.EMPTY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		Iterables.concat(
			edibles, logs, saplings, shrooms,
			materials, seeds, berries, fruits,
			crops
		).forEach(crateRegistry::registerCrate);
	}

	@Override
	public void registerRecipes() {
		int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		for (ItemStack seed : seeds) {
			RecipeManagers.squeezerManager.addRecipe(10, NonNullList.from(seed, seed), Fluids.SEED_OIL.getFluid(amount));
			RecipeManagers.moistenerManager.addRecipe(seed, new ItemStack(Blocks.MYCELIUM), 5000);
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.FARMING)) {
			cropBlocks.forEach(itemStack -> {
				Block block = ItemStackUtil.getBlock(itemStack);
				ItemStack seedItem;
				int maxAge;
				try {
					maxAge = (int) block.getClass().getDeclaredMethod("getMaxAge").invoke(block);
					seedItem = block.getPickBlock(block.getBlockState().getBaseState(), null, null, null, null);
				} catch (Exception ignored) {
					return;
				}
				Log.info("[PluginNatura] Addding crop '{}'", itemStack);
				if (seedItem.isEmpty()) {
					return;
				}

				FarmRegistry.getInstance().registerFarmables(ForestryFarmIdentifier.CROPS,
					new FarmableAgingCrop(
						seedItem,
						block,
						(IProperty<Integer>) block.getBlockState().getProperty("age"),
						maxAge
					)
				);
			});
		}

		ItemRegistryCore coreItems = ModuleCore.getItems();

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 2;
		final int juiceAmount = Math.max(amount, 1); // Produce at least 1 mb of juice.
		ItemStack mulch = coreItems.mulch.getItemStack();
		fruits.forEach(fruit -> RecipeManagers.squeezerManager.addRecipe(
			10, NonNullList.from(fruit, fruit),
			Fluids.JUICE.getFluid(juiceAmount), mulch,
			ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple"))
		);

		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 25;
		amount = Math.max(amount, 1); // Produce at least 1 mb of juice.

		for (ItemStack berry : berries) {
			RecipeManagers.squeezerManager.addRecipe(3, NonNullList.from(berry, berry), Fluids.JUICE.getFluid(amount));
		}

		crops.forEach(crop -> {
			RecipeUtil.addFermenterRecipes(crop, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
			if (crop.getTranslationKey().matches("^.*cotton$")) {
				return;
			}

			int compostWheatAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
			if (compostWheatAmount > 0) {
				ItemStack compostWheat = coreItems.fertilizerCompound.getItemStack(compostWheatAmount);
				RecipeUtil.addRecipe(compostWheat.getTranslationKey(), compostWheat, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', crop);
			}
			FuelManager.moistenerResource.put(crop, new MoistenerFuel(crop, coreItems.mouldyWheat.getItemStack(), 0, 300));
		});
	}


	/*
	 * Register soils required by Natura trees. Must run in postInit(), after core PluginFarming has registered FarmingLogic instances
	 */
	@Override
	public void postInit() {
		IFarmRegistry registry = FarmRegistry.getInstance();
		IFarmProperties mushroomFarm = registry.getProperties(ForestryFarmIdentifier.SHROOM);
		IFarmProperties farmArboreal = registry.getProperties(ForestryFarmIdentifier.ARBOREAL);
		if (farmArboreal != null) {
			farmArboreal.registerSoil(new ItemStack(Blocks.NETHERRACK), Blocks.NETHERRACK.getDefaultState());
		}
		if (mushroomFarm != null) {
			mushroomFarm.registerSoil(new ItemStack(Blocks.NETHERRACK), Blocks.NETHERRACK.getDefaultState());
			mushroomFarm.registerSoil(new ItemStack(Blocks.SOUL_SAND), Blocks.SOUL_SAND.getDefaultState());
		}
	}

}
