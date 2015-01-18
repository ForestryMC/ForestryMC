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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameData;

import forestry.api.core.Tabs;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.api.storage.EnumBackpackType;
import forestry.api.storage.IBackpackDefinition;
import forestry.api.storage.StorageManager;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.fluids.Fluids;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.IResupplyHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemCrated;
import forestry.core.network.GuiId;
import forestry.core.proxy.Proxies;
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
public class PluginStorage extends ForestryPlugin implements IOreDictionaryHandler {

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
	private Configuration config;

	@Override
	@SuppressWarnings({"unchecked","rawtypes"})
	public void preInit() {
		BackpackManager.backpackInterface = new BackpackHelper();

		BackpackManager.backpackItems = new ArrayList[6];

		BackpackManager.backpackItems[0] = minerItems;
		BackpackManager.backpackItems[1] = diggerItems;
		BackpackManager.backpackItems[2] = foresterItems;
		BackpackManager.backpackItems[3] = hunterItems;
		BackpackManager.backpackItems[4] = adventurerItems;
		BackpackManager.backpackItems[5] = builderItems;

		StorageManager.crateRegistry = new CrateRegistry();
	}

	@Override
	public void postInit() {
		scanForItems();

		config = new Configuration();

		Property backpackConf = config.get("backpacks.miner.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add additional blocks and items for the miner's backpack here in the format id:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		parseBackpackItems("Miner's Backpack", backpackConf.value, BackpackManager.definitions.get("miner"));
		backpackConf = config.get("backpacks.digger.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add additional blocks and items for the digger's backpack here in the format id:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		parseBackpackItems("Digger's Backpack", backpackConf.value, BackpackManager.definitions.get("digger"));
		backpackConf = config.get("backpacks.forester.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add additional blocks and items for the forester's backpack here in the format id:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		parseBackpackItems("Forester's Backpack", backpackConf.value, BackpackManager.definitions.get("forester"));
		backpackConf = config.get("backpacks.hunter.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add additional blocks and items for the hunter's backpack here in the format id:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		parseBackpackItems("Hunter's Backpack", backpackConf.value, BackpackManager.definitions.get("hunter"));
		backpackConf = config.get("backpacks.adventurer.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add blocks and items for the adventurer's backpack here in the format id:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		parseBackpackItems("Adventurer's Backpack", backpackConf.value, BackpackManager.definitions.get("adventurer"));
		backpackConf = config.get("backpacks.builder.items", CONFIG_CATEGORY, "");
		backpackConf.comment = "add blocks and items for the builder's backpack here in the format id:meta. separate blocks and items using ';'. wildcard for metadata: '*'";
		parseBackpackItems("Builder's Backpack", backpackConf.value, BackpackManager.definitions.get("builder"));

		config.save();

		BackpackManager.definitions.get("miner").addValidItems(minerItems);
		BackpackManager.definitions.get("digger").addValidItems(diggerItems);
		BackpackManager.definitions.get("forester").addValidItems(foresterItems);
		BackpackManager.definitions.get("hunter").addValidItems(hunterItems);
		BackpackManager.definitions.get("adventurer").addValidItems(adventurerItems);
		BackpackManager.definitions.get("builder").addValidItems(builderItems);
	}

	public static void registerCrate(ItemCrated crate) {
		proxy.registerCrate(crate);
		crates.add(crate);
	}

	public static void createCrateRecipes() {
		for (ItemCrated crate : crates) {
			ItemStack itemStack = new ItemStack(crate);
			if (crate.usesOreDict()) {
				RecipeManagers.carpenterManager.addCratingWithOreDict(itemStack);
			} else {
				RecipeManagers.carpenterManager.addCrating(itemStack);
			}
		}
	}

	public static void addBackpackItem(String pack, ItemStack stack) {
		if (stack == null)
			return;
		BackpackManager.definitions.get(pack).addValidItem(stack);
	}

	public static void addBackpackItem(String pack, Block block) {
		BackpackManager.definitions.get(pack).addValidItem(new ItemStack(block, 1, Defaults.WILDCARD));
	}

	public static void addBackpackItem(String pack, Item item) {
		BackpackManager.definitions.get(pack).addValidItem(new ItemStack(item, 1, Defaults.WILDCARD));
	}

	public static void scanForItems() {
		for (Object id : Block.blockRegistry.getKeys())
			try {
				Block block = (Block) Block.blockRegistry.getObject(id);

				if (block instanceof IPlantable
						|| block instanceof IShearable
						|| block instanceof BlockLog
						|| block instanceof BlockMelon
						|| block instanceof BlockPumpkin)
					addBackpackItem("forester", block);
				if (block instanceof BlockStairs || block.getRenderType() == 10
						|| block instanceof BlockFence || block.getRenderType() == 11
						|| block instanceof BlockFenceGate || block.getRenderType() == 21
						|| block instanceof BlockWall || block.getRenderType() == 32
						|| block instanceof BlockBasePressurePlate
						|| block instanceof BlockLever
						|| block instanceof BlockButton
						|| block instanceof BlockTorch
						|| block instanceof BlockRedstoneDiode
						|| block instanceof BlockChest || block.getRenderType() == 22
						|| block instanceof BlockWorkbench
						|| block instanceof BlockFurnace
						|| block instanceof BlockLadder || block.getRenderType() == 8
						|| block instanceof BlockTrapDoor
						|| block.getUnlocalizedName().contains("door"))
					addBackpackItem("builder", block);
			} catch (Throwable error) {
				error.printStackTrace();
			}

		for (Object id : Item.itemRegistry.getKeys())
			try {
				Item item = (Item) Item.itemRegistry.getObject(id);

				if (item instanceof IPlantable)
					addBackpackItem("forester", item);
				else if (item instanceof ItemFood)
					addBackpackItem("hunter", item);
				else if (item instanceof ItemDoor)
					addBackpackItem("builder", item);
			} catch (Throwable error) {
				error.printStackTrace();
			}
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-backpack-items")) {
			String[] tokens = message.getStringValue().split("@");
			if (tokens.length != 2) {
				Logger.getLogger("Forestry").log(Level.INFO,
						String.format("Received an invalid 'add-backpack-items' request %s from mod %s", message.getStringValue(), message.getSender()));
				return true;
			}

			if (!BackpackManager.definitions.containsKey(tokens[0])) {
				Logger.getLogger("Forestry").log(
						Level.INFO,
						String.format("Received an invalid 'add-backpack-items' request %s from mod %s for non-existent backpack %s.",
								message.getStringValue(), message.getSender(), tokens[0]));
				return true;
			}

			parseBackpackItems(tokens[0] + "'s Backpack", tokens[1], BackpackManager.definitions.get(tokens[0]));

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
		BackpackDefinition definition;

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			definition = new BackpackDefinitionApiarist("apiarist", 0xc4923d);
			BackpackManager.definitions.put(definition.getKey(), definition);
			ForestryItem.apiaristBackpack.registerItem(new ItemNaturalistBackpack(GuiId.ApiaristBackpackGUI.ordinal(), definition).setCreativeTab(Tabs.tabApiculture), "apiaristBag");
		}

		if (PluginManager.Module.LEPIDOPTEROLOGY.isEnabled()) {
			definition = new BackpackDefinitionLepidopterist("lepidopterist", 0x995b31);
			BackpackManager.definitions.put(definition.getKey(), definition);
			ForestryItem.lepidopteristBackpack.registerItem(new ItemNaturalistBackpack(GuiId.LepidopteristBackpackGUI.ordinal(), definition).setCreativeTab(Tabs.tabLepidopterology), "lepidopteristBag");
		}

		definition = new BackpackDefinition("miner", 0x36187d);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.minerBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "minerBag");
		ForestryItem.minerBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "minerBagT2");

		definition = new BackpackDefinition("digger", 0x363cc5);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.diggerBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "diggerBag");
		ForestryItem.diggerBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "diggerBagT2");

		definition = new BackpackDefinition("forester", 0x347427);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.foresterBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "foresterBag");
		ForestryItem.foresterBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "foresterBagT2");

		definition = new BackpackDefinition("hunter", 0x412215);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.hunterBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "hunterBag");
		ForestryItem.hunterBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "hunterBagT2");

		definition = new BackpackDefinition("adventurer", 0x7fb8c2);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.adventurerBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "adventurerBag");
		ForestryItem.adventurerBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "adventurerBagT2");

		definition = new BackpackDefinition("builder", 0xdd3a3a);
		BackpackManager.definitions.put(definition.getKey(), definition);
		ForestryItem.builderBackpack.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T1), "builderBag");
		ForestryItem.builderBackpackT2.registerItem(BackpackManager.backpackInterface.addBackpack(definition, EnumBackpackType.T2), "builderBagT2");
	}

	@Override
	protected void registerBackpackItems() {

		// [0] Set valid items in miner's backpack
		minerItems.add(new ItemStack(Blocks.obsidian));
		minerItems.add(new ItemStack(Blocks.coal_ore));
		minerItems.add(new ItemStack(Items.coal));
		minerItems.add(new ItemStack(Blocks.diamond_ore));
		minerItems.add(new ItemStack(Items.diamond));
		minerItems.add(new ItemStack(Blocks.gold_ore));
		minerItems.add(new ItemStack(Items.gold_ingot));
		minerItems.add(new ItemStack(Blocks.iron_ore));
		minerItems.add(new ItemStack(Items.iron_ingot));
		minerItems.add(new ItemStack(Blocks.lapis_ore));
		minerItems.add(new ItemStack(Blocks.redstone_ore));
		minerItems.add(new ItemStack(Items.redstone));
		minerItems.add(new ItemStack(Items.dye, 1, 4));
		minerItems.add(new ItemStack(Items.glowstone_dust));
		minerItems.add(new ItemStack(Items.emerald));
		minerItems.add(new ItemStack(Blocks.diamond_block));
		minerItems.add(new ItemStack(Blocks.emerald_block));
		minerItems.add(new ItemStack(Blocks.gold_block));
		minerItems.add(new ItemStack(Blocks.iron_block));
		minerItems.add(new ItemStack(Blocks.lapis_block));
		minerItems.add(ForestryItem.bronzePickaxe.getItemStack());
		minerItems.add(ForestryItem.kitPickaxe.getItemStack());
		minerItems.add(ForestryItem.brokenBronzePickaxe.getItemStack());
		minerItems.add(new ItemStack(Items.quartz));
		minerItems.add(new ItemStack(Blocks.emerald_ore));
		minerItems.add(new ItemStack(Blocks.quartz_ore));

		// [1] Set valid items in digger's backpack
		diggerItems.add(new ItemStack(Blocks.dirt));
		diggerItems.add(new ItemStack(Blocks.cobblestone));
		diggerItems.add(new ItemStack(Blocks.sand));
		diggerItems.add(new ItemStack(Blocks.sandstone));
		diggerItems.add(new ItemStack(Blocks.gravel));
		diggerItems.add(new ItemStack(Items.flint));
		diggerItems.add(new ItemStack(Blocks.netherrack));
		diggerItems.add(new ItemStack(Items.clay_ball));
		diggerItems.add(new ItemStack(Blocks.soul_sand));
		diggerItems.add(ForestryItem.bronzeShovel.getItemStack());
		diggerItems.add(ForestryItem.kitShovel.getItemStack());
		diggerItems.add(ForestryItem.brokenBronzeShovel.getItemStack());

		// [2] Set valid items in forester's backpack
		foresterItems.add(new ItemStack(Blocks.sapling, 1, Defaults.WILDCARD));
		foresterItems.add(new ItemStack(Blocks.red_mushroom));
		foresterItems.add(new ItemStack(Blocks.brown_mushroom));
		foresterItems.add(new ItemStack(Blocks.log, 1, Defaults.WILDCARD));
		foresterItems.add(new ItemStack(Items.wheat_seeds));
		foresterItems.add(new ItemStack(Blocks.red_flower));
		foresterItems.add(new ItemStack(Blocks.yellow_flower));
		foresterItems.add(new ItemStack(Blocks.leaves, 1, Defaults.WILDCARD));
		foresterItems.add(new ItemStack(Blocks.cactus));
		foresterItems.add(new ItemStack(Blocks.tallgrass, 1, Defaults.WILDCARD));
		foresterItems.add(new ItemStack(Blocks.vine));
		foresterItems.add(new ItemStack(Blocks.pumpkin));
		foresterItems.add(new ItemStack(Blocks.melon_block));
		foresterItems.add(new ItemStack(Items.apple));
		foresterItems.add(new ItemStack(Items.golden_apple));
		foresterItems.add(new ItemStack(Items.nether_wart));
		foresterItems.add(new ItemStack(Items.pumpkin_seeds));
		foresterItems.add(new ItemStack(Items.melon_seeds));
		foresterItems.add(new ItemStack(Items.wheat));
		if (PluginManager.Module.ARBORICULTURE.isEnabled()) {
			foresterItems.add(ForestryBlock.saplingGE.getWildcard());
		}

		// [3] Set valid items in hunter's backpack
		hunterItems.add(new ItemStack(Items.feather));
		hunterItems.add(new ItemStack(Items.gunpowder));
		hunterItems.add(new ItemStack(Items.blaze_powder));
		hunterItems.add(new ItemStack(Items.blaze_rod));
		hunterItems.add(new ItemStack(Items.bone));
		hunterItems.add(new ItemStack(Items.string));
		hunterItems.add(new ItemStack(Items.rotten_flesh));
		hunterItems.add(new ItemStack(Items.ghast_tear));
		hunterItems.add(new ItemStack(Items.gold_nugget));
		hunterItems.add(new ItemStack(Items.arrow));
		hunterItems.add(new ItemStack(Items.porkchop));
		hunterItems.add(new ItemStack(Items.cooked_porkchop));
		hunterItems.add(new ItemStack(Items.beef));
		hunterItems.add(new ItemStack(Items.cooked_beef));
		hunterItems.add(new ItemStack(Items.chicken));
		hunterItems.add(new ItemStack(Items.cooked_chicken));
		hunterItems.add(new ItemStack(Items.leather));
		hunterItems.add(new ItemStack(Items.egg));
		hunterItems.add(new ItemStack(Items.ender_pearl));
		hunterItems.add(new ItemStack(Items.spider_eye));
		hunterItems.add(new ItemStack(Items.fermented_spider_eye));
		hunterItems.add(new ItemStack(Items.slime_ball));
		hunterItems.add(new ItemStack(Items.dye, 1, 0));
		hunterItems.add(new ItemStack(Blocks.hay_block));
		hunterItems.add(new ItemStack(Blocks.wool));
		hunterItems.add(new ItemStack(Blocks.wool, 1, Defaults.WILDCARD));
		hunterItems.add(new ItemStack(Items.ender_eye));
		hunterItems.add(new ItemStack(Items.magma_cream));
		hunterItems.add(new ItemStack(Items.speckled_melon));
		hunterItems.add(new ItemStack(Items.fish));
		hunterItems.add(new ItemStack(Items.cooked_fished));
		hunterItems.add(new ItemStack(Items.lead));
		hunterItems.add(new ItemStack(Items.fishing_rod));
		hunterItems.add(new ItemStack(Items.name_tag));
		hunterItems.add(new ItemStack(Items.saddle));
		hunterItems.add(new ItemStack(Items.diamond_horse_armor));
		hunterItems.add(new ItemStack(Items.golden_horse_armor));
		hunterItems.add(new ItemStack(Items.iron_horse_armor));

		// [4] Set valid items in adventurer's backpack

		// [5] Set valid items in builder's backpack
		if (PluginManager.Module.APICULTURE.isEnabled()) {
			builderItems.add(ForestryBlock.candle.getWildcard());
			builderItems.add(ForestryBlock.stump.getWildcard());
		}

		builderItems.add(new ItemStack(Blocks.torch));
		builderItems.add(new ItemStack(Blocks.redstone_torch));
		builderItems.add(new ItemStack(Blocks.redstone_lamp));
		builderItems.add(new ItemStack(Blocks.stonebrick, 1, Defaults.WILDCARD));
		builderItems.add(new ItemStack(Blocks.sandstone, 1, 1));
		builderItems.add(new ItemStack(Blocks.sandstone, 1, 2));
		builderItems.add(new ItemStack(Blocks.stone));
		builderItems.add(new ItemStack(Blocks.brick_block));
		builderItems.add(new ItemStack(Blocks.planks, 1, Defaults.WILDCARD));
		builderItems.add(new ItemStack(Blocks.hardened_clay, 1, Defaults.WILDCARD));
		builderItems.add(new ItemStack(Blocks.nether_brick));
		builderItems.add(new ItemStack(Blocks.nether_brick_fence));
		builderItems.add(new ItemStack(Blocks.stone_stairs));
		builderItems.add(new ItemStack(Blocks.oak_stairs));
		builderItems.add(new ItemStack(Blocks.brick_stairs));
		builderItems.add(new ItemStack(Blocks.nether_brick_stairs));
		builderItems.add(new ItemStack(Blocks.stone_brick_stairs));
		builderItems.add(new ItemStack(Blocks.glass));
		builderItems.add(new ItemStack(Blocks.glass_pane));
		builderItems.add(new ItemStack(Blocks.chest));
		builderItems.add(new ItemStack(Blocks.crafting_table));
		builderItems.add(new ItemStack(Blocks.furnace));
		builderItems.add(new ItemStack(Blocks.lever));
		builderItems.add(new ItemStack(Blocks.wooden_button));
		builderItems.add(new ItemStack(Blocks.stone_button));
		builderItems.add(new ItemStack(Blocks.dispenser));
		builderItems.add(new ItemStack(Blocks.dropper));
		builderItems.add(new ItemStack(Blocks.ladder));
		builderItems.add(new ItemStack(Blocks.fence));
		builderItems.add(new ItemStack(Blocks.fence_gate));
		builderItems.add(new ItemStack(Blocks.iron_bars));
		builderItems.add(new ItemStack(Blocks.stone_slab, 1, Defaults.WILDCARD));
		builderItems.add(new ItemStack(Blocks.quartz_block, 1, Defaults.WILDCARD));
		builderItems.add(new ItemStack(Blocks.quartz_stairs));
		builderItems.add(new ItemStack(Blocks.sandstone_stairs));
		builderItems.add(new ItemStack(Blocks.birch_stairs));
		builderItems.add(new ItemStack(Blocks.spruce_stairs));
		builderItems.add(new ItemStack(Blocks.jungle_stairs));
		builderItems.add(new ItemStack(Blocks.cobblestone_wall, 1, Defaults.WILDCARD));
		builderItems.add(new ItemStack(Items.iron_door));
		builderItems.add(new ItemStack(Items.wooden_door));
		builderItems.add(new ItemStack(Items.sign));
		builderItems.add(new ItemStack(Items.repeater));
		builderItems.add(new ItemStack(Items.comparator));
		builderItems.add(new ItemStack(Items.item_frame));
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

	@Override
	protected void registerCrates() {
	}

	private static void parseBackpackItems(String backpackIdent, String list, IBackpackDefinition target) {
		String[] parts = list.split("[;]+");

		for (String part : parts) {
			if (part.isEmpty())
				continue;

			String[] ident = part.split("[:]+");

			if (ident.length != 2 && ident.length != 3) {
				Proxies.log.warning("Failed to add block/item of (" + part + ") to " + backpackIdent + " since it isn't formatted properly. Suitable are <name>, <name>:<meta> or <name>:*, e.g. IC2:blockWall:*.");
				continue;
			}

			String name = ident[0] + ":" + ident[1];
			int meta;

			if (ident.length == 2)
				meta = 0;
			else
				try {
					meta = ident[2].equals("*") ? OreDictionary.WILDCARD_VALUE : NumberFormat.getIntegerInstance().parse(ident[2]).intValue();
				} catch (ParseException e) {
					Proxies.log.warning("Failed to add block/item of (" + part + ") to " + backpackIdent + " since its metadata isn't formatted properly. Suitable are integer values or *.");
					continue;
				}

			Item item = GameData.getItemRegistry().getRaw(name);

			if (item == null) {
				Block block = GameData.getBlockRegistry().getRaw(name);

				if (block == null || Item.getItemFromBlock(block) == null) {
					Proxies.log.warning("Failed to add block/item of (" + part + ") to " + backpackIdent + " since it couldn't be found.");
					continue;
				}

				item = Item.getItemFromBlock(block);
			}

			Proxies.log.finer("Adding block/item of (" + part + ") to " + backpackIdent + ".");
			target.addValidItem(new ItemStack(item, 1, meta));
		}
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return this;
	}

	@Override
	public void onOreRegistration(String name, ItemStack ore) {

		if (ore == null) {
			Proxies.log.warning("An ore/item of type %s was registered with the Forge ore dictionary, however the passed itemstack is null. Someone broke it. :(", name);
			return;
		}

		if (name.startsWith("ingot"))
			minerItems.add(ore);
		else if (name.startsWith("ore"))
			minerItems.add(ore);
		else if (name.startsWith("gem"))
			minerItems.add(ore);
		else if (name.startsWith("dust"))
			minerItems.add(ore);
		else if (name.startsWith("crystal"))
			minerItems.add(ore);
		else if (name.startsWith("cluster"))
			minerItems.add(ore);
		else if (name.startsWith("shard"))
			minerItems.add(ore);
		else if (name.startsWith("clump"))
			minerItems.add(ore);
		else if (name.matches("dropUranium"))
			minerItems.add(ore);
		else if (name.equals("treeLeaves") || name.equals("treeSapling") || name.equals("logWood"))
			foresterItems.add(ore);
		else if (name.equals("stairWood") || name.equals("plankWood") || name.equals("slabWood"))
			builderItems.add(ore);
		else if (name.startsWith("wood"))
			foresterItems.add(ore);
	}
}
