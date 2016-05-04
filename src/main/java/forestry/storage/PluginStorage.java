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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import forestry.Forestry;
import forestry.api.core.ForestryAPI;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.StorageManager;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.blocks.BlockRegistryApiculture;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.PluginCore;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.fluids.Fluids;
import forestry.core.items.ItemCrated;
import forestry.core.models.ModelCrate;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.lepidopterology.PluginLepidopterology;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;
import forestry.lepidopterology.blocks.BlockTypeLepidopterologyTesr;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import forestry.storage.items.ItemRegistryStorage;

@ForestryPlugin(pluginID = ForestryPluginUids.STORAGE, name = "Storage", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.storage.description")
public class PluginStorage extends BlankForestryPlugin {

	private static final List<ItemCrated> crates = new ArrayList<>();
	private static final String CONFIG_CATEGORY = "backpacks";

	public static ItemRegistryStorage items;

	private final Multimap<String, String> backpackOreDictRegexpDefaults = HashMultimap.create();
	private final Multimap<String, String> backpackItemDefaults = HashMultimap.create();

	@Override
	public void setupAPI() {
		super.setupAPI();

		StorageManager.crateRegistry = new CrateRegistry();

		BackpackManager.backpackInterface = new BackpackInterface();

		BackpackDefinition definition;

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			definition = new BackpackDefinition.BackpackDefinitionNaturalist(new Color(0xc4923d), "rootBees");
			BackpackManager.backpackInterface.registerBackpack("apiarist", definition);
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			definition = new BackpackDefinition.BackpackDefinitionNaturalist(new Color(0x995b31), "rootButterflies");
			BackpackManager.backpackInterface.registerBackpack("lepidopterist", definition);
		}

		definition = new BackpackDefinition(new Color(0x36187d));
		BackpackManager.backpackInterface.registerBackpack(BackpackManager.MINER_UID, definition);

		definition = new BackpackDefinition(new Color(0x363cc5));
		BackpackManager.backpackInterface.registerBackpack(BackpackManager.DIGGER_UID, definition);

		definition = new BackpackDefinition(new Color(0x347427));
		BackpackManager.backpackInterface.registerBackpack(BackpackManager.FORESTER_UID, definition);

		definition = new BackpackDefinition(new Color(0x412215));
		BackpackManager.backpackInterface.registerBackpack(BackpackManager.HUNTER_UID, definition);

		definition = new BackpackDefinition(new Color(0x7fb8c2));
		BackpackManager.backpackInterface.registerBackpack(BackpackManager.ADVENTURER_UID, definition);

		definition = new BackpackDefinition(new Color(0xdd3a3a));
		BackpackManager.backpackInterface.registerBackpack(BackpackManager.BUILDER_UID, definition);
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryStorage();
	}
	
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postInit() {
		final String newConfig = CONFIG_CATEGORY + ".cfg";

		File configFile = new File(Forestry.instance.getConfigFolder(), newConfig);
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");

		setDefaultsForConfig();

		handleBackpackConfig(config, BackpackManager.MINER_UID);
		handleBackpackConfig(config, BackpackManager.DIGGER_UID);
		handleBackpackConfig(config, BackpackManager.FORESTER_UID);
		handleBackpackConfig(config, BackpackManager.HUNTER_UID);
		handleBackpackConfig(config, BackpackManager.ADVENTURER_UID);
		handleBackpackConfig(config, BackpackManager.BUILDER_UID);

		config.save();
	}

	private void setDefaultsForConfig() {
		backpackOreDictRegexpDefaults.get(BackpackManager.MINER_UID).addAll(Arrays.asList(
				"ore[A-Z].*",
				"dust[A-Z].*",
				"gem[A-Z].*",
				"ingot[A-Z].*",
				"nugget[A-Z].*",
				"crushed[A-Z].*",
				"cluster[A-Z].*",
				"denseore[A-Z].*"
		));

		backpackOreDictRegexpDefaults.get(BackpackManager.DIGGER_UID).addAll(Arrays.asList(
				"cobblestone",
				"stone",
				"stone[A-Z].*",
				"sand"
		));

		backpackOreDictRegexpDefaults.get(BackpackManager.FORESTER_UID).addAll(Arrays.asList(
				"logWood",
				"stickWood",
				"woodStick",
				"saplingTree",
				"vine",
				"crop[A-Z].*",
				"seed[A-Z].*",
				"tree[A-Z].*"
		));

		backpackOreDictRegexpDefaults.get(BackpackManager.BUILDER_UID).addAll(Arrays.asList(
				"block[A-Z].*",
				"paneGlass[A-Z].*",
				"slabWood[A-Z].*",
				"stainedClay[A-Z].*",
				"stainedGlass[A-Z].*",
				"stone",
				"plankWood",
				"stairWood",
				"slabWood",
				"fenceWood",
				"fenceGateWood",
				"glass",
				"paneGlass",
				"torch",
				"chest",
				"chest[A-Z].*",
				"workbench",
				"doorWood"
		));

		backpackItemDefaults.get(BackpackManager.MINER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Blocks.OBSIDIAN),
				new ItemStack(Blocks.coal_ore),
				new ItemStack(Items.coal),
				PluginCore.items.bronzePickaxe.getItemStack(),
				PluginCore.items.kitPickaxe.getItemStack(),
				PluginCore.items.brokenBronzePickaxe.getItemStack()
		)));

		backpackItemDefaults.get(BackpackManager.DIGGER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Blocks.DIRT, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.gravel),
				new ItemStack(Items.flint),
				new ItemStack(Blocks.netherrack),
				new ItemStack(Blocks.SANDSTONE, 1, 0),
				new ItemStack(Items.clay_ball),
				new ItemStack(Blocks.soul_sand),
				PluginCore.items.bronzeShovel.getItemStack(),
				PluginCore.items.kitShovel.getItemStack(),
				PluginCore.items.brokenBronzeShovel.getItemStack()
		)));

		backpackItemDefaults.get(BackpackManager.FORESTER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Blocks.RED_MUSHROOM),
				new ItemStack(Blocks.BROWN_MUSHROOM),
				new ItemStack(Blocks.red_flower),
				new ItemStack(Blocks.yellow_flower),
				new ItemStack(Blocks.CACTUS),
				new ItemStack(Blocks.tallgrass, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.pumpkin),
				new ItemStack(Blocks.melon_block),
				new ItemStack(Items.golden_apple),
				new ItemStack(Items.nether_wart),
				new ItemStack(Items.PUMPKIN_SEEDS),
				new ItemStack(Items.MELON_SEEDS)
		)));

		backpackItemDefaults.get(BackpackManager.HUNTER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Items.feather),
				new ItemStack(Items.GUNPOWDER),
				new ItemStack(Items.BLAZE_POWDER),
				new ItemStack(Items.BLAZE_ROD),
				new ItemStack(Items.BONE),
				new ItemStack(Items.STRING),
				new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.GHAST_TEAR),
				new ItemStack(Items.gold_nugget),
				new ItemStack(Items.ARROW),
				new ItemStack(Items.porkchop),
				new ItemStack(Items.cooked_porkchop),
				new ItemStack(Items.beef),
				new ItemStack(Items.cooked_beef),
				new ItemStack(Items.chicken),
				new ItemStack(Items.cooked_chicken),
				new ItemStack(Items.leather),
				new ItemStack(Items.egg),
				new ItemStack(Items.ENDER_PEARL),
				new ItemStack(Items.SPIDER_EYE),
				new ItemStack(Items.fermented_spider_eye),
				new ItemStack(Items.slime_ball),
				new ItemStack(Items.DYE, 1, 0),
				new ItemStack(Blocks.hay_block),
				new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.ENDER_EYE),
				new ItemStack(Items.magma_cream),
				new ItemStack(Items.speckled_melon),
				new ItemStack(Items.fish),
				new ItemStack(Items.cooked_fish),
				new ItemStack(Items.lead),
				new ItemStack(Items.fishing_rod),
				new ItemStack(Items.name_tag),
				new ItemStack(Items.saddle),
				new ItemStack(Items.diamond_horse_armor),
				new ItemStack(Items.golden_horse_armor),
				new ItemStack(Items.iron_horse_armor)
		)));

		backpackItemDefaults.get(BackpackManager.BUILDER_UID).addAll(getItemStrings(Arrays.asList(
				new ItemStack(Blocks.redstone_torch),
				new ItemStack(Blocks.redstone_lamp),
				new ItemStack(Blocks.STONEBRICK, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.SANDSTONE, 1, 1),
				new ItemStack(Blocks.SANDSTONE, 1, 2),
				new ItemStack(Blocks.BRICK_BLOCK),
				new ItemStack(Blocks.clay),
				new ItemStack(Blocks.hardened_clay, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.stained_hardened_clay, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.packed_ice),
				new ItemStack(Blocks.NETHER_BRICK),
				new ItemStack(Blocks.crafting_table),
				new ItemStack(Blocks.furnace),
				new ItemStack(Blocks.lever),
				new ItemStack(Blocks.dispenser),
				new ItemStack(Blocks.dropper),
				new ItemStack(Blocks.ladder),
				new ItemStack(Blocks.iron_bars),
				new ItemStack(Blocks.QUARTZ_BLOCK, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.COBBLESTONE_wall),
				new ItemStack(Blocks.stone_button),
				new ItemStack(Blocks.wooden_button),
				new ItemStack(Blocks.stone_slab),
				new ItemStack(Blocks.wooden_slab),
				new ItemStack(Blocks.trapdoor),
				new ItemStack(Blocks.iron_trapdoor),
				new ItemStack(Blocks.stone_pressure_plate),
				new ItemStack(Blocks.wooden_pressure_plate),
				new ItemStack(Items.sign),
				new ItemStack(Items.item_frame),
				new ItemStack(Items.acacia_door),
				new ItemStack(Items.birch_door),
				new ItemStack(Items.dark_oak_door),
				new ItemStack(Items.iron_door),
				new ItemStack(Items.jungle_door),
				new ItemStack(Items.oak_door),
				new ItemStack(Items.spruce_door)
		)));

		BlockRegistryApiculture beeBlocks = PluginApiculture.blocks;
		if (beeBlocks != null) {
			backpackItemDefaults.get(BackpackManager.BUILDER_UID).addAll(getItemStrings(Arrays.asList(
					new ItemStack(beeBlocks.candle, 1, OreDictionary.WILDCARD_VALUE),
					new ItemStack(beeBlocks.stump, 1, OreDictionary.WILDCARD_VALUE)
			)));
		}
	}

	@Nonnull
	private static Set<String> getItemStrings(List<ItemStack> itemStacks) {
		Set<String> itemStrings = new HashSet<>(itemStacks.size());
		for (ItemStack itemStack : itemStacks) {
			String itemString = ItemStackUtil.getStringForItemStack(itemStack);
			itemStrings.add(itemString);
		}
		return itemStrings;
	}

	private void handleBackpackConfig(LocalizedConfiguration config, String backpackUid) {
		BackpackDefinition backpackDefinition = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(backpackUid);
		backpackDefinition.clearAllValid();

		{
			List<String> defaultItemNames = new ArrayList<>(backpackItemDefaults.get(backpackUid));
			Collections.sort(defaultItemNames);
			String[] defaultValidItems = defaultItemNames.toArray(new String[defaultItemNames.size()]);

			Property backpackConf = config.get("backpacks." + backpackUid, "item.stacks", defaultValidItems);
			backpackConf.comment = Translator.translateToLocalFormatted("for.config.backpacks.item.stacks.format", backpackUid);

			String[] backpackItemList = backpackConf.getStringList();
			List<ItemStack> backpackItems = ItemStackUtil.parseItemStackStrings(backpackItemList, OreDictionary.WILDCARD_VALUE);
			backpackDefinition.addValidItems(backpackItems);
		}

		{

			List<String> defaultOreRegexpList = new ArrayList<>(backpackOreDictRegexpDefaults.get(backpackUid));
			Collections.sort(defaultOreRegexpList);
			String[] defaultOreRegexpNames = defaultOreRegexpList.toArray(new String[defaultOreRegexpList.size()]);

			Property backpackConf = config.get("backpacks." + backpackUid, "ore.dict", defaultOreRegexpNames);
			backpackConf.comment = Translator.translateToLocalFormatted("for.config.backpacks.ore.dict.format", backpackUid);

			List<String> oreDictNameList = new ArrayList<>();
			for (String name : OreDictionary.getOreNames()) {
				for (String regex : backpackConf.getStringList()) {
					if (name.matches(regex)) {
						oreDictNameList.add(name);
					}
				}
			}

			backpackDefinition.addValidOreDictNames(oreDictNameList);
		}
	}

	public static void registerCrate(ItemCrated crate) {
		crates.add(crate);
	}

	public static void createCrateRecipes() {
		for (ItemCrated crate : crates) {
			ItemStack crateStack = new ItemStack(crate);
			ItemStack uncrated = crate.getContained();
			if (crate.usesOreDict()) {
				int[] oreIds = OreDictionary.getOreIDs(uncrated);
				for (int oreId : oreIds) {
					String oreName = OreDictionary.getOreName(oreId);
					addCrating(crateStack, oreName);
				}
			} else {
				addCrating(crateStack, uncrated);
			}
			addUncrating(crateStack, uncrated);
		}
	}

	private static void addCrating(ItemStack crateStack, Object uncrated) {
		FluidStack water = Fluids.WATER.getFluid(Constants.CARPENTER_CRATING_LIQUID_QUANTITY);
		ItemStack box = items.crate.getItemStack();
		RecipeManagers.carpenterManager.addRecipe(Constants.CARPENTER_CRATING_CYCLES, water, box, crateStack, "###", "###", "###", '#', uncrated);
	}

	private static void addUncrating(ItemStack crateStack, ItemStack uncrated) {
		ItemStack product = new ItemStack(uncrated.getItem(), 9, uncrated.getItemDamage());
		RecipeManagers.carpenterManager.addRecipe(Constants.CARPENTER_UNCRATING_CYCLES, null, product, "#", '#', crateStack);
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-backpack-items")) {
			String[] tokens = message.getStringValue().split("@");
			if (tokens.length != 2) {
				IMCUtil.logInvalidIMCMessage(message);
				return true;
			}

			IBackpackDefinition backpackDefinition = BackpackManager.backpackInterface.getBackpack(tokens[0]);
			if (backpackDefinition == null) {
				String errorMessage = IMCUtil.getInvalidIMCMessageText(message);
				Log.warning("%s For non-existent backpack %s.", errorMessage, tokens[0]);
				return true;
			}
			List<ItemStack> itemStacks = ItemStackUtil.parseItemStackStrings(tokens[1], 0);
			backpackDefinition.addValidItems(itemStacks);

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
		BlockRegistryApiculture beeBlocks = PluginApiculture.blocks;
		if (items.apiaristBackpack != null && beeBlocks != null) {
			addBackpackRecipe(items.apiaristBackpack, "stickWood", beeBlocks.apicultureChest);
		}

		BlockRegistryLepidopterology butterflyBlocks = PluginLepidopterology.blocks;
		if (items.lepidopteristBackpack != null && butterflyBlocks != null) {
			ItemStack chest = butterflyBlocks.lepidopterology.get(BlockTypeLepidopterologyTesr.LEPICHEST);
			addBackpackRecipe(items.lepidopteristBackpack, "stickWood", chest);
		}

		addBackpackRecipe(items.minerBackpack, "ingotIron");
		addBackpackRecipe(items.diggerBackpack, "stone");
		addBackpackRecipe(items.foresterBackpack, "logWood");
		addBackpackRecipe(items.hunterBackpack, Items.feather);
		addBackpackRecipe(items.adventurerBackpack, Items.BONE);
		addBackpackRecipe(items.builderBackpack, Items.clay_ball);

		// / CARPENTER
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {
			// / CRATES
			RecipeManagers.carpenterManager.addRecipe(20, Fluids.WATER.getFluid(1000), null, items.crate.getItemStack(24),
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
				'#', Blocks.wool,
				'X', Items.STRING,
				'V', material,
				'Y', chest);
	}

	private static void addT2BackpackRecipe(Item backpackT1, Item backpackT2) {
		ItemStack wovenSilk = PluginCore.items.craftingMaterial.getWovenSilk();
		RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.getFluid(1000), null, new ItemStack(backpackT2),
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
		ModelCrate.initModel(event);
	}
}
