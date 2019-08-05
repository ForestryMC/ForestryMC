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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

import forestry.Forestry;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.IBackpackFilterConfigurable;
import forestry.api.storage.StorageManager;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.blocks.BlockRegistryApiculture;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.Translator;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.storage.items.ItemRegistryBackpacks;

@ForestryModule(moduleID = ForestryModuleUids.BACKPACKS, containerID = Constants.MOD_ID, name = "Backpack", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.backpacks.description", lootTable = "storage")
public class ModuleBackpacks extends BlankForestryModule {

	private static final String CONFIG_CATEGORY = "backpacks";

	@Nullable
	private static ItemRegistryBackpacks items;

	private final Map<String, List<String>> backpackAcceptedOreDictRegexpDefaults = new HashMap<>();
	private final Map<String, List<String>> backpackAcceptedItemDefaults = new HashMap<>();

	private final List<String> forestryBackpackUids = Arrays.asList(
		BackpackManager.MINER_UID,
		BackpackManager.DIGGER_UID,
		BackpackManager.FORESTER_UID,
		BackpackManager.HUNTER_UID,
		BackpackManager.ADVENTURER_UID,
		BackpackManager.BUILDER_UID
	);

	public static ItemRegistryBackpacks getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	@Override
	public void setupAPI() {

		StorageManager.crateRegistry = new CrateRegistry();

		BackpackManager.backpackInterface = new BackpackInterface();

		BackpackDefinition definition;

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			Predicate<ItemStack> filter = BackpackManager.backpackInterface.createNaturalistBackpackFilter("rootBees");
			definition = new BackpackDefinition(new Color(0xc4923d), Color.WHITE, filter);
			BackpackManager.backpackInterface.registerBackpackDefinition("apiarist", definition);
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
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
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryBackpacks();
	}

	@Override
	public void preInit() {
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

	//TODO - in 1.13 just ship json file that people can edit, don't have config in code.
	private void setDefaultsForConfig() {
		ItemRegistryCore coreItems = ModuleCore.getItems();

		backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.MINER_UID, Arrays.asList(
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

		backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.DIGGER_UID, Arrays.asList(
			"cobblestone",
			"dirt",
			"grass",
			"grass[A-Z].*",
			"gravel",
			"netherrack",
			"stone",
			"stone[A-Z].*",
			"sandstone",
			"sand"
		));

		backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.HUNTER_UID, Arrays.asList(
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

		backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.FORESTER_UID, Arrays.asList(
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

		backpackAcceptedOreDictRegexpDefaults.put(BackpackManager.BUILDER_UID, Arrays.asList(
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

		backpackAcceptedItemDefaults.put(BackpackManager.MINER_UID, getItemStrings(Arrays.asList(
			new ItemStack(Blocks.COAL_ORE),
			new ItemStack(Items.COAL),
			coreItems.bronzePickaxe.getItemStack(),
			coreItems.kitPickaxe.getItemStack(),
			coreItems.brokenBronzePickaxe.getItemStack()
		)));

		backpackAcceptedItemDefaults.put(BackpackManager.DIGGER_UID, getItemStrings(Arrays.asList(
			new ItemStack(Blocks.DIRT, 1, OreDictionary.WILDCARD_VALUE),
			new ItemStack(Items.FLINT),
			new ItemStack(Items.CLAY_BALL),
			new ItemStack(Items.SNOWBALL),
			new ItemStack(Blocks.SOUL_SAND),
			new ItemStack(Blocks.CLAY),
			new ItemStack(Blocks.SNOW),
			coreItems.bronzeShovel.getItemStack(),
			coreItems.kitShovel.getItemStack(),
			coreItems.brokenBronzeShovel.getItemStack()
		)));

		backpackAcceptedItemDefaults.put(BackpackManager.FORESTER_UID, getItemStrings(Arrays.asList(
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
			new ItemStack(Blocks.CHORUS_PLANT),
			new ItemStack(Items.APPLE)
		)));

		backpackAcceptedItemDefaults.put(BackpackManager.HUNTER_UID, getItemStrings(Arrays.asList(
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

		backpackAcceptedItemDefaults.put(BackpackManager.BUILDER_UID, getItemStrings(Arrays.asList(
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

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			BlockRegistryApiculture beeBlocks = ModuleApiculture.getBlocks();
			backpackAcceptedItemDefaults.get(BackpackManager.BUILDER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(beeBlocks.candle, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(beeBlocks.stump, 1, OreDictionary.WILDCARD_VALUE)
			)));
		}

		// include everything added via the API
		BackpackInterface backpackInterface = (BackpackInterface) BackpackManager.backpackInterface;
		backpackAcceptedItemDefaults.putAll(backpackInterface.getBackpackAcceptedItems());
	}

	private static List<String> getItemStrings(List<ItemStack> itemStacks) {
		List<String> itemStrings = new ArrayList<>(itemStacks.size());
		for (ItemStack itemStack : itemStacks) {
			String itemString = ItemStackUtil.getStringForItemStack(itemStack);
			if (itemString != null) {
				itemStrings.add(itemString);
			}
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
				String[] defaultValidItems = new String[0];
				List<String> defaultAcceptedItemNames = backpackAcceptedItemDefaults.get(backpackUid);
				if (defaultAcceptedItemNames != null) {
					Collections.sort(defaultAcceptedItemNames);
					defaultValidItems = defaultAcceptedItemNames.toArray(new String[0]);
				}

				Property backpackConf = config.get("backpacks." + backpackUid, "item.stacks.accepted", defaultValidItems);
				backpackConf.setComment(Translator.translateToLocalFormatted("for.config.backpacks.item.stacks.format", backpackUid));

				String[] backpackItemList = backpackConf.getStringList();
				List<ItemStack> backpackItems = ItemStackUtil.parseItemStackStrings(backpackItemList, OreDictionary.WILDCARD_VALUE);
				for (ItemStack backpackItem : backpackItems) {
					backpackFilter.acceptItem(backpackItem);
				}
			}

			// accepted oreDict
			{
				String[] defaultOreRegexpNames = new String[0];
				List<String> defaultOreRegexpList = backpackAcceptedOreDictRegexpDefaults.get(backpackUid);
				if (defaultOreRegexpList != null) {
					Collections.sort(defaultOreRegexpList);
					defaultOreRegexpNames = defaultOreRegexpList.toArray(new String[0]);
				}

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
		}
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
		ItemRegistryBackpacks items = getItems();
		if (items.apiaristBackpack != null && ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			BlockRegistryApiculture beeBlocks = ModuleApiculture.getBlocks();
			addBackpackRecipe("bee", items.apiaristBackpack, "stickWood", beeBlocks.beeChest);
		}

		if (items.lepidopteristBackpack != null && ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			BlockRegistryLepidopterology butterflyBlocks = ModuleLepidopterology.getBlocks();
			ItemStack chest = new ItemStack(butterflyBlocks.butterflyChest);
			addBackpackRecipe("butterfly", items.lepidopteristBackpack, "stickWood", chest);
		}

		addBackpackRecipe("mining", items.minerBackpack, "ingotIron");
		addBackpackRecipe("digging", items.diggerBackpack, "stone");
		addBackpackRecipe("foresting", items.foresterBackpack, "logWood");
		addBackpackRecipe("hunting", items.hunterBackpack, Items.FEATHER);
		addBackpackRecipe("adventuring", items.adventurerBackpack, Items.BONE);
		addBackpackRecipe("building", items.builderBackpack, Items.CLAY_BALL);

		// / CARPENTER
		if (ModuleHelper.isEnabled(ForestryModuleUids.FACTORY)) {
			// / BACKPACKS WOVEN
			addT2BackpackRecipe(items.minerBackpack, items.minerBackpackT2);
			addT2BackpackRecipe(items.diggerBackpack, items.diggerBackpackT2);
			addT2BackpackRecipe(items.foresterBackpack, items.foresterBackpackT2);
			addT2BackpackRecipe(items.hunterBackpack, items.hunterBackpackT2);
			addT2BackpackRecipe(items.adventurerBackpack, items.adventurerBackpackT2);
			addT2BackpackRecipe(items.builderBackpack, items.builderBackpackT2);
		}
	}

	private static void addBackpackRecipe(String recipeName, Item backpack, Object material) {
		addBackpackRecipe(recipeName, backpack, material, "chestWood");
	}

	private static void addBackpackRecipe(String recipeName, Item backpack, Object material, Object chest) {
		RecipeUtil.addRecipe("backpack_" + recipeName, backpack,
			"X#X",
			"VYV",
			"X#X",
			'#', Blocks.WOOL,
			'X', Items.STRING,
			'V', material,
			'Y', chest);
	}

	private static void addT2BackpackRecipe(Item backpackT1, Item backpackT2) {
		ItemRegistryCore coreItems = ModuleCore.getItems();

		ItemStack wovenSilk = coreItems.craftingMaterial.getWovenSilk();
		RecipeManagers.carpenterManager.addRecipe(200, new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), ItemStack.EMPTY, new ItemStack(backpackT2),
			"WXW",
			"WTW",
			"WWW",
			'X', "gemDiamond",
			'W', wovenSilk,
			'T', backpackT1);
	}

	@Override
	public void addLootPoolNames(Set<String> lootPoolNames) {
		lootPoolNames.add("forestry_storage_items");
	}
}
