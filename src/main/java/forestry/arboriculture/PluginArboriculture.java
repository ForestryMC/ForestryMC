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
package forestry.arboriculture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Preconditions;
import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IArmorNaturalist;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBlockTranslator;
import forestry.api.genetics.IItemTranslator;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.arboriculture.blocks.BlockArbLog;
import forestry.arboriculture.blocks.BlockArbSlab;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.arboriculture.capabilities.ArmorNaturalist;
import forestry.arboriculture.charcoal.CharcoalPileWall;
import forestry.arboriculture.commands.CommandTree;
import forestry.arboriculture.genetics.TreeBranchDefinition;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeFactory;
import forestry.arboriculture.genetics.TreeMutationFactory;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.arboriculture.genetics.TreekeepingMode;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.genetics.alleles.AlleleLeafEffects;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemGrafter;
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.arboriculture.models.TextureLeaves;
import forestry.arboriculture.models.WoodTextureManager;
import forestry.arboriculture.network.PacketRegistryArboriculture;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.proxy.ProxyArboricultureClient;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.arboriculture.tiles.TileSapling;
import forestry.arboriculture.worldgen.TreeDecorator;
import forestry.core.PluginCore;
import forestry.core.capabilities.NullStorage;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.render.TextureManagerForestry;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.VillagerTradeLists;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@ForestryPlugin(pluginID = ForestryPluginUids.ARBORICULTURE, name = "Arboriculture", author = "Binnie & SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.arboriculture.description")
public class PluginArboriculture extends BlankForestryPlugin {

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.arboriculture.proxy.ProxyArboricultureClient", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;
	public static String treekeepingMode = "NORMAL";

	public static final List<Block> validFences = new ArrayList<>();

	@Nullable
	private static ItemRegistryArboriculture items;
	@Nullable
	private static BlockRegistryArboriculture blocks;
	@Nullable
	public static VillagerRegistry.VillagerProfession villagerArborist;

	public static ItemRegistryArboriculture getItems() {
		Preconditions.checkState(items != null);
		return items;
	}

	public static BlockRegistryArboriculture getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public void setupAPI() {
		TreeManager.treeFactory = new TreeFactory();
		TreeManager.treeMutationFactory = new TreeMutationFactory();

		TreeManager.woodAccess = WoodAccess.getInstance();

		// Init tree interface
		TreeManager.treeRoot = new TreeRoot();
		AlleleManager.alleleRegistry.registerSpeciesRoot(TreeManager.treeRoot);

		// Modes
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.easy);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.normal);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.hard);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.hardcore);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.insane);

		// Capabilities
		CapabilityManager.INSTANCE.register(IArmorNaturalist.class, new NullStorage<>(), () -> ArmorNaturalist.INSTANCE);
	}

	@Override
	public void disabledSetupAPI() {
		TreeManager.woodAccess = WoodAccess.getInstance();

		// Capabilities
		CapabilityManager.INSTANCE.register(IArmorNaturalist.class, new NullStorage<>(), () -> ArmorNaturalist.INSTANCE);
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryArboriculture();
		blocks = new BlockRegistryArboriculture();
	}

	@Override
	public void preInit() {
		super.preInit();

		MinecraftForge.EVENT_BUS.register(this);
		
		if (Config.generateTrees) {
			MinecraftForge.TERRAIN_GEN_BUS.register(new TreeDecorator());
		}

		BlockRegistryArboriculture blocks = getBlocks();

		WoodAccess woodAccess = WoodAccess.getInstance();

		woodAccess.registerLogs(blocks.logs);
		woodAccess.registerPlanks(blocks.planks);
		woodAccess.registerSlabs(blocks.slabs);
		woodAccess.registerFences(blocks.fences);
		woodAccess.registerFenceGates(blocks.fenceGates);
		woodAccess.registerStairs(blocks.stairs);
		woodAccess.registerDoors(blocks.doors);

		woodAccess.registerLogs(blocks.logsFireproof);
		woodAccess.registerPlanks(blocks.planksFireproof);
		woodAccess.registerSlabs(blocks.slabsFireproof);
		woodAccess.registerFences(blocks.fencesFireproof);
		woodAccess.registerFenceGates(blocks.fenceGatesFireproof);
		woodAccess.registerStairs(blocks.stairsFireproof);

		woodAccess.registerLogs(blocks.logsVanillaFireproof);
		woodAccess.registerPlanks(blocks.planksVanillaFireproof);
		woodAccess.registerSlabs(blocks.slabsVanillaFireproof);
		woodAccess.registerFences(blocks.fencesVanillaFireproof);
		woodAccess.registerFenceGates(blocks.fenceGatesVanillaFireproof);
		woodAccess.registerStairs(blocks.stairsVanillaFireproof);

		// Init rendering
		proxy.initializeModels();

		// Commands
		PluginCore.rootCommand.addChildCommand(new CommandTree());

		CamouflageManager.camouflageAccess.registerCamouflageItemHandler(new CamouflageHandlerArbDoor());
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.CLAY, 3));
		TreeManager.pileWalls.add(new CharcoalPileWall(PluginArboriculture.getBlocks().loam, 5));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.END_STONE, 6));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.END_BRICKS, 6));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.DIRT, 2));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.NETHERRACK, 4));
	}

	@Override
	public void addLootPoolNames(Set<String> lootPoolNames) {
		super.addLootPoolNames(lootPoolNames);
		lootPoolNames.add("forestry_arboriculture_items");
	}

	@Override
	public void doInit() {
		super.doInit();

		// Create alleles
		registerAlleles();
		TreeDefinition.initTrees();
		registerErsatzGenomes();

		GameRegistry.registerTileEntity(TileSapling.class, "forestry.Sapling");
		GameRegistry.registerTileEntity(TileLeaves.class, "forestry.Leaves");
		GameRegistry.registerTileEntity(TileFruitPod.class, "forestry.Pods");

		ItemRegistryArboriculture items = getItems();
		BlockRegistryArboriculture blocks = getBlocks();

		blocks.treeChest.init();

		if (Config.enableVillagers) {
			villagerArborist = new VillagerRegistry.VillagerProfession(Constants.ID_VILLAGER_ARBORIST, Constants.TEXTURE_SKIN_LUMBERJACK, Constants.TEXTURE_SKIN_ZOMBIE_LUMBERJACK);
			VillagerRegistry.instance().register(villagerArborist);

			VillagerRegistry.VillagerCareer arboristCareer = new VillagerRegistry.VillagerCareer(villagerArborist, "arborist");
			arboristCareer.addTrade(1,
					new VillagerArboristTrades.GivePlanksForEmeralds(new EntityVillager.PriceInfo(1, 1), new EntityVillager.PriceInfo(10, 32)),
					new VillagerArboristTrades.GivePollenForEmeralds(new EntityVillager.PriceInfo(1, 1), new EntityVillager.PriceInfo(1, 3), EnumGermlingType.SAPLING, 4)
			);
			arboristCareer.addTrade(2,
					new VillagerArboristTrades.GivePlanksForEmeralds(new EntityVillager.PriceInfo(1, 1), new EntityVillager.PriceInfo(10, 32)),
					new VillagerTradeLists.GiveItemForEmeralds(new EntityVillager.PriceInfo(1, 4), items.grafterProven.getItemStack(), new EntityVillager.PriceInfo(1, 1)),
					new VillagerArboristTrades.GivePollenForEmeralds(new EntityVillager.PriceInfo(2, 3), new EntityVillager.PriceInfo(1, 1), EnumGermlingType.POLLEN, 6)
			);
			arboristCareer.addTrade(3,
					new VillagerArboristTrades.GiveLogsForEmeralds(new EntityVillager.PriceInfo(2, 5), new EntityVillager.PriceInfo(6, 18)),
					new VillagerArboristTrades.GiveLogsForEmeralds(new EntityVillager.PriceInfo(2, 5), new EntityVillager.PriceInfo(6, 18))
			);
			arboristCareer.addTrade(4,
					new VillagerArboristTrades.GivePollenForEmeralds(new EntityVillager.PriceInfo(5, 20), new EntityVillager.PriceInfo(1, 1), EnumGermlingType.POLLEN, 10),
					new VillagerArboristTrades.GivePollenForEmeralds(new EntityVillager.PriceInfo(5, 20), new EntityVillager.PriceInfo(1, 1), EnumGermlingType.SAPLING, 10)
			);
		}
	}

	@Override
	public void registerCrates() {
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		crateRegistry.registerCrate(EnumFruit.CHERRY.getStack());
		crateRegistry.registerCrate(EnumFruit.WALNUT.getStack());
		crateRegistry.registerCrate(EnumFruit.CHESTNUT.getStack());
		crateRegistry.registerCrate(EnumFruit.LEMON.getStack());
		crateRegistry.registerCrate(EnumFruit.PLUM.getStack());
		crateRegistry.registerCrate(EnumFruit.PAPAYA.getStack());
		crateRegistry.registerCrate(EnumFruit.DATES.getStack());
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = PluginCore.getItems();
		BlockRegistryArboriculture blocks = getBlocks();
		ItemRegistryArboriculture items = getItems();

		for (BlockArbLog log : blocks.logs) {
			ItemStack logInput = new ItemStack(log, 1, OreDictionary.WILDCARD_VALUE);
			ItemStack coalOutput = new ItemStack(Items.COAL, 1, 1);
			RecipeUtil.addSmelting(logInput, coalOutput, 0.15F);
		}

		List<IWoodType> allWoodTypes = new ArrayList<>();
		Collections.addAll(allWoodTypes, EnumForestryWoodType.VALUES);
		Collections.addAll(allWoodTypes, EnumVanillaWoodType.VALUES);

		for (IWoodType woodType : allWoodTypes) {
			ItemStack planks = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.PLANKS, false);
			ItemStack logs = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.LOG, false);
			ItemStack slabs = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.SLAB, false);
			ItemStack fences = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.FENCE, false);
			ItemStack fenceGates = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.FENCE_GATE, false);
			ItemStack stairs = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.STAIRS, false);
			ItemStack doors = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.DOOR, false);

			ItemStack fireproofPlanks = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.PLANKS, true);
			ItemStack fireproofLogs = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.LOG, true);
			ItemStack fireproofSlabs = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.SLAB, true);
			ItemStack fireproofFences = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.FENCE, true);
			ItemStack fireproofFenceGates = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.FENCE_GATE, true);
			ItemStack fireproofStairs = TreeManager.woodAccess.getStack(woodType, WoodBlockKind.STAIRS, true);

			if (woodType instanceof EnumForestryWoodType) {
				planks.setCount(4);
				logs.setCount(1);
				RecipeUtil.addShapelessRecipe(planks.copy(), logs.copy());

				slabs.setCount(6);
				planks.setCount(1);
				RecipeUtil.addPriorityRecipe(slabs.copy(), "###", '#', planks.copy());

				fences.setCount(3);
				planks.setCount(1);
				RecipeUtil.addRecipe(fences.copy(),
						"#X#",
						"#X#",
						'#', planks.copy(), 'X', "stickWood");

				fenceGates.setCount(1);
				planks.setCount(1);
				RecipeUtil.addRecipe(fenceGates.copy(),
						"X#X",
						"X#X",
						'#', planks.copy(), 'X', "stickWood");

				stairs.setCount(4);
				planks.setCount(1);
				RecipeUtil.addPriorityRecipe(stairs.copy(),
						"#  ",
						"## ",
						"###",
						'#', planks.copy());

				doors.setCount(3);
				planks.setCount(1);
				RecipeUtil.addPriorityRecipe(doors.copy(),
						"## ",
						"## ",
						"## ",
						'#', planks.copy());
			}

			fireproofPlanks.setCount(4);
			fireproofLogs.setCount(1);
			RecipeUtil.addShapelessRecipe(fireproofPlanks.copy(), fireproofLogs.copy());

			fireproofSlabs.setCount(6);
			fireproofPlanks.setCount(1);
			RecipeUtil.addPriorityRecipe(fireproofSlabs.copy(),
					"###",
					'#', fireproofPlanks.copy());

			fireproofFences.setCount(3);
			fireproofPlanks.setCount(1);
			RecipeUtil.addRecipe(fireproofFences.copy(),
					"#X#",
					"#X#",
					'#', fireproofPlanks.copy(), 'X', "stickWood");

			fireproofFenceGates.setCount(1);
			fireproofPlanks.setCount(1);
			RecipeUtil.addRecipe(fireproofFenceGates.copy(),
					"X#X",
					"X#X",
					'#', fireproofPlanks.copy(), 'X', "stickWood");

			fireproofStairs.setCount(4);
			fireproofPlanks.setCount(1);
			RecipeUtil.addPriorityRecipe(fireproofStairs.copy(),
					"#  ",
					"## ",
					"###",
					'#', fireproofPlanks.copy());

			doors.setCount(3);
			fireproofPlanks.setCount(1);
			RecipeUtil.addPriorityRecipe(doors.copy(),
					"## ",
					"## ",
					"## ",
					'#', fireproofPlanks.copy());

			// Fabricator recipes
			if (ForestryAPI.enabledPlugins.containsAll(Arrays.asList(ForestryPluginUids.FACTORY, ForestryPluginUids.APICULTURE))) {
				logs.setCount(1);
				fireproofLogs.setCount(1);
				RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), fireproofLogs.copy(), new Object[]{
						" # ",
						"#X#",
						" # ",
						'#', coreItems.refractoryWax,
						'X', logs.copy()});

				planks.setCount(1);
				fireproofPlanks.setCount(5);
				RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), fireproofPlanks.copy(), new Object[]{
						"X#X",
						"#X#",
						"X#X",
						'#', coreItems.refractoryWax,
						'X', planks.copy()});
			}
		}

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {

			// SQUEEZER RECIPES
			int seedOilMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
			int juiceMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
			int mulchMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
			ItemStack mulch = new ItemStack(coreItems.mulch);
			RecipeManagers.squeezerManager.addRecipe(20, EnumFruit.CHERRY.getStack(), Fluids.SEED_OIL.getFluid(5 * seedOilMultiplier), mulch, 5);
			RecipeManagers.squeezerManager.addRecipe(60, EnumFruit.WALNUT.getStack(), Fluids.SEED_OIL.getFluid(18 * seedOilMultiplier), mulch, 5);
			RecipeManagers.squeezerManager.addRecipe(70, EnumFruit.CHESTNUT.getStack(), Fluids.SEED_OIL.getFluid(22 * seedOilMultiplier), mulch, 2);
			RecipeManagers.squeezerManager.addRecipe(10, EnumFruit.LEMON.getStack(), Fluids.JUICE.getFluid(juiceMultiplier * 2), mulch, (int) Math.floor(mulchMultiplier * 0.5f));
			RecipeManagers.squeezerManager.addRecipe(10, EnumFruit.PLUM.getStack(), Fluids.JUICE.getFluid((int) Math.floor(juiceMultiplier * 0.5f)), mulch, mulchMultiplier * 3);
			RecipeManagers.squeezerManager.addRecipe(10, EnumFruit.PAPAYA.getStack(), Fluids.JUICE.getFluid(juiceMultiplier * 3), mulch, (int) Math.floor(mulchMultiplier * 0.5f));
			RecipeManagers.squeezerManager.addRecipe(10, EnumFruit.DATES.getStack(), Fluids.JUICE.getFluid((int) Math.floor(juiceMultiplier * 0.25)), mulch, mulchMultiplier);

			NonNullList<ItemStack> saplings = NonNullList.create();
			items.sapling.addCreativeItems(saplings, false);
			for (ItemStack sapling : saplings) {
				RecipeUtil.addFermenterRecipes(sapling, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
			}
		}

		// Grafter
		RecipeUtil.addRecipe(items.grafter.getItemStack(),
				"  B",
				" # ",
				"#  ",
				'B', "ingotBronze",
				'#', "stickWood");

		RecipeUtil.addRecipe(blocks.treeChest,
				" # ",
				"XYX",
				"XXX",
				'#', "blockGlass",
				'X', "treeSapling",
				'Y', "chestWood");

		//Wood Pile
		RecipeUtil.addShapelessRecipe(new ItemStack(blocks.woodPile), OreDictUtil.LOG_WOOD, OreDictUtil.LOG_WOOD, OreDictUtil.LOG_WOOD, OreDictUtil.LOG_WOOD);

		//Dirt Pile Block
		RecipeUtil.addShapelessRecipe(new ItemStack(blocks.loam, 4), Items.CLAY_BALL, coreItems.fertilizerBio, Items.CLAY_BALL, OreDictUtil.SAND, Items.CLAY_BALL, OreDictUtil.SAND, Items.CLAY_BALL, coreItems.fertilizerBio, Items.CLAY_BALL);
	}

	private static void registerAlleles() {
		TreeBranchDefinition.registerAlleles();
		AlleleLeafEffects.registerAlleles();
	}

	private static void registerErsatzGenomes() {
		TreeManager.treeRoot.registerTranslator(Blocks.LEAVES, new IBlockTranslator<ITree>() {
			@Nullable
			@Override
			public ITree getIndividualFromObject(IBlockState blockState) {
				if (!blockState.getValue(BlockLeaves.DECAYABLE)) {
					return null;
				}
				switch (blockState.getValue(BlockOldLeaf.VARIANT)) {
					case OAK:
						return TreeDefinition.Oak.getIndividual();
					case SPRUCE:
						return TreeDefinition.Spruce.getIndividual();
					case BIRCH:
						return TreeDefinition.Birch.getIndividual();
					case JUNGLE:
						return TreeDefinition.Jungle.getIndividual();
				}
				return null;
			}
		});
		TreeManager.treeRoot.registerTranslator(Blocks.LEAVES2, new IBlockTranslator<ITree>() {
			@Nullable
			@Override
			public ITree getIndividualFromObject(IBlockState blockState) {
				if (!blockState.getValue(BlockLeaves.DECAYABLE)) {
					return null;
				}
				switch (blockState.getValue(BlockNewLeaf.VARIANT)) {
					case ACACIA:
						return TreeDefinition.AcaciaVanilla.getIndividual();
					case DARK_OAK:
						return TreeDefinition.DarkOak.getIndividual();
				}
				return null;
			}
		});

		TreeManager.treeRoot.registerTranslator(Item.getItemFromBlock(Blocks.SAPLING), new IItemTranslator<ITree>() {
			@Nullable
			@Override
			public ITree getIndividualFromObject(ItemStack itemStack) {
				switch (itemStack.getMetadata()) {
					case 0:
						return TreeDefinition.Oak.getIndividual();
					case 1:
						return TreeDefinition.Spruce.getIndividual();
					case 2:
						return TreeDefinition.Birch.getIndividual();
					case 3:
						return TreeDefinition.Jungle.getIndividual();
					case 4:
						return TreeDefinition.AcaciaVanilla.getIndividual();
					case 5:
						return TreeDefinition.DarkOak.getIndividual();
				}
				return null;
			}
		});
	}

	@Override
	public IFuelHandler getFuelHandler() {
		return new FuelHandler(getItems().sapling);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryArboriculture();
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-fence-block") && message.isStringMessage()) {
			Block block = ItemStackUtil.getBlockFromRegistry(message.getStringValue());

			if (block != null) {
				validFences.add(block);
			} else {
				IMCUtil.logInvalidIMCMessage(message);
			}
			return true;
		}
		return super.processIMCMessage(message);
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// sapling itemBlock is different from the normal item
		hiddenItems.add(new ItemStack(getBlocks().saplingGE));
	}

	private static class FuelHandler implements IFuelHandler {
		private final ItemGermlingGE sapling;

		public FuelHandler(ItemGermlingGE sapling) {
			this.sapling = sapling;
		}

		@Override
		public int getBurnTime(ItemStack fuel) {
			Item item = fuel.getItem();
			if (sapling == item) {
				return 100;
			}

			if(Item.getItemFromBlock(blocks.charcoal) == item){
				return 16000;
			}
			if(Item.getItemFromBlock(blocks.woodPile) == item){
				return 1200;
			}
			
			Block block = Block.getBlockFromItem(item);

			if (block instanceof IWoodTyped) {
				IWoodTyped woodTypedBlock = (IWoodTyped) block;
				if (woodTypedBlock.isFireproof()) {
					return 0;
				} else if (block instanceof BlockArbSlab) {
					return 150;
				} else {
					return 300;
				}
			}

			return 0;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerSprites(TextureStitchEvent.Pre event) {
		TextureLeaves.registerAllSprites();
		WoodTextureManager.parseFile(Minecraft.getMinecraft().getResourceManager());
		for (IAlleleFruit alleleFruit : AlleleFruits.getFruitAlleles()) {
			alleleFruit.getProvider().registerSprites();
		}
		List<ResourceLocation> textures = new ArrayList<>();
		for (IWoodType type : TreeManager.woodAccess.getRegisteredWoodTypes()) {
			textures.add(new ResourceLocation(type.getHeartTexture()));
			textures.add(new ResourceLocation(type.getBarkTexture()));
			textures.add(new ResourceLocation(type.getDoorLowerTexture()));
			textures.add(new ResourceLocation(type.getDoorUpperTexture()));
			textures.add(new ResourceLocation(type.getPlankTexture()));
			for (WoodBlockKind kind : WoodBlockKind.values()) {
				for (Entry<String, String> loc : WoodTextureManager.getTextures(type, kind).entrySet()) {
					textures.add(new ResourceLocation(loc.getValue()));
				}
			}
		}
		for (ResourceLocation loc : textures) {
			TextureManagerForestry.registerSprite(loc);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onModelBake(ModelBakeEvent event) {
		((ProxyArboricultureClient) proxy).onModelBake(event);
	}

	@Override
	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		if (Config.generateTrees) {
			TreeDecorator.decorateTrees(world, rand, chunkX, chunkZ);
		}
	}

	@SubscribeEvent
	public void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
		IBlockState state = event.getState();
		Block block = state.getBlock();
		if (block instanceof BlockLeaves && !(block instanceof BlockForestryLeaves)) {
			EntityPlayer player = event.getHarvester();
			if (player != null) {
				ItemStack harvestingTool = player.getHeldItemMainhand();
				if (harvestingTool.getItem() instanceof ItemGrafter) {
					if (event.getDrops().isEmpty()) {
						World world = event.getWorld();
						Item itemDropped = block.getItemDropped(state, world.rand, 3);
						if (itemDropped != Items.AIR) {
							event.getDrops().add(new ItemStack(itemDropped, 1, block.damageDropped(state)));
						}
					}

					harvestingTool.damageItem(1, player);
					if (harvestingTool.isEmpty()) {
						net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, harvestingTool, EnumHand.MAIN_HAND);
					}
				}
			}
		}
	}
}
