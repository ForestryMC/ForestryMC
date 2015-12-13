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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.FuelBurnTimeEvent;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.VillageHandlerArboriculture;
import forestry.arboriculture.WoodItemAccess;
import forestry.arboriculture.blocks.BlockArboricultureType;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
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
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.arboriculture.network.PacketRegistryArboriculture;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.tiles.TileArboristChest;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.arboriculture.tiles.TileSapling;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.blocks.BlockCoreType;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.genetics.alleles.AllelePlantType;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.network.IPacketRegistry;
import forestry.core.proxy.Proxies;
import forestry.core.recipes.RecipeUtil;
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

	public static final List<Block> validFences = new ArrayList<>();

	public static ItemRegistryArboriculture items;
	public static BlockRegistryArboriculture blocks;

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
		items = new ItemRegistryArboriculture();
		blocks = new BlockRegistryArboriculture();
	}

	@Override
	public void preInit() {
		super.preInit();

		// register for FuelBurnTimeEvent
		MinecraftForge.EVENT_BUS.register(this);

		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			WoodItemAccess.registerLog(blocks.logs, woodType, false);
			WoodItemAccess.registerPlanks(blocks.planks, woodType, false);
			WoodItemAccess.registerSlab(blocks.slabs, woodType, false);
			WoodItemAccess.registerFence(blocks.fences, woodType, false);
			WoodItemAccess.registerStairs(blocks.stairs, woodType, false);

			WoodItemAccess.registerLog(blocks.logsFireproof, woodType, true);
			WoodItemAccess.registerPlanks(blocks.planksFireproof, woodType, true);
			WoodItemAccess.registerSlab(blocks.slabsFireproof, woodType, true);
			WoodItemAccess.registerFence(blocks.fencesFireproof, woodType, true);
			WoodItemAccess.registerStairs(blocks.stairsFireproof, woodType, true);
		}

		MachineDefinition definitionChest = new MachineDefinition(BlockArboricultureType.ARBCHEST.ordinal(),
				"forestry.ArbChest", TileArboristChest.class, Proxies.render.getRenderChest("arbchest"))
				.setBoundingBox(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		blocks.arboriculture.addDefinition(definitionChest);

		// Init rendering
		proxy.initializeRendering();

		// Register vanilla and forestry fence ids
		validFences.add(blocks.fences);
		validFences.add(blocks.fencesFireproof);
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

		blocks.arboriculture.init();

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

		RecipeUtil.addSmelting(new ItemStack(blocks.logs, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.coal, 1, 1), 0.15F);

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
			RecipeUtil.addShapelessRecipe(planks.copy(), logs.copy());

			fireproofPlanks.stackSize = 4;
			fireproofLogs.stackSize = 1;
			RecipeUtil.addShapelessRecipe(fireproofPlanks.copy(), fireproofLogs.copy());

			slabs.stackSize = 6;
			planks.stackSize = 1;
			RecipeUtil.addPriorityRecipe(slabs.copy(),
					"###",
					'#', planks.copy());

			fireproofSlabs.stackSize = 6;
			fireproofPlanks.stackSize = 1;
			RecipeUtil.addPriorityRecipe(fireproofSlabs.copy(),
					"###",
					'#', fireproofPlanks.copy());

			fences.stackSize = 3;
			planks.stackSize = 1;
			RecipeUtil.addRecipe(fences.copy(),
					"#X#",
					"#X#",
					'#', planks.copy(), 'X', "stickWood");

			fireproofFences.stackSize = 3;
			fireproofPlanks.stackSize = 1;
			RecipeUtil.addRecipe(fireproofFences.copy(),
					"#X#",
					"#X#",
					'#', fireproofPlanks.copy(), 'X', "stickWood");

			stairs.stackSize = 4;
			planks.stackSize = 1;
			RecipeUtil.addPriorityRecipe(stairs.copy(),
					"#  ",
					"## ",
					"###",
					'#', planks.copy());

			fireproofStairs.stackSize = 4;
			fireproofPlanks.stackSize = 1;
			RecipeUtil.addPriorityRecipe(fireproofStairs.copy(),
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
						'#', PluginCore.items.refractoryWax,
						'X', logs.copy()}));

				planks.stackSize = 1;
				fireproofPlanks.stackSize = 5;
				RecipeManagers.fabricatorManager.addRecipe(new FabricatorRecipe(null, Fluids.GLASS.getFluid(500), fireproofPlanks.copy(), new Object[]{
						"X#X",
						"#X#",
						"X#X",
						'#', PluginCore.items.refractoryWax,
						'X', planks.copy()}));
			}
		}

		if (PluginManager.Module.FACTORY.isEnabled()) {
			// Treealyzer
			RecipeManagers.carpenterManager.addRecipe(100, Fluids.WATER.getFluid(2000), null, items.treealyzer.getItemStack(), "X#X", "X#X", "RDR",
					'#', "paneGlass",
					'X', "ingotCopper",
					'R', "dustRedstone",
					'D', "gemDiamond");

			// SQUEEZER RECIPES
			int seedOilMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
			int juiceMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
			int mulchMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
			ItemStack mulch = new ItemStack(PluginCore.items.mulch);
			RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{EnumFruit.CHERRY.getStack()}, Fluids.SEEDOIL.getFluid(5 * seedOilMultiplier), mulch, 5);
			RecipeManagers.squeezerManager.addRecipe(60, new ItemStack[]{EnumFruit.WALNUT.getStack()}, Fluids.SEEDOIL.getFluid(18 * seedOilMultiplier), mulch, 5);
			RecipeManagers.squeezerManager.addRecipe(70, new ItemStack[]{EnumFruit.CHESTNUT.getStack()}, Fluids.SEEDOIL.getFluid(22 * seedOilMultiplier), mulch, 2);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.LEMON.getStack()}, Fluids.JUICE.getFluid(juiceMultiplier * 2), mulch, (int) Math.floor(mulchMultiplier * 0.5f));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.PLUM.getStack()}, Fluids.JUICE.getFluid((int) Math.floor(juiceMultiplier * 0.5f)), mulch, mulchMultiplier * 3);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.PAPAYA.getStack()}, Fluids.JUICE.getFluid(juiceMultiplier * 3), mulch, (int) Math.floor(mulchMultiplier * 0.5f));
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{EnumFruit.DATES.getStack()}, Fluids.JUICE.getFluid((int) Math.floor(juiceMultiplier * 0.25)), mulch, mulchMultiplier);

			RecipeUtil.addFermenterRecipes(items.sapling.getItemStack(), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		}

		// Grafter
		RecipeUtil.addRecipe(items.grafter.getItemStack(),
				"  B",
				" # ",
				"#  ",
				'B', "ingotBronze",
				'#', "stickWood");

		// ANALYZER
		RecipeUtil.addRecipe(PluginCore.blocks.core.get(BlockCoreType.ANALYZER),
				"XTX",
				" Y ",
				"X X",
				'Y', PluginCore.items.sturdyCasing,
				'T', items.treealyzer,
				'X', "ingotBronze");

		RecipeUtil.addRecipe(blocks.arboriculture.get(BlockArboricultureType.ARBCHEST),
				" # ",
				"XYX",
				"XXX",
				'#', "blockGlass",
				'X', "treeSapling",
				'Y', "chestWood");
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
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves2, 1, 0), TreeDefinition.AcaciaVanilla.getIndividual());
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves2, 1, 1), TreeDefinition.DarkOak.getIndividual());

		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 0), TreeDefinition.Oak.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 1), TreeDefinition.Spruce.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 2), TreeDefinition.Birch.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 3), TreeDefinition.Jungle.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 4), TreeDefinition.AcaciaVanilla.getIndividual());
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 5), TreeDefinition.DarkOak.getIndividual());
	}

	@Override
	public IFuelHandler getFuelHandler() {
		return new FuelHandler();
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryArboriculture();
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
		ChestGenHooks.addItem(Constants.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(items.grafter.getItemStack(), 1, 1, 8));

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
			Item item = fuel.getItem();

			if (items.sapling == item) {
				return 100;
			}

			return 0;
		}
	}

	@SubscribeEvent
	public void fuelBurnTimeEvent(FuelBurnTimeEvent fuelBurnTimeEvent) {
		Item item = fuelBurnTimeEvent.fuel.getItem();
		Block block = Block.getBlockFromItem(item);

		if (block instanceof IWoodTyped) {
			IWoodTyped woodTypedBlock = (IWoodTyped) block;
			if (woodTypedBlock.isFireproof()) {
				fuelBurnTimeEvent.burnTime = 0;
				fuelBurnTimeEvent.setResult(Event.Result.DENY);
			} else if (blocks.slabs == block) {
				fuelBurnTimeEvent.burnTime = 150;
				fuelBurnTimeEvent.setResult(Event.Result.DENY);
			}
		}
	}
}
