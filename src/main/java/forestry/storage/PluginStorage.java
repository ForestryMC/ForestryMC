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
package forestry.storage;

import javax.annotation.Nullable;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import forestry.Forestry;
import forestry.api.core.ForestryAPI;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackFilterConfigurable;
import forestry.api.storage.StorageManager;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.blocks.BlockRegistryApiculture;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.items.ItemCrated;
import forestry.core.items.ItemRegistryCore;
import forestry.core.models.ModelCrate;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.Translator;
import forestry.lepidopterology.PluginLepidopterology;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import forestry.storage.items.ItemRegistryStorage;
import forestry.storage.proxy.ProxyStorage;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@ForestryPlugin(pluginID = ForestryPluginUids.STORAGE, name = "Storage", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.storage.description")
public class PluginStorage extends BlankForestryPlugin {

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.storage.proxy.ProxyStorageClient", serverSide = "forestry.storage.proxy.ProxyStorage")
	public static ProxyStorage proxy;

	private static final List<ItemCrated> crates = new ArrayList<>();
	private static final String CONFIG_CATEGORY = "backpacks";

	@Nullable
	private static ItemRegistryStorage items;

	private final Multimap<String, String> backpackAcceptedOreDictRegexpDefaults = HashMultimap.create();
	private final Multimap<String, String> backpackRejectedOreDictRegexpDefaults = HashMultimap.create();
	private final Multimap<String, String> backpackAcceptedItemDefaults = HashMultimap.create();
	private final Multimap<String, String> backpackRejectedItemDefaults = HashMultimap.create();

	private final List<String> forestryBackpackUids = Arrays.asList(
			BackpackManager.MINER_UID,
			BackpackManager.DIGGER_UID,
			BackpackManager.FORESTER_UID,
			BackpackManager.HUNTER_UID,
			BackpackManager.ADVENTURER_UID,
			BackpackManager.BUILDER_UID
	);

	public static ItemRegistryStorage getItems() {
		Preconditions.checkState(items != null);
		return items;
	}

	@Override
	public void setupAPI() {
		super.setupAPI();

		StorageManager.crateRegistry = new CrateRegistry();

		BackpackManager.backpackInterface = new BackpackInterface();

		BackpackDefinition definition;

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			Predicate<ItemStack> filter = BackpackManager.backpackInterface.createNaturalistBackpackFilter("rootBees");
			definition = new BackpackDefinition(new Color(0xc4923d), Color.WHITE, filter);
			BackpackManager.backpackInterface.registerBackpackDefinition("apiarist", definition);
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			Predicate<ItemStack> filter = BackpackManager.backpackInterface.createNaturalistBackpackFilter("rootButterflies");
			definition = new BackpackDefinition(new Color(0x995b31), Color.WHITE, filter);
			BackpackManager.backpackInterface.registerBackpackDefinition("lepidopterist", definition);
		}

		definition = new BackpackDefinition(new Color(0x36187d), Color.WHITE);
		BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.MINER_UID, definition);

		definition = new BackpackDefinition(new Color(0x363cc5), Color.WHITE);
		BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.DIGGER_UID, definition);

		definition = new BackpackDefinition(new Color(0x347427), Color.WHITE);
		BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.FORESTER_UID, definition);

		definition = new BackpackDefinition(new Color(0x412215), Color.WHITE);
		BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.HUNTER_UID, definition);

		definition = new BackpackDefinition(new Color(0x7fb8c2), Color.WHITE);
		BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.ADVENTURER_UID, definition);

		definition = new BackpackDefinition(new Color(0xdd3a3a), Color.WHITE);
		BackpackManager.backpackInterface.registerBackpackDefinition(BackpackManager.BUILDER_UID, definition);

		proxy.registerCrateModel();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryStorage();
	}

	@Override
	public void preInit() {
		registerFenceAndFenceGatesToOreDict();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postInit() {
		final String newConfig = CONFIG_CATEGORY + ".cfg";

		File configFile = new File(Forestry.instance.getConfigFolder(), newConfig);
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "2.0.0");
		if (!config.getDefinedConfigVersion().equals(config.getLoadedConfigVersion())) {
			boolean deleted = configFile.delete();
			if (deleted) {
				config = new LocalizedConfiguration(configFile, "2.0.0");
			}
		}

		setDefaultsForConfig();

		for (String backpackUid : forestryBackpackUids) {
			handleBackpackConfig(config, backpackUid);
		}

		config.save();
	}

	private void setDefaultsForConfig() {
		ItemRegistryCore coreItems = PluginCore.getItems();

		backpackAcceptedOreDictRegexpDefaults.get(BackpackManager.MINER_UID).addAll(Arrays.asList(
				"obsidian",
				"ore[A-Z].*",
				"dust[A-Z].*",
				"gem[A-Z].*",
				"ingot[A-Z].*",
				"nugget[A-Z].*",
				"crushed[A-Z].*",
				"cluster[A-Z].*",
				"denseore[A-Z].*"
		));

		backpackAcceptedOreDictRegexpDefaults.get(BackpackManager.DIGGER_UID).addAll(Arrays.asList(
				"cobblestone",
				"dirt",
				"gravel",
				"netherrack",
				"stone",
				"stone[A-Z].*",
				"sand"
		));

		backpackAcceptedOreDictRegexpDefaults.get(BackpackManager.HUNTER_UID).addAll(Arrays.asList(
				"bone",
				"egg",
				"enderpearl",
				"feather",
				"fish[A-Z].*",
				"gunpowder",
				"leather",
				"slimeball",
				"string"
		));

		backpackAcceptedOreDictRegexpDefaults.get(BackpackManager.FORESTER_UID).addAll(Arrays.asList(
				"logWood",
				"stickWood",
				"woodStick",
				"saplingTree",
				"treeSapling",
				"vine",
				"sugarcane",
				"blockCactus",
				"crop[A-Z].*",
				"seed[A-Z].*",
				"tree[A-Z].*"
		));

		backpackAcceptedOreDictRegexpDefaults.get(BackpackManager.BUILDER_UID).addAll(Arrays.asList(
				"block[A-Z].*",
				"paneGlass[A-Z].*",
				"slabWood[A-Z].*",
				"stainedClay[A-Z].*",
				"stainedGlass[A-Z].*",
				"stone",
				"sandstone",
				OreDictUtil.PLANK_WOOD,
				OreDictUtil.STAIR_WOOD,
				OreDictUtil.SLAB_WOOD,
				OreDictUtil.FENCE_WOOD,
				OreDictUtil.FENCE_GATE_WOOD,
				OreDictUtil.TRAPDOOR_WOOD,
				"glass",
				"paneGlass",
				"torch",
				"chest",
				"chest[A-Z].*",
				"workbench",
				"doorWood"
		));

		backpackAcceptedItemDefaults.get(BackpackManager.MINER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Blocks.COAL_ORE),
				new ItemStack(Items.COAL),
				coreItems.bronzePickaxe.getItemStack(),
				coreItems.kitPickaxe.getItemStack(),
				coreItems.brokenBronzePickaxe.getItemStack()
		)));

		backpackAcceptedItemDefaults.get(BackpackManager.DIGGER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Blocks.DIRT, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.FLINT),
				new ItemStack(Blocks.SANDSTONE, 1, 0),
				new ItemStack(Items.CLAY_BALL),
				new ItemStack(Items.SNOWBALL),
				new ItemStack(Blocks.SOUL_SAND),
				coreItems.bronzeShovel.getItemStack(),
				coreItems.kitShovel.getItemStack(),
				coreItems.brokenBronzeShovel.getItemStack()
		)));

		backpackAcceptedItemDefaults.get(BackpackManager.FORESTER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Blocks.RED_MUSHROOM),
				new ItemStack(Blocks.BROWN_MUSHROOM),
				new ItemStack(Blocks.RED_FLOWER, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.YELLOW_FLOWER),
				new ItemStack(Blocks.TALLGRASS, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.DOUBLE_PLANT, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.PUMPKIN),
				new ItemStack(Blocks.MELON_BLOCK),
				new ItemStack(Items.GOLDEN_APPLE),
				new ItemStack(Items.NETHER_WART),
				new ItemStack(Items.WHEAT_SEEDS),
				new ItemStack(Items.PUMPKIN_SEEDS),
				new ItemStack(Items.MELON_SEEDS),
				new ItemStack(Items.BEETROOT_SEEDS),
				new ItemStack(Items.BEETROOT),
				new ItemStack(Items.CHORUS_FRUIT),
				new ItemStack(Items.APPLE)
		)));

		backpackAcceptedItemDefaults.get(BackpackManager.HUNTER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Items.BLAZE_POWDER),
				new ItemStack(Items.BLAZE_ROD),
				new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.SKULL, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.GHAST_TEAR),
				new ItemStack(Items.GOLD_NUGGET),
				new ItemStack(Items.ARROW),
				new ItemStack(Items.SPECTRAL_ARROW),
				new ItemStack(Items.TIPPED_ARROW),
				new ItemStack(Items.PORKCHOP),
				new ItemStack(Items.COOKED_PORKCHOP),
				new ItemStack(Items.BEEF),
				new ItemStack(Items.COOKED_BEEF),
				new ItemStack(Items.CHICKEN),
				new ItemStack(Items.COOKED_CHICKEN),
				new ItemStack(Items.MUTTON),
				new ItemStack(Items.COOKED_MUTTON),
				new ItemStack(Items.RABBIT),
				new ItemStack(Items.COOKED_RABBIT),
				new ItemStack(Items.RABBIT_FOOT),
				new ItemStack(Items.RABBIT_HIDE),
				new ItemStack(Items.SPIDER_EYE),
				new ItemStack(Items.FERMENTED_SPIDER_EYE),
				new ItemStack(Items.DYE, 1, 0),
				new ItemStack(Blocks.HAY_BLOCK),
				new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.ENDER_EYE),
				new ItemStack(Items.MAGMA_CREAM),
				new ItemStack(Items.SPECKLED_MELON),
				new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.COOKED_FISH, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.LEAD),
				new ItemStack(Items.FISHING_ROD),
				new ItemStack(Items.NAME_TAG),
				new ItemStack(Items.SADDLE),
				new ItemStack(Items.DIAMOND_HORSE_ARMOR),
				new ItemStack(Items.GOLDEN_HORSE_ARMOR),
				new ItemStack(Items.IRON_HORSE_ARMOR)
		)));

		backpackAcceptedItemDefaults.get(BackpackManager.BUILDER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Blocks.REDSTONE_TORCH),
				new ItemStack(Blocks.REDSTONE_LAMP),
				new ItemStack(Blocks.SEA_LANTERN),
				new ItemStack(Blocks.END_ROD),
				new ItemStack(Blocks.STONEBRICK, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.BRICK_BLOCK),
				new ItemStack(Blocks.CLAY),
				new ItemStack(Blocks.HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.PACKED_ICE),
				new ItemStack(Blocks.NETHER_BRICK),
				new ItemStack(Blocks.NETHER_BRICK_FENCE),
				new ItemStack(Blocks.CRAFTING_TABLE),
				new ItemStack(Blocks.FURNACE),
				new ItemStack(Blocks.LEVER),
				new ItemStack(Blocks.DISPENSER),
				new ItemStack(Blocks.DROPPER),
				new ItemStack(Blocks.LADDER),
				new ItemStack(Blocks.IRON_BARS),
				new ItemStack(Blocks.QUARTZ_BLOCK, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.QUARTZ_STAIRS),
				new ItemStack(Blocks.SANDSTONE_STAIRS),
				new ItemStack(Blocks.RED_SANDSTONE_STAIRS),
				new ItemStack(Blocks.COBBLESTONE_WALL, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.STONE_BUTTON),
				new ItemStack(Blocks.WOODEN_BUTTON),
				new ItemStack(Blocks.STONE_SLAB, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.STONE_SLAB2, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.WOODEN_SLAB, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.PURPUR_BLOCK),
				new ItemStack(Blocks.PURPUR_PILLAR),
				new ItemStack(Blocks.PURPUR_STAIRS),
				new ItemStack(Blocks.PURPUR_SLAB),
				new ItemStack(Blocks.END_BRICKS),
				new ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.IRON_TRAPDOOR),
				new ItemStack(Blocks.STONE_PRESSURE_PLATE),
				new ItemStack(Blocks.WOODEN_PRESSURE_PLATE),
				new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE),
				new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
				new ItemStack(Items.SIGN),
				new ItemStack(Items.ITEM_FRAME),
				new ItemStack(Items.ACACIA_DOOR),
				new ItemStack(Items.BIRCH_DOOR),
				new ItemStack(Items.DARK_OAK_DOOR),
				new ItemStack(Items.IRON_DOOR),
				new ItemStack(Items.JUNGLE_DOOR),
				new ItemStack(Items.OAK_DOOR),
				new ItemStack(Items.SPRUCE_DOOR)
		)));

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			BlockRegistryApiculture beeBlocks = PluginApiculture.getBlocks();
			backpackAcceptedItemDefaults.get(BackpackManager.BUILDER_UID).addAll(getItemStrings(Arrays.asList(
					new ItemStack(beeBlocks.candle, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(beeBlocks.stump, 1, OreDictionary.WILDCARD_VALUE)
			)));
		}

		// include everything added via the API
		BackpackInterface backpackInterface = (BackpackInterface) BackpackManager.backpackInterface;
		backpackAcceptedItemDefaults.putAll(backpackInterface.getBackpackAcceptedItems());
	}

	// Should be ore dicted in Forge at some point.
	private static void registerFenceAndFenceGatesToOreDict() {
		OreDictionary.registerOre(OreDictUtil.FENCE_WOOD, Blocks.OAK_FENCE);
		OreDictionary.registerOre(OreDictUtil.FENCE_WOOD, Blocks.SPRUCE_FENCE);
		OreDictionary.registerOre(OreDictUtil.FENCE_WOOD, Blocks.BIRCH_FENCE);
		OreDictionary.registerOre(OreDictUtil.FENCE_WOOD, Blocks.JUNGLE_FENCE);
		OreDictionary.registerOre(OreDictUtil.FENCE_WOOD, Blocks.DARK_OAK_FENCE);
		OreDictionary.registerOre(OreDictUtil.FENCE_WOOD, Blocks.ACACIA_FENCE);
		OreDictionary.registerOre(OreDictUtil.FENCE_GATE_WOOD, Blocks.OAK_FENCE_GATE);
		OreDictionary.registerOre(OreDictUtil.FENCE_GATE_WOOD, Blocks.SPRUCE_FENCE_GATE);
		OreDictionary.registerOre(OreDictUtil.FENCE_GATE_WOOD, Blocks.BIRCH_FENCE_GATE);
		OreDictionary.registerOre(OreDictUtil.FENCE_GATE_WOOD, Blocks.JUNGLE_FENCE_GATE);
		OreDictionary.registerOre(OreDictUtil.FENCE_GATE_WOOD, Blocks.DARK_OAK_FENCE_GATE);
		OreDictionary.registerOre(OreDictUtil.FENCE_GATE_WOOD, Blocks.ACACIA_FENCE_GATE);
	}

	private static Set<String> getItemStrings(List<ItemStack> itemStacks) {
		Set<String> itemStrings = new HashSet<>(itemStacks.size());
		for (ItemStack itemStack : itemStacks) {
			String itemString = ItemStackUtil.getStringForItemStack(itemStack);
			itemStrings.add(itemString);
		}
		return itemStrings;
	}

	private void handleBackpackConfig(LocalizedConfiguration config, String backpackUid) {
		BackpackDefinition backpackDefinition = (BackpackDefinition) BackpackManager.backpackInterface.getBackpackDefinition(backpackUid);
		if (backpackDefinition == null) {
			return;
		}

		Predicate<ItemStack> filter = backpackDefinition.getFilter();
		if (filter instanceof IBackpackFilterConfigurable) {
			IBackpackFilterConfigurable backpackFilter = (IBackpackFilterConfigurable) filter;
			backpackFilter.clear();

			// accepted items
			{
				List<String> defaultAcceptedItemNames = new ArrayList<>(backpackAcceptedItemDefaults.get(backpackUid));
				Collections.sort(defaultAcceptedItemNames);
				String[] defaultValidItems = defaultAcceptedItemNames.toArray(new String[defaultAcceptedItemNames.size()]);

				Property backpackConf = config.get("backpacks." + backpackUid, "item.stacks.accepted", defaultValidItems);
				backpackConf.setComment(Translator.translateToLocalFormatted("for.config.backpacks.item.stacks.format", backpackUid));

				String[] backpackItemList = backpackConf.getStringList();
				List<ItemStack> backpackItems = ItemStackUtil.parseItemStackStrings(backpackItemList, OreDictionary.WILDCARD_VALUE);
				for (ItemStack backpackItem : backpackItems) {
					backpackFilter.acceptItem(backpackItem);
				}
			}

			// rejected items
			{
				List<String> defaultRejectedItemNames = new ArrayList<>(backpackRejectedItemDefaults.get(backpackUid));
				Collections.sort(defaultRejectedItemNames);
				String[] defaultRejectedItems = defaultRejectedItemNames.toArray(new String[defaultRejectedItemNames.size()]);

				Property backpackConf = config.get("backpacks." + backpackUid, "item.stacks.rejected", defaultRejectedItems);
				backpackConf.setComment(Translator.translateToLocalFormatted("for.config.backpacks.item.stacks.format", backpackUid));

				String[] backpackItemList = backpackConf.getStringList();
				List<ItemStack> backpackItems = ItemStackUtil.parseItemStackStrings(backpackItemList, OreDictionary.WILDCARD_VALUE);
				for (ItemStack backpackItem : backpackItems) {
					backpackFilter.rejectItem(backpackItem);
				}
			}

			// accepted oreDict
			{
				List<String> defaultOreRegexpList = new ArrayList<>(backpackAcceptedOreDictRegexpDefaults.get(backpackUid));
				Collections.sort(defaultOreRegexpList);
				String[] defaultOreRegexpNames = defaultOreRegexpList.toArray(new String[defaultOreRegexpList.size()]);

				Property backpackConf = config.get("backpacks." + backpackUid, "ore.dict.accepted", defaultOreRegexpNames);
				backpackConf.setComment(Translator.translateToLocalFormatted("for.config.backpacks.ore.dict.format", backpackUid));

				for (String name : OreDictionary.getOreNames()) {
					if (name == null) {
						Log.error("Found a null oreName in the ore dictionary");
					} else {
						for (String regex : backpackConf.getStringList()) {
							if (name.matches(regex)) {
								backpackFilter.acceptOreDictName(name);
							}
						}
					}
				}
			}

			// rejected oreDict
			{
				List<String> defaultOreRegexpList = new ArrayList<>(backpackRejectedOreDictRegexpDefaults.get(backpackUid));
				Collections.sort(defaultOreRegexpList);
				String[] defaultOreRegexpNames = defaultOreRegexpList.toArray(new String[defaultOreRegexpList.size()]);

				Property backpackConf = config.get("backpacks." + backpackUid, "ore.dict.rejected", defaultOreRegexpNames);
				backpackConf.setComment(Translator.translateToLocalFormatted("for.config.backpacks.ore.dict.format", backpackUid));

				for (String name : OreDictionary.getOreNames()) {
					if (name == null) {
						Log.error("Found a null oreName in the ore dictionary");
					} else {
						for (String regex : backpackConf.getStringList()) {
							if (name.matches(regex)) {
								backpackFilter.rejectOreDictName(name);
							}
						}
					}
				}
			}
		}
	}

	public static void registerCrate(ItemCrated crate) {
		crates.add(crate);
	}

	public static void createCrateRecipes() {
		for (ItemCrated crate : crates) {
			ItemStack crateStack = new ItemStack(crate);
			ItemStack uncrated = crate.getContained();
			if (!uncrated.isEmpty()) {
				if (crate.getOreDictName() != null) {
					addCrating(crateStack, crate.getOreDictName());
				} else {
					addCrating(crateStack, uncrated);
				}
				addUncrating(crateStack, uncrated);
			}
		}
	}

	private static void addCrating(ItemStack crateStack, Object uncrated) {
		FluidStack water = new FluidStack(FluidRegistry.WATER, Constants.CARPENTER_CRATING_LIQUID_QUANTITY);
		ItemStack box = getItems().crate.getItemStack();
		RecipeManagers.carpenterManager.addRecipe(Constants.CARPENTER_CRATING_CYCLES, water, box, crateStack, "###", "###", "###", '#', uncrated);
	}

	private static void addUncrating(ItemStack crateStack, ItemStack uncrated) {
		ItemStack product = new ItemStack(uncrated.getItem(), 9, uncrated.getItemDamage());
		RecipeManagers.carpenterManager.addRecipe(Constants.CARPENTER_UNCRATING_CYCLES, ItemStack.EMPTY, product, "#", '#', crateStack);
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-backpack-items")) {
			String[] tokens = message.getStringValue().split("@");
			if (tokens.length != 2) {
				IMCUtil.logInvalidIMCMessage(message);
				return true;
			}

			String backpackUid = tokens[0];
			String itemStackStrings = tokens[1];

			IBackpackDefinition backpackDefinition = BackpackManager.backpackInterface.getBackpackDefinition(backpackUid);
			if (backpackDefinition == null) {
				String errorMessage = IMCUtil.getInvalidIMCMessageText(message);
				Log.error("{} For non-existent backpack {}.", errorMessage, backpackUid);
				return true;
			}

			List<ItemStack> itemStacks = ItemStackUtil.parseItemStackStrings(itemStackStrings, 0);
			for (ItemStack itemStack : itemStacks) {
				BackpackManager.backpackInterface.addItemToForestryBackpack(backpackUid, itemStack);
			}

			return true;
		}
		return false;
	}

	@Override
	public IPickupHandler getPickupHandler() {
		return new PickupHandlerStorage();
	}

	@Override
	public IResupplyHandler getResupplyHandler() {
		return new ResupplyHandler();
	}

	@Override
	public void registerRecipes() {
		ItemRegistryStorage items = getItems();
		if (items.apiaristBackpack != null && ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			BlockRegistryApiculture beeBlocks = PluginApiculture.getBlocks();
			addBackpackRecipe(items.apiaristBackpack, "stickWood", beeBlocks.beeChest);
		}

		if (items.lepidopteristBackpack != null && ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			BlockRegistryLepidopterology butterflyBlocks = PluginLepidopterology.getBlocks();
			ItemStack chest = new ItemStack(butterflyBlocks.butterflyChest);
			addBackpackRecipe(items.lepidopteristBackpack, "stickWood", chest);
		}

		addBackpackRecipe(items.minerBackpack, "ingotIron");
		addBackpackRecipe(items.diggerBackpack, "stone");
		addBackpackRecipe(items.foresterBackpack, "logWood");
		addBackpackRecipe(items.hunterBackpack, Items.FEATHER);
		addBackpackRecipe(items.adventurerBackpack, Items.BONE);
		addBackpackRecipe(items.builderBackpack, Items.CLAY_BALL);

		// / CARPENTER
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {
			// / CRATES
			RecipeManagers.carpenterManager.addRecipe(20, new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), ItemStack.EMPTY, items.crate.getItemStack(24),
					" # ", "# #", " # ", '#', "logWood");

			// / BACKPACKS WOVEN
			addT2BackpackRecipe(items.minerBackpack, items.minerBackpackT2);
			addT2BackpackRecipe(items.diggerBackpack, items.diggerBackpackT2);
			addT2BackpackRecipe(items.foresterBackpack, items.foresterBackpackT2);
			addT2BackpackRecipe(items.hunterBackpack, items.hunterBackpackT2);
			addT2BackpackRecipe(items.adventurerBackpack, items.adventurerBackpackT2);
			addT2BackpackRecipe(items.builderBackpack, items.builderBackpackT2);
		}
	}

	private static void addBackpackRecipe(Item backpack, Object material) {
		addBackpackRecipe(backpack, material, "chestWood");
	}

	private static void addBackpackRecipe(Item backpack, Object material, Object chest) {
		RecipeUtil.addRecipe(backpack,
				"X#X",
				"VYV",
				"X#X",
				'#', Blocks.WOOL,
				'X', Items.STRING,
				'V', material,
				'Y', chest);
	}

	private static void addT2BackpackRecipe(Item backpackT1, Item backpackT2) {
		ItemRegistryCore coreItems = PluginCore.getItems();

		ItemStack wovenSilk = coreItems.craftingMaterial.getWovenSilk();
		RecipeManagers.carpenterManager.addRecipe(200, new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), ItemStack.EMPTY, new ItemStack(backpackT2),
				"WXW",
				"WTW",
				"WWW",
				'X', "gemDiamond",
				'W', wovenSilk,
				'T', backpackT1);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onBakeModel(ModelBakeEvent event) {
		ModelCrate.onModelBake(event);
	}

	@Override
	public void addLootPoolNames(Set<String> lootPoolNames) {
		super.addLootPoolNames(lootPoolNames);
		lootPoolNames.add("forestry_storage_items");
	}
}
