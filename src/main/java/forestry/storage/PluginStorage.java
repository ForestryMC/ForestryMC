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

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
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
import forestry.core.utils.StringUtil;
import forestry.lepidopterology.PluginLepidopterology;
import forestry.lepidopterology.blocks.BlockRegistryLepidopterology;
import forestry.lepidopterology.blocks.BlockTypeLepidopterologyTesr;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import forestry.storage.BackpackDefinition.BackpackDefinitionApiarist;
import forestry.storage.BackpackDefinition.BackpackDefinitionLepidopterist;
import forestry.storage.items.ItemRegistryStorage;

@ForestryPlugin(pluginID = ForestryPluginUids.STORAGE, name = "Storage", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.storage.description")
public class PluginStorage extends BlankForestryPlugin {

	private static final List<ItemCrated> crates = new ArrayList<>();
	private static final String CONFIG_CATEGORY = "backpacks";

	public static ItemRegistryStorage items;

	@Override
	public void setupAPI() {
		super.setupAPI();

		StorageManager.crateRegistry = new CrateRegistry();

		BackpackManager.backpackInterface = new BackpackInterface();

		BackpackDefinition definition;

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			definition = new BackpackDefinitionApiarist(new Color(0xc4923d));
			BackpackManager.backpackInterface.registerBackpack("apiarist", definition);
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
			definition = new BackpackDefinitionLepidopterist(new Color(0x995b31));
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
		if (!configFile.exists()) {
			setDefaultsForConfig();
		}

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");

		handleBackpackConfig(config, BackpackManager.MINER_UID);
		handleBackpackConfig(config, BackpackManager.DIGGER_UID);
		handleBackpackConfig(config, BackpackManager.FORESTER_UID);
		handleBackpackConfig(config, BackpackManager.HUNTER_UID);
		handleBackpackConfig(config, BackpackManager.ADVENTURER_UID);
		handleBackpackConfig(config, BackpackManager.BUILDER_UID);

		config.save();

		BackpackDefinition forester = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(BackpackManager.FORESTER_UID);
		forester.addValidBlockClasses(Arrays.<Class>asList(
				IPlantable.class,
				IGrowable.class,
				IShearable.class
		));
		forester.addValidItemClasses(Arrays.<Class>asList(
				IPlantable.class,
				IGrowable.class
		));

		BackpackDefinition builder = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(BackpackManager.BUILDER_UID);
		builder.addValidBlockClasses(Arrays.<Class>asList(
				BlockStairs.class,
				BlockFence.class,
				BlockFenceGate.class,
				BlockWall.class,
				BlockBasePressurePlate.class,
				BlockLever.class,
				BlockButton.class,
				BlockTorch.class,
				BlockRedstoneDiode.class,
				BlockChest.class,
				BlockWorkbench.class,
				BlockFurnace.class,
				BlockLadder.class,
				BlockTrapDoor.class,
				BlockDoor.class,
				BlockSlab.class
		));
		builder.addValidItemClass(ItemDoor.class);
	}

	private static void setDefaultsForConfig() {

		final BackpackDefinition miner = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(BackpackManager.MINER_UID);
		final BackpackDefinition digger = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(BackpackManager.DIGGER_UID);
		final BackpackDefinition forester = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(BackpackManager.FORESTER_UID);
		final BackpackDefinition adventurer = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(BackpackManager.ADVENTURER_UID);
		final BackpackDefinition builder = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(BackpackManager.BUILDER_UID);
		final BackpackDefinition hunter = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(BackpackManager.HUNTER_UID);

		final Pattern minerOreDictPattern = Pattern.compile("(ore|dust|gem|ingot|nugget|crushed|cluster|denseore)[A-Z].*");
		final Pattern diggerOreDictPattern = Pattern.compile("(stone)[A-Z].*");
		final Pattern foresterOreDictPattern = Pattern.compile("(crop|seed|tree)[A-Z].*");
		final Pattern builderOreDictPattern = Pattern.compile("(block|paneGlass|slabWood|stainedClay|stainedGlass)[A-Z].*");

		final List<String> minerOreDictNames = new ArrayList<>();
		final List<String> diggerOreDictNames = new ArrayList<>(Arrays.asList(
				"cobblestone",
				"stone",
				"sand"
		));
		final List<String> foresterOreDictNames = new ArrayList<>(Arrays.asList(
				"logWood",
				"stickWood",
				"woodStick",
				"saplingTree"
		));
		final List<String> builderOreDictNames = new ArrayList<>(Arrays.asList(
				"stone",
				"plankWood",
				"stairWood",
				"slabWood",
				"fenceWood",
				"glass",
				"blockGlass",
				"paneGlass"
		));

		for (String name : OreDictionary.getOreNames()) {
			if (minerOreDictPattern.matcher(name).matches()) {
				minerOreDictNames.add(name);
			} else if (diggerOreDictPattern.matcher(name).matches()) {
				if (name.equals("stoneRod")) {
					continue;
				}
				diggerOreDictNames.add(name);
			} else if (foresterOreDictPattern.matcher(name).matches()) {
				foresterOreDictNames.add(name);
			} else if (builderOreDictPattern.matcher(name).matches()) {
				if (name.equals("blockHopper")) {
					continue;
				}
				builderOreDictNames.add(name);
			}
		}

		miner.addValidOreDictNames(minerOreDictNames);
		miner.addValidItems(Arrays.asList(
				new ItemStack(Blocks.obsidian),
				new ItemStack(Blocks.coal_ore),
				new ItemStack(Items.coal),
				PluginCore.items.bronzePickaxe.getItemStack(),
				PluginCore.items.kitPickaxe.getItemStack(),
				PluginCore.items.brokenBronzePickaxe.getItemStack()
		));

		digger.addValidOreDictNames(diggerOreDictNames);
		digger.addValidItems(Arrays.asList(
				new ItemStack(Blocks.dirt, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.gravel),
				new ItemStack(Items.flint),
				new ItemStack(Blocks.netherrack),
				new ItemStack(Blocks.sandstone, 1, 0),
				new ItemStack(Items.clay_ball),
				new ItemStack(Blocks.soul_sand),
				PluginCore.items.bronzeShovel.getItemStack(),
				PluginCore.items.kitShovel.getItemStack(),
				PluginCore.items.brokenBronzeShovel.getItemStack()
		));

		forester.addValidOreDictNames(foresterOreDictNames);
		forester.addValidItems(Arrays.asList(
				new ItemStack(Blocks.red_mushroom),
				new ItemStack(Blocks.brown_mushroom),
				new ItemStack(Blocks.red_flower),
				new ItemStack(Blocks.yellow_flower),
				new ItemStack(Blocks.cactus),
				new ItemStack(Blocks.tallgrass, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.vine),
				new ItemStack(Blocks.pumpkin),
				new ItemStack(Blocks.melon_block),
				new ItemStack(Items.golden_apple),
				new ItemStack(Items.nether_wart),
				new ItemStack(Items.pumpkin_seeds),
				new ItemStack(Items.melon_seeds)
		));

		hunter.addValidItems(Arrays.asList(
				new ItemStack(Items.feather),
				new ItemStack(Items.gunpowder),
				new ItemStack(Items.blaze_powder),
				new ItemStack(Items.blaze_rod),
				new ItemStack(Items.bone),
				new ItemStack(Items.string),
				new ItemStack(Items.rotten_flesh),
				new ItemStack(Items.ghast_tear),
				new ItemStack(Items.gold_nugget),
				new ItemStack(Items.arrow),
				new ItemStack(Items.porkchop),
				new ItemStack(Items.cooked_porkchop),
				new ItemStack(Items.beef),
				new ItemStack(Items.cooked_beef),
				new ItemStack(Items.chicken),
				new ItemStack(Items.cooked_chicken),
				new ItemStack(Items.leather),
				new ItemStack(Items.egg),
				new ItemStack(Items.ender_pearl),
				new ItemStack(Items.spider_eye),
				new ItemStack(Items.fermented_spider_eye),
				new ItemStack(Items.slime_ball),
				new ItemStack(Items.dye, 1, 0),
				new ItemStack(Blocks.hay_block),
				new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.ender_eye),
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
		));

		builder.addValidOreDictNames(builderOreDictNames);
		builder.addValidItems(Arrays.asList(
				new ItemStack(Blocks.torch),
				new ItemStack(Blocks.redstone_torch),
				new ItemStack(Blocks.redstone_lamp),
				new ItemStack(Blocks.stonebrick, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.sandstone, 1, 1),
				new ItemStack(Blocks.sandstone, 1, 2),
				new ItemStack(Blocks.brick_block),
				new ItemStack(Blocks.clay),
				new ItemStack(Blocks.hardened_clay, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.stained_hardened_clay, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Blocks.packed_ice),
				new ItemStack(Blocks.nether_brick),
				new ItemStack(Blocks.crafting_table),
				new ItemStack(Blocks.furnace),
				new ItemStack(Blocks.lever),
				new ItemStack(Blocks.dispenser),
				new ItemStack(Blocks.dropper),
				new ItemStack(Blocks.ladder),
				new ItemStack(Blocks.iron_bars),
				new ItemStack(Blocks.quartz_block, 1, OreDictionary.WILDCARD_VALUE),
				new ItemStack(Items.sign),
				new ItemStack(Items.item_frame)
		));

		BlockRegistryApiculture beeBlocks = PluginApiculture.blocks;
		if (beeBlocks != null) {
			builder.addValidItem(new ItemStack(beeBlocks.candle, 1, OreDictionary.WILDCARD_VALUE));
			builder.addValidItem(new ItemStack(beeBlocks.stump, 1, OreDictionary.WILDCARD_VALUE));
		}
	}

	private static void handleBackpackConfig(LocalizedConfiguration config, String backpackName) {
		BackpackDefinition backpackDefinition = (BackpackDefinition) BackpackManager.backpackInterface.getBackpack(backpackName);

		List<ItemStack> backpackItems;
		List<String> backpackOreDict = new ArrayList<>();

		{
			List<String> validItems = new ArrayList<>(backpackDefinition.getValidItemStacks());
			Collections.sort(validItems);
			String[] defaultValidItems = validItems.toArray(new String[validItems.size()]);

			Property backpackConf = config.get("backpacks." + backpackName, "item.stacks", defaultValidItems);
			backpackConf.comment = StringUtil.localizeAndFormat("config.backpacks.item.stacks.format", backpackName);

			String[] backpackItemList = backpackConf.getStringList();
			backpackItems = ItemStackUtil.parseItemStackStrings(backpackItemList, OreDictionary.WILDCARD_VALUE);
		}

		{
			List<Integer> oreIds = new ArrayList<>(backpackDefinition.getValidOreIds());
			String[] defaultOreNames = new String[oreIds.size()];
			for (int i = 0; i < oreIds.size(); i++) {
				int oreId = oreIds.get(i);
				defaultOreNames[i] = OreDictionary.getOreName(oreId);
			}

			List<String> defaultOreNamesList = new ArrayList<>();
			Collections.addAll(defaultOreNamesList, defaultOreNames);
			Collections.sort(defaultOreNamesList);
			defaultOreNames = defaultOreNamesList.toArray(new String[defaultOreNamesList.size()]);

			Property backpackConf = config.get("backpacks." + backpackName, "ore.dict", defaultOreNames);
			backpackConf.comment = StringUtil.localizeAndFormat("config.backpacks.ore.dict.format", backpackName);

			String[] oreDictNameList = backpackConf.getStringList();
			Collections.addAll(backpackOreDict, oreDictNameList);
		}

		backpackDefinition.clearAllValid();

		backpackDefinition.addValidItems(backpackItems);
		backpackDefinition.addValidOreDictNames(backpackOreDict);
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
		addBackpackRecipe(items.adventurerBackpack, Items.bone);
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
				'X', Items.string,
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
