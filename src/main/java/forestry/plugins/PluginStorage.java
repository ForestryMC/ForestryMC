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

import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.IGuiHandler;

import forestry.Forestry;
import forestry.api.core.Tabs;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.StorageManager;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.fluids.Fluids;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.IResupplyHandler;
import forestry.core.items.ItemCrated;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import forestry.factory.gadgets.MachineCarpenter;
import forestry.storage.BackpackDefinition;
import forestry.storage.BackpackHelper;
import forestry.storage.CrateRegistry;
import forestry.storage.GuiHandlerStorage;
import forestry.storage.PickupHandlerStorage;
import forestry.storage.ResupplyHandler;
import forestry.storage.items.ItemNaturalistBackpack;
import forestry.storage.items.ItemNaturalistBackpack.BackpackDefinitionApiarist;
import forestry.storage.items.ItemNaturalistBackpack.BackpackDefinitionLepidopterist;
import forestry.storage.proxy.ProxyStorage;

@Plugin(pluginID = "Storage", name = "Storage", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.storage.description")
public class PluginStorage extends ForestryPlugin {

	private static final List<ItemCrated> crates = new ArrayList<ItemCrated>();
	private static final String CONFIG_CATEGORY = "backpacks";

	@SidedProxy(clientSide = "forestry.storage.proxy.ClientProxyStorage", serverSide = "forestry.storage.proxy.ProxyStorage")
	public static ProxyStorage proxy;
	private final ArrayList<ItemStack> minerItems = new ArrayList<ItemStack>();
	private final ArrayList<ItemStack> diggerItems = new ArrayList<ItemStack>();
	private final ArrayList<ItemStack> foresterItems = new ArrayList<ItemStack>();
	private final ArrayList<ItemStack> hunterItems = new ArrayList<ItemStack>();
	private final ArrayList<ItemStack> adventurerItems = new ArrayList<ItemStack>();
	private final ArrayList<ItemStack> builderItems = new ArrayList<ItemStack>();

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void setupAPI() {
		super.setupAPI();

		StorageManager.crateRegistry = new CrateRegistry();

		BackpackManager.backpackInterface = new BackpackHelper();

		BackpackManager.backpackItems = new ArrayList[6];

		BackpackManager.backpackItems[0] = minerItems;
		BackpackManager.backpackItems[1] = diggerItems;
		BackpackManager.backpackItems[2] = foresterItems;
		BackpackManager.backpackItems[3] = hunterItems;
		BackpackManager.backpackItems[4] = adventurerItems;
		BackpackManager.backpackItems[5] = builderItems;

		BackpackDefinition definition;

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			definition = new BackpackDefinitionApiarist("apiarist", new Color(0xc4923d).getRGB());
			BackpackManager.definitions.put(definition.getKey(), definition);
		}

		if (PluginManager.Module.LEPIDOPTEROLOGY.isEnabled()) {
			definition = new BackpackDefinitionLepidopterist("lepidopterist", new Color(0x995b31).getRGB());
			BackpackManager.definitions.put(definition.getKey(), definition);
		}

		definition = new BackpackDefinition("miner", new Color(0x36187d).getRGB());
		BackpackManager.definitions.put(definition.getKey(), definition);

		definition = new BackpackDefinition("digger", new Color(0x363cc5).getRGB());
		BackpackManager.definitions.put(definition.getKey(), definition);

		definition = new BackpackDefinition("forester", new Color(0x347427).getRGB());
		BackpackManager.definitions.put(definition.getKey(), definition);

		definition = new BackpackDefinition("hunter", new Color(0x412215).getRGB());
		BackpackManager.definitions.put(definition.getKey(), definition);

		definition = new BackpackDefinition("adventurer", new Color(0x7fb8c2).getRGB());
		BackpackManager.definitions.put(definition.getKey(), definition);

		definition = new BackpackDefinition("builder", new Color(0xdd3a3a).getRGB());
		BackpackManager.definitions.put(definition.getKey(), definition);
	}

	@Override
	public void postInit() {
		final String oldConfig = CONFIG_CATEGORY + ".conf";
		final String newConfig = CONFIG_CATEGORY + ".cfg";

		File configFile = new File(Forestry.instance.getConfigFolder(), newConfig);
		if (!configFile.exists()) {
			setDefaultsForConfig();
		}

		File oldConfigFile = new File(Forestry.instance.getConfigFolder(), oldConfig);
		if (oldConfigFile.exists()) {
			loadOldConfig();

			final String oldConfigRenamed = CONFIG_CATEGORY + ".conf.old";
			File oldConfigFileRenamed = new File(Forestry.instance.getConfigFolder(), oldConfigRenamed);
			if (oldConfigFile.renameTo(oldConfigFileRenamed)) {
				Proxies.log.info("Migrated " + CONFIG_CATEGORY + " settings to the new file '" + newConfig + "' and renamed '" + oldConfig + "' to '" + oldConfigRenamed + "'.");
			}
		}

		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");

		handleBackpackConfig(config, "miner");
		handleBackpackConfig(config, "digger");
		handleBackpackConfig(config, "forester");
		handleBackpackConfig(config, "hunter");
		handleBackpackConfig(config, "adventurer");
		handleBackpackConfig(config, "builder");

		config.save();

		BackpackDefinition forester = (BackpackDefinition) BackpackManager.definitions.get("forester");
		forester.addValidBlockClasses(Arrays.<Class>asList(
				IPlantable.class,
				IGrowable.class,
				IShearable.class
		));
		forester.addValidItemClasses(Arrays.<Class>asList(
				IPlantable.class,
				IGrowable.class
		));

		BackpackDefinition builder = (BackpackDefinition) BackpackManager.definitions.get("builder");
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

	private static void loadOldConfig() {

		final forestry.core.config.deprecated.Configuration config = new forestry.core.config.deprecated.Configuration();

		forestry.core.config.deprecated.Property backpackConf = config.get("backpacks.miner.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add additional blocks and items for the miner's backpack here in the format modid:name:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		old_parseBackpackItems(backpackConf.value, BackpackManager.definitions.get("miner"));
		backpackConf = config.get("backpacks.digger.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add additional blocks and items for the digger's backpack here in the format modid:name:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		old_parseBackpackItems(backpackConf.value, BackpackManager.definitions.get("digger"));
		backpackConf = config.get("backpacks.forester.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add additional blocks and items for the forester's backpack here in the format modid:name:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		old_parseBackpackItems(backpackConf.value, BackpackManager.definitions.get("forester"));
		backpackConf = config.get("backpacks.hunter.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add additional blocks and items for the hunter's backpack here in the format modid:name:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		old_parseBackpackItems(backpackConf.value, BackpackManager.definitions.get("hunter"));
		backpackConf = config.get("backpacks.adventurer.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add blocks and items for the adventurer's backpack here in the format modid:name:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		old_parseBackpackItems(backpackConf.value, BackpackManager.definitions.get("adventurer"));
		backpackConf = config.get("backpacks.builder.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add blocks and items for the builder's backpack here in the format modid:name:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		old_parseBackpackItems(backpackConf.value, BackpackManager.definitions.get("builder"));
	}

	private static void old_parseBackpackItems(String list, IBackpackDefinition backpackDefinition) {
		List<ItemStack> backpackItems = StackUtils.parseItemStackStrings(list, 0);
		backpackDefinition.addValidItems(backpackItems);
	}

	private void setDefaultsForConfig() {

		final BackpackDefinition miner = (BackpackDefinition) BackpackManager.definitions.get("miner");
		final BackpackDefinition digger = (BackpackDefinition) BackpackManager.definitions.get("digger");
		final BackpackDefinition forester = (BackpackDefinition) BackpackManager.definitions.get("forester");
		final BackpackDefinition adventurer = (BackpackDefinition) BackpackManager.definitions.get("adventurer");
		final BackpackDefinition builder = (BackpackDefinition) BackpackManager.definitions.get("builder");
		final BackpackDefinition hunter = (BackpackDefinition) BackpackManager.definitions.get("hunter");

		miner.addValidItems(minerItems);
		digger.addValidItems(diggerItems);
		forester.addValidItems(foresterItems);
		hunter.addValidItems(hunterItems);
		adventurer.addValidItems(adventurerItems);
		builder.addValidItems(builderItems);

		final Pattern minerOreDictPattern = Pattern.compile("(ore|dust|gem|ingot|nugget|crushed|cluster|denseore)[A-Z].*");
		final Pattern diggerOreDictPattern = Pattern.compile("(stone)[A-Z].*");
		final Pattern foresterOreDictPattern = Pattern.compile("(crop|seed|tree)[A-Z].*");
		final Pattern builderOreDictPattern = Pattern.compile("(block|paneGlass|slabWood|stainedClay|stainedGlass)[A-Z].*");

		final List<String> minerOreDictNames = new ArrayList<String>();
		final List<String> diggerOreDictNames = new ArrayList<String>(Arrays.asList(
				"cobblestone",
				"stone",
				"sand"
		));
		final List<String> foresterOreDictNames = new ArrayList<String>(Arrays.asList(
				"logWood",
				"stickWood",
				"woodStick",
				"saplingTree"
		));
		final List<String> builderOreDictNames = new ArrayList<String>(Arrays.asList(
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
				ForestryItem.bronzePickaxe.getItemStack(),
				ForestryItem.kitPickaxe.getItemStack(),
				ForestryItem.brokenBronzePickaxe.getItemStack()
		));

		digger.addValidOreDictNames(diggerOreDictNames);
		digger.addValidItems(Arrays.asList(
				new ItemStack(Blocks.dirt, 1, Defaults.WILDCARD),
				new ItemStack(Blocks.gravel),
				new ItemStack(Items.flint),
				new ItemStack(Blocks.netherrack),
				new ItemStack(Blocks.sandstone, 1, 0),
				new ItemStack(Items.clay_ball),
				new ItemStack(Blocks.soul_sand),
				ForestryItem.bronzeShovel.getItemStack(),
				ForestryItem.kitShovel.getItemStack(),
				ForestryItem.brokenBronzeShovel.getItemStack()
		));

		forester.addValidOreDictNames(foresterOreDictNames);
		forester.addValidItems(Arrays.asList(
				new ItemStack(Blocks.red_mushroom),
				new ItemStack(Blocks.brown_mushroom),
				new ItemStack(Blocks.red_flower),
				new ItemStack(Blocks.yellow_flower),
				new ItemStack(Blocks.cactus),
				new ItemStack(Blocks.tallgrass, 1, Defaults.WILDCARD),
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
				new ItemStack(Blocks.wool, 1, Defaults.WILDCARD),
				new ItemStack(Items.ender_eye),
				new ItemStack(Items.magma_cream),
				new ItemStack(Items.speckled_melon),
				new ItemStack(Items.fish),
				new ItemStack(Items.cooked_fished),
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
				new ItemStack(Blocks.stonebrick, 1, Defaults.WILDCARD),
				new ItemStack(Blocks.sandstone, 1, 1),
				new ItemStack(Blocks.sandstone, 1, 2),
				new ItemStack(Blocks.brick_block),
				new ItemStack(Blocks.clay),
				new ItemStack(Blocks.hardened_clay, 1, Defaults.WILDCARD),
				new ItemStack(Blocks.stained_hardened_clay, 1, Defaults.WILDCARD),
				new ItemStack(Blocks.packed_ice),
				new ItemStack(Blocks.nether_brick),
				new ItemStack(Blocks.crafting_table),
				new ItemStack(Blocks.furnace),
				new ItemStack(Blocks.lever),
				new ItemStack(Blocks.dispenser),
				new ItemStack(Blocks.dropper),
				new ItemStack(Blocks.ladder),
				new ItemStack(Blocks.iron_bars),
				new ItemStack(Blocks.quartz_block, 1, Defaults.WILDCARD),
				new ItemStack(Items.sign),
				new ItemStack(Items.item_frame)
		));

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			builder.addValidItem(ForestryBlock.candle.getWildcard());
			builder.addValidItem(ForestryBlock.stump.getWildcard());
		}
	}

	private static void handleBackpackConfig(LocalizedConfiguration config, String backpackName) {
		BackpackDefinition backpackDefinition = (BackpackDefinition) BackpackManager.definitions.get(backpackName);

		List<ItemStack> backpackItems;
		List<String> backpackOreDict = new ArrayList<String>();

		{
			List<String> validItems = new ArrayList<String>(backpackDefinition.getValidItemStacks());
			Collections.sort(validItems);
			String[] defaultValidItems = validItems.toArray(new String[validItems.size()]);

			Property backpackConf = config.get("backpacks." + backpackName, "item.stacks", defaultValidItems);
			backpackConf.comment = StringUtil.localizeAndFormat("config.backpacks.item.stacks.format", backpackName);

			String[] backpackItemList = backpackConf.getStringList();
			backpackItems = StackUtils.parseItemStackStrings(backpackItemList, OreDictionary.WILDCARD_VALUE);
		}

		{
			List<Integer> oreIds = new ArrayList<Integer>(backpackDefinition.getValidOreIds());
			String[] defaultOreNames = new String[oreIds.size()];
			for (int i = 0; i < oreIds.size(); i++) {
				int oreId = oreIds.get(i);
				defaultOreNames[i] = OreDictionary.getOreName(oreId);
			}

			List<String> defaultOreNamesList = new ArrayList<String>();
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
		proxy.registerCrateForRendering(crate);
		crates.add(crate);
	}

	public static void createCrateRecipes() {
		MachineCarpenter.RecipeManager carpenterManager = (MachineCarpenter.RecipeManager) RecipeManagers.carpenterManager;
		for (ItemCrated crate : crates) {
			ItemStack itemStack = new ItemStack(crate);
			if (crate.usesOreDict()) {
				carpenterManager.addCratingWithOreDict(itemStack);
			} else {
				carpenterManager.addCrating(itemStack);
			}
		}
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-backpack-items")) {
			String[] tokens = message.getStringValue().split("@");
			if (tokens.length != 2) {
				logInvalidIMCMessage(message);
				return true;
			}

			if (!BackpackManager.definitions.containsKey(tokens[0])) {
				String errorMessage = getInvalidIMCMessageText(message);
				Proxies.log.warning("%s For non-existent backpack %s.", errorMessage, tokens[0]);
				return true;
			}

			IBackpackDefinition backpackDefinition = BackpackManager.definitions.get(tokens[0]);
			List<ItemStack> itemStacks = StackUtils.parseItemStackStrings(tokens[1], 0);
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
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerStorage();
	}

	@Override
	public IResupplyHandler getResupplyHandler() {
		return new ResupplyHandler();
	}

	@Override
	protected void registerItems() {
		// CRATE
		ForestryItem.crate.registerItem((new ItemCrated(null, false)), "crate");

		// BACKPACKS
		IBackpackDefinition definition;

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			definition = BackpackManager.definitions.get("apiarist");
			Item backpack = new ItemNaturalistBackpack(GuiId.ApiaristBackpackGUI.ordinal(), definition).setCreativeTab(Tabs.tabApiculture);
			ForestryItem.apiaristBackpack.registerItem(backpack, "apiaristBag");
		}

		if (PluginManager.Module.LEPIDOPTEROLOGY.isEnabled()) {
			definition = BackpackManager.definitions.get("lepidopterist");
			Item backpack = new ItemNaturalistBackpack(GuiId.LepidopteristBackpackGUI.ordinal(), definition).setCreativeTab(Tabs.tabLepidopterology);
			ForestryItem.lepidopteristBackpack.registerItem(backpack, "lepidopteristBag");
		}

		definition = BackpackManager.definitions.get("miner");
		ForestryItem.minerBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "minerBag");
		ForestryItem.minerBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "minerBagT2");

		definition = BackpackManager.definitions.get("digger");
		ForestryItem.diggerBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "diggerBag");
		ForestryItem.diggerBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "diggerBagT2");

		definition = BackpackManager.definitions.get("forester");
		ForestryItem.foresterBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "foresterBag");
		ForestryItem.foresterBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "foresterBagT2");

		definition = BackpackManager.definitions.get("hunter");
		ForestryItem.hunterBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "hunterBag");
		ForestryItem.hunterBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "hunterBagT2");

		definition = BackpackManager.definitions.get("adventurer");
		ForestryItem.adventurerBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "adventurerBag");
		ForestryItem.adventurerBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "adventurerBagT2");

		definition = BackpackManager.definitions.get("builder");
		ForestryItem.builderBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "builderBag");
		ForestryItem.builderBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "builderBagT2");
	}

	@Override
	protected void registerRecipes() {

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			// Apiarist's Backpack
			Proxies.common.addRecipe(ForestryItem.apiaristBackpack.getItemStack(),
					"X#X",
					"VYV",
					"X#X",
					'#', Blocks.wool,
					'X', Items.string,
					'V', "stickWood",
					'Y', ForestryBlock.apiculture.getItemStack(1, Defaults.DEFINITION_APIARISTCHEST_META));
		}

		if (PluginManager.Module.LEPIDOPTEROLOGY.isEnabled()) {
			// Lepidopterist's Backpack
			Proxies.common.addRecipe(ForestryItem.lepidopteristBackpack.getItemStack(),
					"X#X",
					"VYV",
					"X#X",
					'#', Blocks.wool,
					'X', Items.string,
					'V', "stickWood",
					'Y', ForestryBlock.lepidopterology.getItemStack(1, Defaults.DEFINITION_LEPICHEST_META));
		}

		// Miner's Backpack
		Proxies.common.addRecipe(ForestryItem.minerBackpack.getItemStack(),
				"X#X", "VYV", "X#X", '#', Blocks.wool, 'X', Items.string, 'V',
				Items.iron_ingot, 'Y', Blocks.chest);
		// Digger's Backpack
		Proxies.common.addRecipe(ForestryItem.diggerBackpack.getItemStack(),
				"X#X", "VYV", "X#X", '#', Blocks.wool, 'X', Items.string, 'V',
				Blocks.stone, 'Y', Blocks.chest);
		// Forester's Backpack
		Proxies.common.addRecipe(ForestryItem.foresterBackpack.getItemStack(), "X#X", "VYV", "X#X", '#', Blocks.wool,
				'X', Items.string, 'V', "logWood", 'Y', Blocks.chest);
		// Hunter's Backpack
		Proxies.common.addRecipe(ForestryItem.hunterBackpack.getItemStack(),
				"X#X", "VYV", "X#X", '#', Blocks.wool, 'X', Items.string, 'V',
				Items.feather, 'Y', Blocks.chest);
		// Adventurer's Backpack
		Proxies.common.addRecipe(ForestryItem.adventurerBackpack.getItemStack(), "X#X", "VYV", "X#X", '#', Blocks.wool,
				'X', Items.string, 'V', Items.bone, 'Y', Blocks.chest);
		// Builder's Backpack
		Proxies.common.addRecipe(ForestryItem.builderBackpack.getItemStack(), "X#X", "VYV", "X#X", '#', Blocks.wool,
				'X', Items.string, 'V', Items.clay_ball, 'Y', Blocks.chest);

		if (PluginManager.Module.FACTORY.isEnabled()) {
			// / CARPENTER

			// / CRATES
			RecipeManagers.carpenterManager.addRecipe(20, Fluids.WATER.getFluid(1000), null, ForestryItem.crate.getItemStack(24),
					" # ", "# #", " # ", '#', "logWood");

			// / BACKPACKS T2
			RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.getFluid(1000), null, ForestryItem.minerBackpackT2.getItemStack(),
					"WXW", "WTW", "WWW", 'X', Items.diamond, 'W',
					ForestryItem.craftingMaterial.getItemStack(1, 3), 'T', ForestryItem.minerBackpack);
			RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.getFluid(1000), null, ForestryItem.diggerBackpackT2.getItemStack(),
					"WXW", "WTW", "WWW", 'X', Items.diamond, 'W',
					ForestryItem.craftingMaterial.getItemStack(1, 3), 'T', ForestryItem.diggerBackpack);
			RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.getFluid(1000), null, ForestryItem.foresterBackpackT2.getItemStack(),
					"WXW", "WTW", "WWW", 'X', Items.diamond, 'W',
					ForestryItem.craftingMaterial.getItemStack(1, 3), 'T', ForestryItem.foresterBackpack);
			RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.getFluid(1000), null, ForestryItem.hunterBackpackT2.getItemStack(),
					"WXW", "WTW", "WWW", 'X', Items.diamond, 'W',
					ForestryItem.craftingMaterial.getItemStack(1, 3), 'T', ForestryItem.hunterBackpack);
			RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.getFluid(1000), null, ForestryItem.adventurerBackpackT2.getItemStack(),
					"WXW", "WTW", "WWW", 'X', Items.diamond, 'W',
					ForestryItem.craftingMaterial.getItemStack(1, 3), 'T', ForestryItem.adventurerBackpack);
			RecipeManagers.carpenterManager.addRecipe(200, Fluids.WATER.getFluid(1000), null, ForestryItem.builderBackpackT2.getItemStack(),
					"WXW", "WTW", "WWW", 'X', Items.diamond, 'W',
					ForestryItem.craftingMaterial.getItemStack(1, 3), 'T', ForestryItem.builderBackpack);
		}
	}

}
