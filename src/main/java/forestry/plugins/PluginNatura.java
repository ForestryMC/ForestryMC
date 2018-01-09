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

import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmLogic;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.farming.FarmRegistry;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.farming.logic.farmables.FarmableSapling;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@SuppressWarnings("unused")
@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.NATURA, name = "Natura", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.natura.description")
public class PluginNatura extends BlankForestryModule {

	public static final String modId = "natura";

	private static ArrayList<ItemStack> fruits = new ArrayList<>();
	private static ArrayList<ItemStack> soups = new ArrayList<>();
	private static ArrayList<ItemStack> berries = new ArrayList<>();
	private static ArrayList<ItemStack> edibles = new ArrayList<>();
	private static ArrayList<ItemStack> seeds = new ArrayList<>();
	private static ArrayList<ItemStack> logs = new ArrayList<>();
	private static ArrayList<ItemStack> saplings = new ArrayList<>();
	private static ArrayList<ItemStack> shrooms = new ArrayList<>();
	private static ArrayList<ItemStack> materials = new ArrayList<>();
	private static ArrayList<ItemStack> crops = new ArrayList<>();
	private static ArrayList<ItemStack> cropBlocks = new ArrayList<>();

	public PluginNatura() {
		super();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(modId);
	}

	@Override
	public String getFailMessage() {
		return "Natura not found";
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent<Item> itemRegistryEvent) {
		ForgeRegistries.ITEMS.forEach(item -> {
			final ResourceLocation registryName = item.getRegistryName();
			if(!registryName.getResourceDomain().equals(modId)) return;

			final String itemName = registryName.getResourcePath();

			if(itemName.matches("^.*_sapling\\d?$")) {
				consumeSubItems(item, "saplings", saplings);

				RecipeUtil.addFermenterRecipes(
						new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE),
						ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"),
						Fluids.BIOMASS
				);

				FarmRegistry.getInstance().registerFarmables("farmArboreal", new FarmableSapling(
						new ItemStack(item),
						new ItemStack[] {}
				));
				return;
			}

			if(itemName.matches("^edibles|.*fruit_item|soups$")) {
				complexConsumeSubItems(item, "edibles", subitem -> {
					final String subItemName = subitem.getUnlocalizedName();
					if(subItemName.matches("^.*berry$")) {
						berries.add(subitem);
						return;
					}

					if(subItemName.matches("^.*(fruit_item|apple)$")) {
						fruits.add(subitem);
						return;
					}

					if(subItemName.matches("^.*stew$")) {
						soups.add(subitem);
						return;
					}

					edibles.add(subitem);
				});
			}

			if(itemName.matches("^.*_seeds$")) {
				consumeSubItems(item, "seeds", seeds);
				return;
			}

			if(itemName.matches("^.*_logs\\d?$")) {
				consumeSubItems(item, "logs", logs);
				return;
			}

			if(itemName.matches("^.*_crop\\d?$")) {
				consumeSubItems(item, "cropBlocks", cropBlocks);
				return;
			}

			if(itemName.matches("^materials$")) {
				complexConsumeSubItems(item, "materials", subitem -> {
					final String subItemName = subitem.getUnlocalizedName();

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
		complexConsumeSubItems(item, groupName, subItem -> consumer.add(subItem));
	}

	private void complexConsumeSubItems(Item item, String groupName, Consumer<ItemStack> consumer) {
		final NonNullList<ItemStack> subItems = NonNullList.create();
		item.getSubItems(CreativeTabs.SEARCH, subItems);

		// Fallback if item is not returned in sub items
		if(subItems.isEmpty()) {
			final ItemStack itemStack = new ItemStack(item, 1);
			final String subItemName = itemStack.getUnlocalizedName();
			Log.info("[PluginNatura] Adding '{}' to list of {}", subItemName, groupName);
			consumer.accept(itemStack);
			return;
		}

		subItems.forEach(itemStack -> {
			final String subItemName = itemStack.getUnlocalizedName();
			Log.info("[PluginNatura] Adding '{}' to list of {}", subItemName, groupName);
			consumer.accept(itemStack);
		});
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;

		Iterables.concat(
				edibles, logs, saplings, shrooms,
				materials, seeds, berries, fruits,
				crops
		).forEach(item -> crateRegistry.registerCrate(item));
	}

	@Override
	public void registerRecipes() {
		int amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		for (ItemStack seed : seeds) {
			RecipeManagers.squeezerManager.addRecipe(10, NonNullList.from(seed, seed), Fluids.SEED_OIL.getFluid(amount));
			RecipeManagers.moistenerManager.addRecipe(seed, new ItemStack(Blocks.MYCELIUM), 5000);
		}

		if (ForestryAPI.enabledModules.contains(new ResourceLocation(Constants.MOD_ID, ForestryModuleUids.FARMING))) {
			cropBlocks.forEach(itemStack -> {
				Block block = ItemStackUtil.getBlock(itemStack);
				ItemStack seedItem;
				int maxAge;
				try {
					maxAge = (int)block.getClass().getDeclaredMethod("getMaxAge").invoke(block);
					seedItem = block.getPickBlock(block.getBlockState().getBaseState(), null, null, null, null);
				} catch (Exception ignored) {
					return;
				}
				Log.info("[PluginNatura] Addding crop '{}'", itemStack);
				if (seedItem.isEmpty()) {
					return;
				}

				FarmRegistry.getInstance().registerFarmables("farmCrops",
					new FarmableAgingCrop(
						seedItem,
						block,
						(IProperty<Integer>)block.getBlockState().getProperty("age"),
						maxAge
					)
				);
			});
		}
		
		amount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple") / 2;
		final int juiceAmount = Math.max(amount, 1); // Produce at least 1 mb of juice.
		ItemStack mulch = ModuleCore.items.mulch.getItemStack();
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
			if (crop.getUnlocalizedName().matches("^.*cotton$")) return;

			int compostWheatAmount = ForestryAPI.activeMode.getIntegerSetting("recipe.output.compost.wheat");
			if (compostWheatAmount > 0) {
				ItemStack compostWheat = ModuleCore.items.fertilizerCompound.getItemStack(compostWheatAmount);
				RecipeUtil.addRecipe(compostWheat.getUnlocalizedName(), compostWheat, " X ", "X#X", " X ", '#', Blocks.DIRT, 'X', crop);
			}
			FuelManager.moistenerResource.put(crop, new MoistenerFuel(crop, ModuleCore.items.mouldyWheat.getItemStack(), 0, 300));
		});
	}
	
	
	/* 
	 * Register soils required by Natura trees. Must run in postInit(), after core PluginFarming has registered FarmingLogic instances
	 */
	@Override
	public void postInit() {
		IFarmLogic farmArboreal = FarmRegistry.getInstance().getFarmLogic("farmArboreal");
		farmArboreal.addSoil(new ItemStack(Blocks.NETHERRACK), Blocks.NETHERRACK.getDefaultState(), false);
	
	}

}
