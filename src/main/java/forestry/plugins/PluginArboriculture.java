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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.arboriculture.GuiHandlerArboriculture;
import forestry.arboriculture.VillageHandlerArboriculture;
import forestry.arboriculture.WoodItemAccess;
import forestry.arboriculture.blocks.BlockArbFence;
import forestry.arboriculture.blocks.BlockArbStairs;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.arboriculture.blocks.BlockLog;
import forestry.arboriculture.blocks.BlockPlanks;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.blocks.BlockSlab;
import forestry.arboriculture.commands.CommandTree;
import forestry.arboriculture.genetics.TreeBranchDefinition;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeFactory;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.genetics.TreeMutationFactory;
import forestry.arboriculture.genetics.TreekeepingMode;
import forestry.arboriculture.genetics.alleles.AlleleFruit;
import forestry.arboriculture.genetics.alleles.AlleleGrowth;
import forestry.arboriculture.genetics.alleles.AlleleLeafEffect;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemGrafter;
import forestry.arboriculture.network.PacketRipeningUpdate;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.tiles.TileArboristChest;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.arboriculture.tiles.TileSapling;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.GuiHandlerBase;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.genetics.alleles.AllelePlantType;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.items.ItemWithGui;
import forestry.core.network.GuiId;
import forestry.core.network.PacketIdClient;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.core.recipes.ShapelessRecipeCustom;
import forestry.core.tiles.MachineDefinition;
import forestry.factory.recipes.FabricatorRecipe;

@Plugin(pluginID = "Arboriculture", name = "Arboriculture", author = "Binnie & SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.arboriculture.description")
public class PluginArboriculture extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.arboriculture.proxy.ProxyArboricultureClient", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;
	public static String treekeepingMode = "NORMAL";

	public static int modelIdSaplings;
	public static int modelIdLeaves;
	public static int modelIdPods;

	private static MachineDefinition definitionChest;
	public static final List<Block> validFences = new ArrayList<>();

	@Override
	protected void setupAPI() {
		super.setupAPI();

		TreeManager.treeFactory = new TreeFactory();
		TreeManager.treeMutationFactory = new TreeMutationFactory();

		TreeManager.woodItemAccess = new WoodItemAccess();

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
	protected void registerItemsAndBlocks() {

		ForestryItem.sapling.registerItem(new ItemGermlingGE(EnumGermlingType.SAPLING), "sapling");
		OreDictionary.registerOre("treeSapling", ForestryItem.sapling.getWildcard());

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			ForestryItem.pollenFertile.registerItem(new ItemGermlingGE(EnumGermlingType.POLLEN), "pollenFertile");
			Item treealyzer = new ItemWithGui(GuiId.TreealyzerGUI).setCreativeTab(Tabs.tabArboriculture);
			ForestryItem.treealyzer.registerItem(treealyzer, "treealyzer");
		}

		ForestryItem.grafter.registerItem(new ItemGrafter(4), "grafter");
		ForestryItem.grafterProven.registerItem(new ItemGrafter(149), "grafterProven");

		// Wood blocks
		ForestryBlock.logs.registerBlock(new BlockLog(false), ItemBlockWood.class, "logs");
		OreDictionary.registerOre("logWood", ForestryBlock.logs.getWildcard());

		ForestryBlock.planks.registerBlock(new BlockPlanks(false), ItemBlockWood.class, "planks");
		OreDictionary.registerOre("plankWood", ForestryBlock.planks.getWildcard());

		ForestryBlock.slabs.registerBlock(new BlockSlab(false), ItemBlockWood.class, "slabs");
		OreDictionary.registerOre("slabWood", ForestryBlock.slabs.getWildcard());

		ForestryBlock.fences.registerBlock(new BlockArbFence(false), ItemBlockWood.class, "fences");
		OreDictionary.registerOre("fenceWood", ForestryBlock.fences.getWildcard());

		ForestryBlock.stairs.registerBlock(new BlockArbStairs(ForestryBlock.planks.block(), false), ItemBlockWood.class, "stairs");
		OreDictionary.registerOre("stairWood", ForestryBlock.stairs.getWildcard());

		ForestryBlock.logsFireproof.registerBlock(new BlockLog(true), ItemBlockWood.class, "logsFireproof");
		OreDictionary.registerOre("logWood", ForestryBlock.logsFireproof.getWildcard());

		ForestryBlock.planksFireproof.registerBlock(new BlockPlanks(true), ItemBlockWood.class, "planksFireproof");
		OreDictionary.registerOre("plankWood", ForestryBlock.planksFireproof.getWildcard());

		ForestryBlock.slabsFireproof.registerBlock(new BlockSlab(true), ItemBlockWood.class, "slabsFireproof");
		OreDictionary.registerOre("slabWood", ForestryBlock.slabsFireproof.getWildcard());

		ForestryBlock.fencesFireproof.registerBlock(new BlockArbFence(true), ItemBlockWood.class, "fencesFireproof");
		OreDictionary.registerOre("fenceWood", ForestryBlock.fencesFireproof.getWildcard());

		ForestryBlock.stairsFireproof.registerBlock(new BlockArbStairs(ForestryBlock.planksFireproof.block(), true), ItemBlockWood.class, "stairsFireproof");
		OreDictionary.registerOre("stairWood", ForestryBlock.stairsFireproof.getWildcard());

		// Saplings
		ForestryBlock.saplingGE.registerBlock(new BlockSapling(), ItemBlockForestry.class, "saplingGE");
		OreDictionary.registerOre("treeSapling", ForestryBlock.saplingGE.getWildcard());

		// Leaves
		ForestryBlock.leaves.registerBlock(new BlockForestryLeaves(), ItemBlockLeaves.class, "leaves");
		OreDictionary.registerOre("treeLeaves", ForestryBlock.leaves.getWildcard());

		// Pods
		ForestryBlock.pods.registerBlock(new BlockFruitPod(), ItemBlockForestry.class, "pods");

		// Machines
		ForestryBlock.arboriculture.registerBlock(new BlockBase(Material.iron, true), ItemBlockForestry.class, "arboriculture");
		ForestryBlock.arboriculture.block().setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public void preInit() {
		super.preInit();

		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			WoodItemAccess.registerLog(ForestryBlock.logs.block(), woodType, false);
			WoodItemAccess.registerPlanks(ForestryBlock.planks.block(), woodType, false);
			WoodItemAccess.registerSlab(ForestryBlock.slabs.block(), woodType, false);
			WoodItemAccess.registerFence(ForestryBlock.fences.block(), woodType, false);
			WoodItemAccess.registerStairs(ForestryBlock.stairs.block(), woodType, false);

			WoodItemAccess.registerLog(ForestryBlock.logsFireproof.block(), woodType, true);
			WoodItemAccess.registerPlanks(ForestryBlock.planksFireproof.block(), woodType, true);
			WoodItemAccess.registerSlab(ForestryBlock.slabsFireproof.block(), woodType, true);
			WoodItemAccess.registerFence(ForestryBlock.fencesFireproof.block(), woodType, true);
			WoodItemAccess.registerStairs(ForestryBlock.stairsFireproof.block(), woodType, true);
		}

		definitionChest = ((BlockBase) ForestryBlock.arboriculture.block()).addDefinition(new MachineDefinition(Constants.DEFINITION_ARBCHEST_META,
				"forestry.ArbChest", TileArboristChest.class, Proxies.render.getRenderChest("arbchest"),
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.arboriculture.getItemStack(1, Constants.DEFINITION_ARBCHEST_META),
						" # ",
						"XYX",
						"XXX",
						'#', "blockGlass",
						'X', "treeSapling",
						'Y', "chestWood"))
				.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F));

		// Init rendering
		proxy.initializeRendering();

		// Register vanilla and forestry fence ids
		validFences.add(ForestryBlock.fences.block());
		validFences.add(ForestryBlock.fencesFireproof.block());
		validFences.add(Blocks.fence);
		validFences.add(Blocks.fence_gate);
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

		if (Config.enableVillagers) {
			VillagerRegistry.instance().registerVillagerId(Constants.ID_VILLAGER_LUMBERJACK);
			Proxies.render.registerVillagerSkin(Constants.ID_VILLAGER_LUMBERJACK, Constants.TEXTURE_SKIN_LUMBERJACK);
			VillagerRegistry.instance().registerVillageTradeHandler(Constants.ID_VILLAGER_LUMBERJACK, new VillageHandlerArboriculture());
		}
	}

	@Override
	public void postInit() {
		super.postInit();
		registerDungeonLoot();
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

		RecipeUtil.addSmelting(ForestryBlock.logs.getWildcard(), new ItemStack(Items.coal, 1, 1), 0.15F);

		for (EnumWoodType woodType : EnumWoodType.VALUES) {

			ItemStack planks = TreeManager.woodItemAccess.getPlanks(woodType, false);
			ItemStack logs = TreeManager.woodItemAccess.getLog(woodType, false);
			ItemStack slabs = TreeManager.woodItemAccess.getSlab(woodType, false);
			ItemStack fences = TreeManager.woodItemAccess.getFence(woodType, false);
			ItemStack stairs = TreeManager.woodItemAccess.getStairs(woodType, false);

			ItemStack fireproofPlanks = TreeManager.woodItemAccess.getPlanks(woodType, true);
			ItemStack fireproofLogs = TreeManager.woodItemAccess.getLog(woodType, true);
			ItemStack fireproofSlabs = TreeManager.woodItemAccess.getSlab(woodType, true);
			ItemStack fireproofFences = TreeManager.woodItemAccess.getFence(woodType, true);
			ItemStack fireproofStairs = TreeManager.woodItemAccess.getStairs(woodType, true);

			planks.stackSize = 4;
			logs.stackSize = 1;
			ShapelessRecipeCustom.buildRecipe(planks.copy(), logs.copy());

			fireproofPlanks.stackSize = 4;
			fireproofLogs.stackSize = 1;
			ShapelessRecipeCustom.buildRecipe(fireproofPlanks.copy(), fireproofLogs.copy());

			slabs.stackSize = 6;
			planks.stackSize = 1;
			ShapedRecipeCustom.buildPriorityRecipe(slabs.copy(),
					"###",
					'#', planks.copy());

			fireproofSlabs.stackSize = 6;
			fireproofPlanks.stackSize = 1;
			ShapedRecipeCustom.buildPriorityRecipe(fireproofSlabs.copy(),
					"###",
					'#', fireproofPlanks.copy());

			fences.stackSize = 3;
			planks.stackSize = 1;
			ShapedRecipeCustom.buildRecipe(fences.copy(),
					"#X#",
					"#X#",
					'#', planks.copy(), 'X', "stickWood");

			fireproofFences.stackSize = 3;
			fireproofPlanks.stackSize = 1;
			ShapedRecipeCustom.buildRecipe(fireproofFences.copy(),
					"#X#",
					"#X#",
					'#', fireproofPlanks.copy(), 'X', "stickWood");

			stairs.stackSize = 4;
			planks.stackSize = 1;
			ShapedRecipeCustom.buildPriorityRecipe(stairs.copy(),
					"#  ",
					"## ",
					"###",
					'#', planks.copy());

			fireproofStairs.stackSize = 4;
			fireproofPlanks.stackSize = 1;
			ShapedRecipeCustom.buildPriorityRecipe(fireproofStairs.copy(),
					"#  ",
					"## ",
					"###",
					'#', fireproofPlanks.copy());

			// Fabricator recipes
			if (PluginManager.Module.FACTORY.isEnabled() && PluginManager.Module.APICULTURE.isEnabled()) {
				logs.stackSize = 1;
				fireproofLogs.stackSize = 1;
				RecipeManagers.fabricatorManager.addRecipe(new FabricatorRecipe(null, Fluids.GLASS.getFluid(500), fireproofLogs.copy(), new Object[]{
						" # ",
						"#X#",
						" # ",
						'#', ForestryItem.refractoryWax,
						'X', logs.copy()}));

				planks.stackSize = 1;
				fireproofPlanks.stackSize = 5;
				RecipeManagers.fabricatorManager.addRecipe(new FabricatorRecipe(null, Fluids.GLASS.getFluid(500), fireproofPlanks.copy(), new Object[]{
						"X#X",
						"#X#",
						"X#X",
						'#', ForestryItem.refractoryWax,
						'X', planks.copy()}));
			}
		}

		if (PluginManager.Module.FACTORY.isEnabled()) {
			// Treealyzer
			RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, ForestryItem.treealyzer.getItemStack(), "X#X", "X#X", "RDR",
					'#', "paneGlass",
					'X', "ingotCopper",
					'R', "dustRedstone",
					'D', "gemDiamond");

			// SQUEEZER RECIPES
			int seedOilMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
			int juiceMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
			int mulchMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
			RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{EnumFruit.CHERRY.getStack()}, Fluids.SEEDOIL.getFluid(5 * seedOilMultiplier), ForestryItem.mulch.getItemStack(), 5);
			RecipeManagers.squeezerManager.addRecipe(60, new ItemStack[]{EnumFruit.WALNUT.getStack()}, Fluids.SEEDOIL.getFluid(18 * seedOilMultiplier), ForestryItem.mulch.getItemStack(), 5);
			RecipeManagers.squeezerManager.addRecipe(70, new ItemStack[]{EnumFruit.CHESTNUT.getStack()}, Fluids.SEEDOIL.getFluid(22 * seedOilMultiplier), ForestryItem.mulch.getItemStack(), 2);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.LEMON.getStack()}, Fluids.JUICE.getFluid(juiceMultiplier * 2), ForestryItem.mulch.getItemStack(), (int) Math.floor(mulchMultiplier * 0.5f));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.PLUM.getStack()}, Fluids.JUICE.getFluid((int) Math.floor(juiceMultiplier * 0.5f)), ForestryItem.mulch.getItemStack(), mulchMultiplier * 3);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.PAPAYA.getStack()}, Fluids.JUICE.getFluid(juiceMultiplier * 3), ForestryItem.mulch.getItemStack(), (int) Math.floor(mulchMultiplier * 0.5f));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.DATES.getStack()}, Fluids.JUICE.getFluid((int) Math.floor(juiceMultiplier * 0.25)), ForestryItem.mulch.getItemStack(), mulchMultiplier);

			RecipeUtil.addFermenterRecipes(ForestryItem.sapling.getItemStack(), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		}

		// Grafter
		RecipeUtil.addRecipe(ForestryItem.grafter.getItemStack(),
				"  B",
				" # ",
				"#  ",
				'B', "ingotBronze",
				'#', "stickWood");
	}

	private static void createAlleles() {

		TreeBranchDefinition.createAlleles();

		AlleleFruit.createAlleles();
		AlleleGrowth.createAlleles();
		AlleleLeafEffect.createAlleles();
		AllelePlantType.createAlleles();
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
	public GuiHandlerBase getGuiHandler() {
		return new GuiHandlerArboriculture();
	}

	@Override
	public IFuelHandler getFuelHandler() {
		return new FuelHandler();
	}

	@Override
	public void registerPacketHandlers() {
		PacketIdClient.RIPENING_UPDATE.setPacketHandler(new PacketRipeningUpdate());
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
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.grafter.getItemStack(), 1, 1, 8));

		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Oak.getMemberStack(EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Spruce.getMemberStack(EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Birch.getMemberStack(EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Larch.getMemberStack(EnumGermlingType.SAPLING), 1, 2, 4));
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Lime.getMemberStack(EnumGermlingType.SAPLING), 1, 2, 4));

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Oak.getMemberStack(EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Spruce.getMemberStack(EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Birch.getMemberStack(EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Larch.getMemberStack(EnumGermlingType.POLLEN), 1, 2, 3));
			ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(TreeDefinition.Lime.getMemberStack(EnumGermlingType.POLLEN), 1, 2, 3));
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
}
