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
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.arboriculture.EnumFruitFamily;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.arboriculture.FruitProviderNone;
import forestry.arboriculture.FruitProviderPod;
import forestry.arboriculture.FruitProviderPod.EnumPodType;
import forestry.arboriculture.FruitProviderRandom;
import forestry.arboriculture.FruitProviderRipening;
import forestry.arboriculture.GuiHandlerArboriculture;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.commands.CommandTree;
import forestry.arboriculture.gadgets.BlockArbFence;
import forestry.arboriculture.gadgets.BlockArbStairs;
import forestry.arboriculture.gadgets.BlockFruitPod;
import forestry.arboriculture.gadgets.BlockLog;
import forestry.arboriculture.gadgets.BlockPlanks;
import forestry.arboriculture.gadgets.BlockSapling;
import forestry.arboriculture.gadgets.BlockSlab;
import forestry.arboriculture.gadgets.ForestryBlockLeaves;
import forestry.arboriculture.gadgets.TileArboristChest;
import forestry.arboriculture.gadgets.TileFruitPod;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.arboriculture.gadgets.TileWood;
import forestry.arboriculture.genetics.AlleleFruit;
import forestry.arboriculture.genetics.AlleleGrowth;
import forestry.arboriculture.genetics.AlleleLeafEffectNone;
import forestry.arboriculture.genetics.GrowthProvider;
import forestry.arboriculture.genetics.GrowthProviderTropical;
import forestry.arboriculture.genetics.TreeBranchDefinition;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeFactory;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.genetics.TreeMutationFactory;
import forestry.arboriculture.genetics.TreekeepingMode;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemGrafter;
import forestry.arboriculture.items.ItemLeavesBlock;
import forestry.arboriculture.items.ItemTreealyzer;
import forestry.arboriculture.items.ItemWoodBlock;
import forestry.arboriculture.network.PacketHandlerArboriculture;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.render.LeafTexture;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.genetics.alleles.Allele;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.network.IPacketHandler;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.core.utils.ShapelessRecipeCustom;
import forestry.factory.recipes.FabricatorRecipe;

@Plugin(pluginID = "Arboriculture", name = "Arboriculture", author = "Binnie & SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.arboriculture.description")
public class PluginArboriculture extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.arboriculture.proxy.ClientProxyArboriculture", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;
	public static String treekeepingMode = "NORMAL";

	public static int modelIdSaplings;
	public static int modelIdLeaves;
	public static int modelIdFences;
	public static int modelIdPods;

	private static MachineDefinition definitionChest;
	public static final List<Block> validFences = new ArrayList<Block>();

	@Override
	protected void setupAPI() {
		super.setupAPI();

		TreeManager.treeFactory = new TreeFactory();
		TreeManager.treeMutationFactory = new TreeMutationFactory();

		// Init tree interface
		TreeManager.treeRoot = new TreeHelper();
		AlleleManager.alleleRegistry.registerSpeciesRoot(TreeManager.treeRoot);

		// Modes
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.easy);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.normal);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.hard);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.hardcore);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.insane);
	}

	@Override
	public void preInit() {
		super.preInit();
		
		MinecraftForge.EVENT_BUS.register(this);

		// Wood blocks
		ForestryBlock.logs.registerBlock(new BlockLog(false), ItemWoodBlock.class, "logs");
		OreDictionary.registerOre("logWood", ForestryBlock.logs.getWildcard());

		ForestryBlock.planks.registerBlock(new BlockPlanks(false), ItemWoodBlock.class, "planks");
		OreDictionary.registerOre("plankWood", ForestryBlock.planks.getWildcard());

		ForestryBlock.slabs.registerBlock(new BlockSlab(false), ItemWoodBlock.class, "slabs");
		OreDictionary.registerOre("slabWood", ForestryBlock.slabs.getWildcard());

		ForestryBlock.fences.registerBlock(new BlockArbFence(false), ItemWoodBlock.class, "fences");
		OreDictionary.registerOre("fenceWood", ForestryBlock.fences.getWildcard());

		ForestryBlock.stairs.registerBlock(new BlockArbStairs(ForestryBlock.planks.block(), false), ItemWoodBlock.class, "stairs");
		OreDictionary.registerOre("stairWood", ForestryBlock.stairs.getWildcard());

		ForestryBlock.logsFireproof.registerBlock(new BlockLog(true), ItemWoodBlock.class, "logsFireproof");
		ForestryBlock.planksFireproof.registerBlock(new BlockPlanks(true), ItemWoodBlock.class, "planksFireproof");
		ForestryBlock.slabsFireproof.registerBlock(new BlockSlab(true), ItemWoodBlock.class, "slabsFireproof");
		ForestryBlock.fencesFireproof.registerBlock(new BlockArbFence(true), ItemWoodBlock.class, "fencesFireproof");
		ForestryBlock.stairsFireproof.registerBlock(new BlockArbStairs(ForestryBlock.planksFireproof.block(), true), ItemWoodBlock.class, "stairsFireproof");

		for (WoodType woodType : WoodType.VALUES) {
			woodType.registerLog(ForestryBlock.logs.block(), false);
			woodType.registerPlanks(ForestryBlock.planks.block(), false);
			woodType.registerSlab(ForestryBlock.slabs.block(), false);
			woodType.registerFence(ForestryBlock.fences.block(), false);
			woodType.registerStairs(ForestryBlock.stairs.block(), false);

			woodType.registerLog(ForestryBlock.logsFireproof.block(), true);
			woodType.registerPlanks(ForestryBlock.planksFireproof.block(), true);
			woodType.registerSlab(ForestryBlock.slabsFireproof.block(), true);
			woodType.registerFence(ForestryBlock.fencesFireproof.block(), true);
			woodType.registerStairs(ForestryBlock.stairsFireproof.block(), true);
		}

		// Saplings
		ForestryBlock.saplingGE.registerBlock(new BlockSapling(), ItemForestryBlock.class, "saplingGE");
		OreDictionary.registerOre("treeSapling", ForestryBlock.saplingGE.getWildcard());

		// Leaves
		ForestryBlock.leaves.registerBlock(new ForestryBlockLeaves(), ItemLeavesBlock.class, "leaves");
		OreDictionary.registerOre("treeLeaves", ForestryBlock.leaves.getWildcard());

		// Pods
		ForestryBlock.pods.registerBlock(new BlockFruitPod(), ItemForestryBlock.class, "pods");

		// Machines
		ForestryBlock.arboriculture.registerBlock(new BlockBase(Material.iron, Defaults.DEFINITION_ARBORICULTURE_ID), ItemForestryBlock.class, "arboriculture");
		ForestryBlock.arboriculture.block().setCreativeTab(Tabs.tabArboriculture);

		definitionChest = ((BlockBase) ForestryBlock.arboriculture.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_ARBCHEST_META,
				"forestry.ArbChest", TileArboristChest.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.arboriculture.getItemStack(1, Defaults.DEFINITION_ARBCHEST_META),
						" # ",
						"XYX",
						"XXX",
						'#', "blockGlass",
						'X', "treeSapling",
						'Y', "chestWood"))
				.setFaces(0, 1, 2, 3, 4, 4, 0, 7));
		((BlockBase)ForestryBlock.apiculture.block()).registerStateMapper();
		
		// Init rendering
		proxy.initializeRendering();

		// Register vanilla and forestry fence ids
		validFences.add(ForestryBlock.fences.block());
		validFences.add(ForestryBlock.fencesFireproof.block());
		validFences.add(Blocks.oak_fence);
		validFences.add(Blocks.spruce_fence);
		validFences.add(Blocks.birch_fence);
		validFences.add(Blocks.jungle_fence);
		validFences.add(Blocks.dark_oak_fence);
		validFences.add(Blocks.acacia_fence);
		validFences.add(Blocks.oak_fence_gate);
		validFences.add(Blocks.spruce_fence_gate);
		validFences.add(Blocks.birch_fence_gate);
		validFences.add(Blocks.jungle_fence_gate);
		validFences.add(Blocks.dark_oak_fence_gate);
		validFences.add(Blocks.acacia_fence_gate);
		validFences.add(Blocks.nether_brick_fence);

		// Commands
		PluginCore.rootCommand.addChildCommand(new CommandTree());
		
	}

	@Override
	public void doInit() {
		super.doInit();

		// Create alleles
		createAlleles();
		TreeDefinition.initTrees();
		registerErsatzGenomes();

		GameRegistry.registerTileEntity(TileSapling.class, "forestry.Sapling");
		GameRegistry.registerTileEntity(TileLeaves.class, "forestry.Leaves");
		GameRegistry.registerTileEntity(TileWood.class, "forestry.Wood");
		GameRegistry.registerTileEntity(TileFruitPod.class, "forestry.Pods");
		definitionChest.register();

		/*if (Config.enableVillagers) {
			VillagerRegistry.instance().registerVillagerId(Defaults.ID_VILLAGER_LUMBERJACK);
			Proxies.render.registerVillagerSkin(Defaults.ID_VILLAGER_LUMBERJACK, Defaults.TEXTURE_SKIN_LUMBERJACK);
			VillagerRegistry.instance().registerVillageTradeHandler(Defaults.ID_VILLAGER_LUMBERJACK, new VillageHandlerArboriculture());
		}*/
	}

	@Override
	public void postInit() {
		super.postInit();
		registerDungeonLoot();

	}

	@Override
	protected void registerItems() {

		ForestryItem.sapling.registerItem(new ItemGermlingGE(EnumGermlingType.SAPLING), "sapling");
		OreDictionary.registerOre("treeSapling", ForestryItem.sapling.getWildcard());

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			ForestryItem.pollenFertile.registerItem(new ItemGermlingGE(EnumGermlingType.POLLEN), "pollenFertile");
			ForestryItem.treealyzer.registerItem(new ItemTreealyzer(), "treealyzer");
		}

		ForestryItem.grafter.registerItem(new ItemGrafter(4), "grafter");
		ForestryItem.grafterProven.registerItem(new ItemGrafter(149), "grafterProven");

	}

	@Override
	protected void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		crateRegistry.registerCrate(EnumFruit.CHERRY.getStack(), "cratedCherry");
		crateRegistry.registerCrate(EnumFruit.WALNUT.getStack(), "cratedWalnut");
		crateRegistry.registerCrate(EnumFruit.CHESTNUT.getStack(), "cratedChestnut");
		crateRegistry.registerCrate(EnumFruit.LEMON.getStack(), "cratedLemon");
		crateRegistry.registerCrate(EnumFruit.PLUM.getStack(), "cratedPlum");
		crateRegistry.registerCrate(EnumFruit.PAPAYA.getStack(), "cratedPapaya");
		crateRegistry.registerCrate(EnumFruit.DATES.getStack(), "cratedDates");
	}

	@Override
	protected void registerRecipes() {

		Proxies.common.addSmelting(ForestryBlock.logs.getWildcard(), new ItemStack(Items.coal, 1, 1), 0.15F);

		ShapelessRecipeCustom.buildRecipe(ForestryBlock.planks.getItemStack(4), ForestryBlock.logs.getWildcard()).setPreserveNBT();
		ShapelessRecipeCustom.buildRecipe(ForestryBlock.planksFireproof.getItemStack(4), ForestryBlock.logsFireproof.getWildcard()).setPreserveNBT();

		// Fabricator recipes
		if (PluginManager.Module.FACTORY.isEnabled() && PluginManager.Module.APICULTURE.isEnabled()) {

			// Fireproof log recipe
			RecipeManagers.fabricatorManager.addRecipe(new FabricatorRecipe(null, Fluids.GLASS.getFluid(500), ForestryBlock.logsFireproof.getItemStack(), new Object[]{
					" # ",
					"#X#",
					" # ",
					'#', ForestryItem.refractoryWax,
					'X', ForestryBlock.logs.getWildcard()}).setPreserveNBT());

			// Fireproof plank recipe
			RecipeManagers.fabricatorManager.addRecipe(new FabricatorRecipe(null, Fluids.GLASS.getFluid(500), ForestryBlock.planksFireproof.getItemStack(5), new Object[]{
					"X#X",
					"#X#",
					"X#X",
					'#', ForestryItem.refractoryWax,
					'X', ForestryBlock.planks.getWildcard()}).setPreserveNBT());
		}

		ShapedRecipeCustom.buildPriorityRecipe(ForestryBlock.slabs.getItemStack(6), "###", '#', ForestryBlock.planks.getWildcard()).setPreserveNBT();
		ShapedRecipeCustom.buildPriorityRecipe(ForestryBlock.slabsFireproof.getItemStack(6), "###", '#', ForestryBlock.planksFireproof.getWildcard()).setPreserveNBT();

		ShapedRecipeCustom.buildRecipe(ForestryBlock.fences.getItemStack(3), "#X#", "#X#", '#', ForestryBlock.planks.getWildcard(), 'X', "stickWood").setPreserveNBT();
		ShapedRecipeCustom.buildRecipe(ForestryBlock.fencesFireproof.getItemStack(3), "#X#", "#X#", '#', ForestryBlock.planksFireproof.getWildcard(), 'X', "stickWood").setPreserveNBT();

		if (PluginManager.Module.FACTORY.isEnabled()) {
			// Treealyzer
			RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, ForestryItem.treealyzer.getItemStack(), "X#X", "X#X", "RDR",
					'#', "paneGlass",
					'X', "ingotCopper",
					'R', "dustRedstone",
					'D', "gemDiamond");

			// SQUEEZER RECIPES
			int seedOilMultiplier = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed");
			int juiceMultiplier = GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple");
			int mulchMultiplier = GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple");
			RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{EnumFruit.CHERRY.getStack()}, Fluids.SEEDOIL.getFluid(5 * seedOilMultiplier), ForestryItem.mulch.getItemStack(), 5);
			RecipeManagers.squeezerManager.addRecipe(60, new ItemStack[]{EnumFruit.WALNUT.getStack()}, Fluids.SEEDOIL.getFluid(18 * seedOilMultiplier), ForestryItem.mulch.getItemStack(), 5);
			RecipeManagers.squeezerManager.addRecipe(70, new ItemStack[]{EnumFruit.CHESTNUT.getStack()}, Fluids.SEEDOIL.getFluid(22 * seedOilMultiplier), ForestryItem.mulch.getItemStack(), 2);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.LEMON.getStack()}, Fluids.JUICE.getFluid(juiceMultiplier * 2), ForestryItem.mulch.getItemStack(), (int) Math.floor(mulchMultiplier * 0.5f));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.PLUM.getStack()}, Fluids.JUICE.getFluid((int) Math.floor(juiceMultiplier * 0.5f)), ForestryItem.mulch.getItemStack(), mulchMultiplier * 3);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.PAPAYA.getStack()}, Fluids.JUICE.getFluid(juiceMultiplier * 3), ForestryItem.mulch.getItemStack(), (int) Math.floor(mulchMultiplier * 0.5f));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.DATES.getStack()}, Fluids.JUICE.getFluid((int) Math.floor(juiceMultiplier * 0.25)), ForestryItem.mulch.getItemStack(), mulchMultiplier);

			RecipeUtil.injectLeveledRecipe(ForestryItem.sapling.getItemStack(), GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		}

		// Stairs
		ShapedRecipeCustom.buildPriorityRecipe(ForestryBlock.stairs.getItemStack(4),
				"#  ",
				"## ",
				"###",
				'#', ForestryBlock.planks.getWildcard()).setPreserveNBT();

		ShapedRecipeCustom.buildPriorityRecipe(ForestryBlock.stairsFireproof.getItemStack(4),
				"#  ",
				"## ",
				"###",
				'#', ForestryBlock.planksFireproof.getWildcard()).setPreserveNBT();

		// Grafter
		Proxies.common.addRecipe(ForestryItem.grafter.getItemStack(),
				"  B",
				" # ",
				"#  ",
				'B', "ingotBronze",
				'#', "stickWood");
	}

	private static void createAlleles() {

		TreeBranchDefinition.createAlleles();

		// FRUITS
		Allele.fruitNone = new AlleleFruit("none", new FruitProviderNone("none", null));
		Allele.fruitApple = new AlleleFruit("apple", new FruitProviderRandom("apple", EnumFruitFamily.POMES, new ItemStack(Items.apple), 1.0f).setColour(0xff2e2e).setOverlay("pomes"));
		Allele.fruitCocoa = new AlleleFruit("cocoa", new FruitProviderPod("cocoa", EnumFruitFamily.JUNGLE, EnumPodType.COCOA));
		// .setColours(0xecdca5, 0xc4d24a), true);
		Allele.fruitChestnut = new AlleleFruit("chestnut", new FruitProviderRipening("chestnut", EnumFruitFamily.NUX, EnumFruit.CHESTNUT.getStack(), 1.0f).setRipeningPeriod(6).setColours(0x7f333d, 0xc4d24a).setOverlay("nuts"), true);
		Allele.fruitWalnut = new AlleleFruit("walnut", new FruitProviderRipening("walnut", EnumFruitFamily.NUX, EnumFruit.WALNUT.getStack(), 1.0f).setRipeningPeriod(8).setColours(0xfba248, 0xc4d24a).setOverlay("nuts"), true);
		Allele.fruitCherry = new AlleleFruit("cherry", new FruitProviderRipening("cherry", EnumFruitFamily.PRUNES, EnumFruit.CHERRY.getStack(), 1.0f).setColours(0xff2e2e, 0xc4d24a).setOverlay("berries"), true);
		Allele.fruitDates = new AlleleFruit("dates", new FruitProviderPod("dates", EnumFruitFamily.JUNGLE, EnumPodType.DATES, EnumFruit.DATES.getStack(4)));
		Allele.fruitPapaya = new AlleleFruit("papaya", new FruitProviderPod("papaya", EnumFruitFamily.JUNGLE, EnumPodType.PAPAYA, EnumFruit.PAPAYA.getStack()));
		// Allele.fruitCoconut = new AlleleFruit("fruitCoconut", new
		// FruitProviderPod("coconut", jungle, EnumPodType.COCONUT, new
		// ItemStack[] { new ItemStack(
		// ForestryItem.fruits, 1, EnumFruit.COCONUT.ordinal()) }));
		Allele.fruitLemon = new AlleleFruit("lemon", new FruitProviderRipening("lemon", EnumFruitFamily.PRUNES, EnumFruit.LEMON.getStack(), 1.0f).setColours(0xeeee00, 0x99ff00).setOverlay("citrus"), true);
		Allele.fruitPlum = new AlleleFruit("plum", new FruitProviderRipening("plum", EnumFruitFamily.PRUNES, EnumFruit.PLUM.getStack(), 1.0f).setColours(0x663446, 0xeeff1a).setOverlay("plums"), true);

		// / TREES // GROWTH PROVIDER 1350 - 1399
		Allele.growthLightlevel = new AlleleGrowth("lightlevel", new GrowthProvider());
		Allele.growthAcacia = new AlleleGrowth("acacia", new GrowthProvider());
		Allele.growthTropical = new AlleleGrowth("tropical", new GrowthProviderTropical());

		// / TREES // EFFECTS 1900 - 1999
		Allele.leavesNone = new AlleleLeafEffectNone();

	}

	private static void registerErsatzGenomes() {
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves, 1, 0), TreeDefinition.Oak.getIndividual());
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves, 1, 1), TreeDefinition.Spruce.getIndividual());
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves, 1, 2), TreeDefinition.Birch.getIndividual());
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves, 1, 3), TreeDefinition.Jungle.getIndividual());
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves2, 1, 0), TreeDefinition.Acacia.getIndividual());
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves2, 1, 1), TreeDefinition.DarkOak.getIndividual());

		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 0), TreeDefinition.Oak.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 1), TreeDefinition.Spruce.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 2), TreeDefinition.Birch.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 3), TreeDefinition.Jungle.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 4), TreeDefinition.Acacia.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 5), TreeDefinition.DarkOak.getIndividual());
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerArboriculture();
	}

	@Override
	public IFuelHandler getFuelHandler() {
		return new FuelHandler();
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerArboriculture();
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-fence-block") && message.isStringMessage()) {
			Block block = GameData.getBlockRegistry().getRaw(message.getStringValue());

			if (block != null && block != Blocks.air) {
				validFences.add(block);
			} else {
				logInvalidIMCMessage(message);
			}
			return true;
		}
		return super.processIMCMessage(message);
	}

	private static void registerDungeonLoot() {
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.grafter.getItemStack(), 1, 1, 8));

		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Oak.getMemberStack(EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Spruce.getMemberStack(EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Birch.getMemberStack(EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Larch.getMemberStack(EnumGermlingType.SAPLING), 1, 2, 4));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Lime.getMemberStack(EnumGermlingType.SAPLING), 1, 2, 4));

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Oak.getMemberStack(EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Spruce.getMemberStack(EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Birch.getMemberStack(EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Larch.getMemberStack(EnumGermlingType.POLLEN), 1, 2, 3));
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Lime.getMemberStack(EnumGermlingType.POLLEN), 1, 2, 3));
		}
	}

	private static class FuelHandler implements IFuelHandler {
		@Override
		public int getBurnTime(ItemStack fuel) {
			if (ForestryItem.sapling.isItemEqual(fuel)) {
				return 100;
			}

			if (ForestryBlock.slabs.isItemEqual(fuel)) {
				return 150;
			}

			return 0;
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		LeafTexture.registerAllIcons();
	}
}
