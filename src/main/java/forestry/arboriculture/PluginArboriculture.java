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
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumPileType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.CamouflageManager;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.ILeafTranslator;
import forestry.api.genetics.ISaplingTranslator;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
import forestry.arboriculture.blocks.BlockPile;
import forestry.arboriculture.blocks.BlockRegistryArboriculture;
import forestry.arboriculture.blocks.log.BlockArbLog;
import forestry.arboriculture.blocks.slab.BlockArbSlab;
import forestry.arboriculture.commands.CommandTree;
import forestry.arboriculture.genetics.TreeBranchDefinition;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.TreeFactory;
import forestry.arboriculture.genetics.TreeMutationFactory;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.arboriculture.genetics.TreekeepingMode;
import forestry.arboriculture.genetics.alleles.AlleleFruit;
import forestry.arboriculture.genetics.alleles.AlleleGrowth;
import forestry.arboriculture.genetics.alleles.AlleleLeafEffect;
import forestry.arboriculture.items.ItemCharcoal;
import forestry.arboriculture.items.ItemRegistryArboriculture;
import forestry.arboriculture.models.TextureLeaves;
import forestry.arboriculture.network.PacketRegistryArboriculture;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.tiles.TileFruitPod;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.arboriculture.tiles.TilePile;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.PluginCore;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.genetics.alleles.AllelePlantType;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.IMCUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.VillagerTradeLists;
import forestry.factory.recipes.FabricatorRecipe;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;

@ForestryPlugin(pluginID = ForestryPluginUids.ARBORICULTURE, name = "Arboriculture", author = "Binnie & SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.arboriculture.description")
public class PluginArboriculture extends BlankForestryPlugin {

	@SidedProxy(clientSide = "forestry.arboriculture.proxy.ProxyArboricultureClient", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;
	public static String treekeepingMode = "NORMAL";

	public static final List<Block> validFences = new ArrayList<>();

	public static ItemRegistryArboriculture items;
	public static BlockRegistryArboriculture blocks;

	public static VillagerRegistry.VillagerProfession villagerArborist;

	@Override
	public void setupAPI() {
		TreeManager.treeFactory = new TreeFactory();
		TreeManager.treeMutationFactory = new TreeMutationFactory();

		TreeManager.woodAccess = new WoodAccess();

		// Init tree interface
		TreeManager.treeRoot = new TreeRoot();
		AlleleManager.alleleRegistry.registerSpeciesRoot(TreeManager.treeRoot);

		// Modes
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.easy);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.normal);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.hard);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.hardcore);
		TreeManager.treeRoot.registerTreekeepingMode(TreekeepingMode.insane);
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

		WoodAccess.registerLogs(blocks.logs);
		WoodAccess.registerPlanks(blocks.planks);
		WoodAccess.registerSlabs(blocks.slabs);
		WoodAccess.registerFences(blocks.fences);
		WoodAccess.registerFenceGates(blocks.fenceGates);
		WoodAccess.registerStairs(blocks.stairs);
		WoodAccess.registerDoors(blocks.doors);

		WoodAccess.registerLogs(blocks.logsFireproof);
		WoodAccess.registerPlanks(blocks.planksFireproof);
		WoodAccess.registerSlabs(blocks.slabsFireproof);
		WoodAccess.registerFences(blocks.fencesFireproof);
		WoodAccess.registerFenceGates(blocks.fenceGatesFireproof);
		WoodAccess.registerStairs(blocks.stairsFireproof);

		WoodAccess.registerLogs(blocks.logsVanillaFireproof);
		WoodAccess.registerPlanks(blocks.planksVanillaFireproof);
		WoodAccess.registerSlabs(blocks.slabsVanillaFireproof);
		WoodAccess.registerFences(blocks.fencesVanillaFireproof);
		WoodAccess.registerFenceGates(blocks.fenceGatesVanillaFireproof);
		WoodAccess.registerStairs(blocks.stairsVanillaFireproof);

		// Init rendering
		proxy.initializeModels();

		// Commands
		PluginCore.rootCommand.addChildCommand(new CommandTree());
		
		CamouflageManager.camouflageAccess.registerCamouflageItemHandler(new CamouflageHandlerArbDoor());
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
		createAlleles();
		TreeDefinition.initTrees();
		registerErsatzGenomes();

		GameRegistry.registerTileEntity(TileSapling.class, "forestry.Sapling");
		GameRegistry.registerTileEntity(TileLeaves.class, "forestry.Leaves");
		GameRegistry.registerTileEntity(TileFruitPod.class, "forestry.Pods");
		GameRegistry.registerTileEntity(TilePile.class, "forestry.Piles");

		blocks.treeChest.init();

		if (Config.enableVillagers) {
			villagerArborist = new VillagerRegistry.VillagerProfession(Constants.ID_VILLAGER_ARBORIST, Constants.TEXTURE_SKIN_LUMBERJACK);
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
				planks.stackSize = 4;
				logs.stackSize = 1;
				RecipeUtil.addShapelessRecipe(planks.copy(), logs.copy());

				slabs.stackSize = 6;
				planks.stackSize = 1;
				RecipeUtil.addPriorityRecipe(slabs.copy(), "###", '#', planks.copy());

				fences.stackSize = 3;
				planks.stackSize = 1;
				RecipeUtil.addRecipe(fences.copy(),
						"#X#",
						"#X#",
						'#', planks.copy(), 'X', "stickWood");

				fenceGates.stackSize = 1;
				planks.stackSize = 1;
				RecipeUtil.addRecipe(fenceGates.copy(),
						"X#X",
						"X#X",
						'#', planks.copy(), 'X', "stickWood");

				stairs.stackSize = 4;
				planks.stackSize = 1;
				RecipeUtil.addPriorityRecipe(stairs.copy(),
						"#  ",
						"## ",
						"###",
						'#', planks.copy());

				doors.stackSize = 3;
				planks.stackSize = 1;
				RecipeUtil.addPriorityRecipe(doors.copy(),
						"## ",
						"## ",
						"## ",
						'#', planks.copy());
			}

			fireproofPlanks.stackSize = 4;
			fireproofLogs.stackSize = 1;
			RecipeUtil.addShapelessRecipe(fireproofPlanks.copy(), fireproofLogs.copy());

			fireproofSlabs.stackSize = 6;
			fireproofPlanks.stackSize = 1;
			RecipeUtil.addPriorityRecipe(fireproofSlabs.copy(),
					"###",
					'#', fireproofPlanks.copy());

			fireproofFences.stackSize = 3;
			fireproofPlanks.stackSize = 1;
			RecipeUtil.addRecipe(fireproofFences.copy(),
					"#X#",
					"#X#",
					'#', fireproofPlanks.copy(), 'X', "stickWood");

			fireproofFenceGates.stackSize = 1;
			fireproofPlanks.stackSize = 1;
			RecipeUtil.addRecipe(fireproofFenceGates.copy(),
					"X#X",
					"X#X",
					'#', fireproofPlanks.copy(), 'X', "stickWood");

			fireproofStairs.stackSize = 4;
			fireproofPlanks.stackSize = 1;
			RecipeUtil.addPriorityRecipe(fireproofStairs.copy(),
					"#  ",
					"## ",
					"###",
					'#', fireproofPlanks.copy());

			doors.stackSize = 3;
			fireproofPlanks.stackSize = 1;
			RecipeUtil.addPriorityRecipe(doors.copy(),
					"## ",
					"## ",
					"## ",
					'#', fireproofPlanks.copy());

			// Fabricator recipes
			if (ForestryAPI.enabledPlugins.containsAll(Arrays.asList(ForestryPluginUids.FACTORY, ForestryPluginUids.APICULTURE))) {
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

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FACTORY)) {

			// SQUEEZER RECIPES
			int seedOilMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
			int juiceMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
			int mulchMultiplier = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
			ItemStack mulch = new ItemStack(PluginCore.items.mulch);
			RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{EnumFruit.CHERRY.getStack()}, Fluids.SEED_OIL.getFluid(5 * seedOilMultiplier), mulch, 5);
			RecipeManagers.squeezerManager.addRecipe(60, new ItemStack[]{EnumFruit.WALNUT.getStack()}, Fluids.SEED_OIL.getFluid(18 * seedOilMultiplier), mulch, 5);
			RecipeManagers.squeezerManager.addRecipe(70, new ItemStack[]{EnumFruit.CHESTNUT.getStack()}, Fluids.SEED_OIL.getFluid(22 * seedOilMultiplier), mulch, 2);
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

		RecipeUtil.addRecipe(blocks.treeChest,
				" # ",
				"XYX",
				"XXX",
				'#', "blockGlass",
				'X', "treeSapling",
				'Y', "chestWood");
		
		//Wood Piles
		for(ITree tree : TreeManager.treeRoot.getIndividualTemplates()){
			ItemStack log = tree.getGenome().getPrimary().getWoodProvider().getWoodStack().copy();
			log.stackSize = 1;
			ItemStack woodPile = BlockPile.createWoodPile(tree);
			RecipeUtil.addShapelessRecipe(woodPile, log, log, log, log);
		}
		
		//Dirt Pile Block
		RecipeUtil.addShapelessRecipe(new ItemStack(PluginArboriculture.blocks.piles.get(EnumPileType.DIRT)), OreDictUtil.DUST_ASH, Items.CLAY_BALL, OreDictUtil.DUST_ASH, OreDictUtil.DIRT, OreDictUtil.DIRT, OreDictUtil.DIRT, OreDictUtil.TREE_LEAVES, Items.CLAY_BALL, OreDictUtil.TREE_LEAVES);
		
	}

	private static void createAlleles() {

		TreeBranchDefinition.createAlleles();

		AlleleGrowth.createAlleles();
		AlleleLeafEffect.createAlleles();
		AllelePlantType.createAlleles();
	}

	private static void registerErsatzGenomes() {
		AlleleManager.leafTranslators.put(Blocks.LEAVES, new ILeafTranslator() {
			@Nullable
			@Override
			public ITree getTreeFromLeaf(IBlockState leafBlockState) {
				if (!leafBlockState.getValue(BlockLeaves.DECAYABLE)) {
					return null;
				}
				switch (leafBlockState.getValue(BlockOldLeaf.VARIANT)) {
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
		AlleleManager.leafTranslators.put(Blocks.LEAVES2, new ILeafTranslator() {
			@Nullable
			@Override
			public ITree getTreeFromLeaf(IBlockState leafBlockState) {
				if (!leafBlockState.getValue(BlockLeaves.DECAYABLE)) {
					return null;
				}
				switch (leafBlockState.getValue(BlockNewLeaf.VARIANT)) {
					case ACACIA:
						return TreeDefinition.AcaciaVanilla.getIndividual();
					case DARK_OAK:
						return TreeDefinition.DarkOak.getIndividual();
				}
				return null;
			}
		});

		AlleleManager.saplingTranslation.put(Item.getItemFromBlock(Blocks.SAPLING), new ISaplingTranslator() {
			@Nullable
			@Override
			public ITree getTreeFromSapling(ItemStack sapling) {
				switch (sapling.getMetadata()) {
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
		return new FuelHandler();
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
		hiddenItems.add(new ItemStack(blocks.saplingGE));
	}

	private static class FuelHandler implements IFuelHandler {
		@Override
		public int getBurnTime(ItemStack fuel) {
			Item item = fuel.getItem();

			if (items.sapling == item) {
				return 100;
			}
			
			if(items.charcoal == item){
				return ItemCharcoal.getBurnTime(fuel);
			}
			
			Block block = Block.getBlockFromItem(item);

			if (block instanceof IWoodTyped) {
				IWoodTyped woodTypedBlock = (IWoodTyped) block;
				if (woodTypedBlock.isFireproof()) {
					return 0;
				} else if (block instanceof BlockArbSlab) {
					return 150;
				}else{
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
		for (IAlleleFruit alleleFruit : AlleleFruit.getFruitAlleles()) {
			alleleFruit.getProvider().registerSprites();
		}
	}
}
