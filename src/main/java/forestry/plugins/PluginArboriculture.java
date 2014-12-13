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
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;

import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IFruitFamily;
import forestry.api.recipes.RecipeManagers;
import forestry.api.storage.BackpackManager;
import forestry.arboriculture.CommandSpawnForest;
import forestry.arboriculture.CommandSpawnTree;
import forestry.arboriculture.CommandTreekeepingMode;
import forestry.arboriculture.EventHandlerArboriculture;
import forestry.arboriculture.FruitProviderNone;
import forestry.arboriculture.FruitProviderPod;
import forestry.arboriculture.FruitProviderPod.EnumPodType;
import forestry.arboriculture.FruitProviderRandom;
import forestry.arboriculture.FruitProviderRipening;
import forestry.arboriculture.GuiHandlerArboriculture;
import forestry.arboriculture.PacketHandlerArboriculture;
import forestry.arboriculture.VillageHandlerArboriculture;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.gadgets.BlockArbFence;
import forestry.arboriculture.gadgets.BlockArbFence.FenceCat;
import forestry.arboriculture.gadgets.BlockArbStairs;
import forestry.arboriculture.gadgets.BlockFireproofLog;
import forestry.arboriculture.gadgets.BlockFireproofPlanks;
import forestry.arboriculture.gadgets.BlockFruitPod;
import forestry.arboriculture.gadgets.BlockLog;
import forestry.arboriculture.gadgets.BlockLog.LogCat;
import forestry.arboriculture.gadgets.BlockPlanks;
import forestry.arboriculture.gadgets.BlockPlanks.PlankCat;
import forestry.arboriculture.gadgets.BlockSapling;
import forestry.arboriculture.gadgets.BlockSlab;
import forestry.arboriculture.gadgets.BlockSlab.SlabCat;
import forestry.arboriculture.gadgets.ForestryBlockLeaves;
import forestry.arboriculture.gadgets.TileArboristChest;
import forestry.arboriculture.gadgets.TileFruitPod;
import forestry.arboriculture.gadgets.TileLeaves;
import forestry.arboriculture.gadgets.TileSapling;
import forestry.arboriculture.gadgets.TileStairs;
import forestry.arboriculture.genetics.AlleleFruit;
import forestry.arboriculture.genetics.AlleleGrowth;
import forestry.arboriculture.genetics.AlleleLeafEffectNone;
import forestry.arboriculture.genetics.AlleleTreeSpecies;
import forestry.arboriculture.genetics.BranchTrees;
import forestry.arboriculture.genetics.GrowthProvider;
import forestry.arboriculture.genetics.GrowthProviderTropical;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.arboriculture.genetics.TreeMutation;
import forestry.arboriculture.genetics.TreeTemplates;
import forestry.arboriculture.genetics.TreekeepingMode;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemGrafter;
import forestry.arboriculture.items.ItemLeavesBlock;
import forestry.arboriculture.items.ItemStairs;
import forestry.arboriculture.items.ItemTreealyzer;
import forestry.arboriculture.items.ItemWoodBlock;
import forestry.arboriculture.proxy.ProxyArboriculture;
import forestry.arboriculture.worldgen.WorldGenAcacia;
import forestry.arboriculture.worldgen.WorldGenAcaciaVanilla;
import forestry.arboriculture.worldgen.WorldGenBalsa;
import forestry.arboriculture.worldgen.WorldGenBaobab;
import forestry.arboriculture.worldgen.WorldGenBirch;
import forestry.arboriculture.worldgen.WorldGenCherry;
import forestry.arboriculture.worldgen.WorldGenChestnut;
import forestry.arboriculture.worldgen.WorldGenDarkOak;
import forestry.arboriculture.worldgen.WorldGenDate;
import forestry.arboriculture.worldgen.WorldGenEbony;
import forestry.arboriculture.worldgen.WorldGenGiganteum;
import forestry.arboriculture.worldgen.WorldGenGreenheart;
import forestry.arboriculture.worldgen.WorldGenJungle;
import forestry.arboriculture.worldgen.WorldGenKapok;
import forestry.arboriculture.worldgen.WorldGenLarch;
import forestry.arboriculture.worldgen.WorldGenLemon;
import forestry.arboriculture.worldgen.WorldGenLime;
import forestry.arboriculture.worldgen.WorldGenMahoe;
import forestry.arboriculture.worldgen.WorldGenMahogany;
import forestry.arboriculture.worldgen.WorldGenMaple;
import forestry.arboriculture.worldgen.WorldGenOak;
import forestry.arboriculture.worldgen.WorldGenPapaya;
import forestry.arboriculture.worldgen.WorldGenPine;
import forestry.arboriculture.worldgen.WorldGenPlum;
import forestry.arboriculture.worldgen.WorldGenPoplar;
import forestry.arboriculture.worldgen.WorldGenSequoia;
import forestry.arboriculture.worldgen.WorldGenSpruce;
import forestry.arboriculture.worldgen.WorldGenTeak;
import forestry.arboriculture.worldgen.WorldGenWalnut;
import forestry.arboriculture.worldgen.WorldGenWenge;
import forestry.arboriculture.worldgen.WorldGenWillow;
import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.genetics.Allele;
import forestry.core.genetics.FruitFamily;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.proxy.Proxies;
import forestry.core.utils.RecipeUtil;
import forestry.core.utils.ShapedRecipeCustom;

@Plugin(pluginID = "Arboriculture", name = "Arboriculture", author = "Binnie & SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.arboriculture.description")
public class PluginArboriculture extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.arboriculture.proxy.ClientProxyArboriculture", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;
	public static String treekeepingMode = "NORMAL";
	public static int modelIdSaplings;
	public static int modelIdLeaves;
	public static int modelIdFences;
	public static int modelIdPods;
	public static ITreeRoot treeInterface;
	public static MachineDefinition definitionChest;
	public static final List<Block> validFences = new ArrayList<Block>();
	private static final EnumSet<ForestryBlock> logs = EnumSet.of(
			ForestryBlock.log1,
			ForestryBlock.log2,
			ForestryBlock.log3,
			ForestryBlock.log4,
			ForestryBlock.log5,
			ForestryBlock.log6,
			ForestryBlock.log7);
	public static final EnumSet<ForestryBlock> fireproofLogs = EnumSet.of(
			ForestryBlock.fireproofLog1,
			ForestryBlock.fireproofLog2,
			ForestryBlock.fireproofLog3,
			ForestryBlock.fireproofLog4,
			ForestryBlock.fireproofLog5,
			ForestryBlock.fireproofLog6,
			ForestryBlock.fireproofLog7);
	private static final EnumSet<ForestryBlock> planks = EnumSet.of(
			ForestryBlock.planks1,
			ForestryBlock.planks2);
	private static final EnumSet<ForestryBlock> fireproofPlanks = EnumSet.of(
			ForestryBlock.fireproofPlanks1,
			ForestryBlock.fireproofPlanks2);
	private static final EnumSet<ForestryBlock> slabs = EnumSet.of(
			ForestryBlock.slabs1,
			ForestryBlock.slabs2,
			ForestryBlock.slabs3);
	private static final EnumSet<ForestryBlock> fences = EnumSet.of(
			ForestryBlock.fences1,
			ForestryBlock.fences1);

	@Override
	public void preInit() {
		super.preInit();

		ForestryBlock.log1.registerBlock(new BlockLog(LogCat.CAT0), ItemWoodBlock.class, "log1");
		ForestryBlock.log2.registerBlock(new BlockLog(LogCat.CAT1), ItemWoodBlock.class, "log2");
		ForestryBlock.log3.registerBlock(new BlockLog(LogCat.CAT2), ItemWoodBlock.class, "log3");
		ForestryBlock.log4.registerBlock(new BlockLog(LogCat.CAT3), ItemWoodBlock.class, "log4");
		ForestryBlock.log5.registerBlock(new BlockLog(LogCat.CAT4), ItemWoodBlock.class, "log5");
		ForestryBlock.log6.registerBlock(new BlockLog(LogCat.CAT5), ItemWoodBlock.class, "log6");
		ForestryBlock.log7.registerBlock(new BlockLog(LogCat.CAT6), ItemWoodBlock.class, "log7");

		for (ForestryBlock log : logs) {
			log.block().setHarvestLevel("axe", 0);
			OreDictionary.registerOre("logWood", log.getWildcard());
		}

		ForestryBlock.fireproofLog1.registerBlock(new BlockFireproofLog(LogCat.CAT0), ItemWoodBlock.class, "fireproofLog1");
		ForestryBlock.fireproofLog2.registerBlock(new BlockFireproofLog(LogCat.CAT1), ItemWoodBlock.class, "fireproofLog2");
		ForestryBlock.fireproofLog3.registerBlock(new BlockFireproofLog(LogCat.CAT2), ItemWoodBlock.class, "fireproofLog3");
		ForestryBlock.fireproofLog4.registerBlock(new BlockFireproofLog(LogCat.CAT3), ItemWoodBlock.class, "fireproofLog4");
		ForestryBlock.fireproofLog5.registerBlock(new BlockFireproofLog(LogCat.CAT4), ItemWoodBlock.class, "fireproofLog5");
		ForestryBlock.fireproofLog6.registerBlock(new BlockFireproofLog(LogCat.CAT5), ItemWoodBlock.class, "fireproofLog6");
		ForestryBlock.fireproofLog7.registerBlock(new BlockFireproofLog(LogCat.CAT6), ItemWoodBlock.class, "fireproofLog7");

		for (ForestryBlock fireproofLog : fireproofLogs) {
			fireproofLog.block().setHarvestLevel("axe", 0);
			OreDictionary.registerOre("logWood", fireproofLog.getWildcard());
		}

		ForestryBlock.planks1.registerBlock(new BlockPlanks(PlankCat.CAT0), ItemWoodBlock.class, "planks");
		ForestryBlock.planks2.registerBlock(new BlockPlanks(PlankCat.CAT1), ItemWoodBlock.class, "planks2");

		for (ForestryBlock plank : planks) {
			plank.block().setHarvestLevel("axe", 0);
			OreDictionary.registerOre("plankWood", plank.getWildcard());
		}

		ForestryBlock.fireproofPlanks1.registerBlock(new BlockFireproofPlanks(PlankCat.CAT0), ItemWoodBlock.class, "fireproofPlanks1");
		ForestryBlock.fireproofPlanks2.registerBlock(new BlockFireproofPlanks(PlankCat.CAT1), ItemWoodBlock.class, "fireproofPlanks2");

		for (ForestryBlock plank : fireproofPlanks) {
			plank.block().setHarvestLevel("axe", 0);
			OreDictionary.registerOre("plankWood", plank.getWildcard());
		}

		ForestryBlock.slabs1.registerBlock(new BlockSlab(SlabCat.CAT0), ItemWoodBlock.class, "slabs1");
		ForestryBlock.slabs2.registerBlock(new BlockSlab(SlabCat.CAT1), ItemWoodBlock.class, "slabs2");
		ForestryBlock.slabs3.registerBlock(new BlockSlab(SlabCat.CAT2), ItemWoodBlock.class, "slabs3");

		for (ForestryBlock plank : slabs) {
			plank.block().setHarvestLevel("axe", 0);
			OreDictionary.registerOre("slabWood", plank.getWildcard());
		}

		// Fences
		ForestryBlock.fences1.registerBlock(new BlockArbFence(FenceCat.CAT0), ItemWoodBlock.class, "fences");
		ForestryBlock.fences2.registerBlock(new BlockArbFence(FenceCat.CAT1), ItemWoodBlock.class, "fences2");

		for (ForestryBlock block : fences) {
			block.block().setHarvestLevel("axe", 0);
			OreDictionary.registerOre("fenceWood", block.getWildcard());
		}

		// Stairs
		ForestryBlock.stairs.registerBlock(new BlockArbStairs(ForestryBlock.planks1.block(), 0), ItemStairs.class, "stairs");
		ForestryBlock.stairs.block().setHarvestLevel("axe", 0);

		// Saplings
		ForestryBlock.saplingGE.registerBlock(new BlockSapling(), ItemForestryBlock.class, "saplingGE");

		// Leaves
		ForestryBlock.leaves.registerBlock(new ForestryBlockLeaves(), ItemLeavesBlock.class, "leaves");
		OreDictionary.registerOre("treeLeaves", ForestryBlock.leaves.getWildcard());

		// Pods
		ForestryBlock.pods.registerBlock(new BlockFruitPod(), ItemForestryBlock.class, "pods");

		// Machines
		ForestryBlock.arboriculture.registerBlock(new BlockBase(Material.iron), ItemForestryBlock.class, "arboriculture");
		ForestryBlock.arboriculture.block().setCreativeTab(Tabs.tabArboriculture);

		definitionChest = ((BlockBase) ForestryBlock.arboriculture.block()).addDefinition(new MachineDefinition(Defaults.DEFINITION_ARBCHEST_META,
				"forestry.ArbChest", TileArboristChest.class,
				ShapedRecipeCustom.createShapedRecipe(ForestryBlock.arboriculture.getItemStack(1, Defaults.DEFINITION_ARBCHEST_META),
						" # ",
						"XYX",
						"XXX",
						'#', Blocks.glass,
						'X', "treeSapling",
						'Y', Blocks.chest))
				.setFaces(0, 1, 2, 3, 4, 4, 0, 7));

		// Init tree interface
		AlleleManager.alleleRegistry.registerSpeciesRoot(PluginArboriculture.treeInterface = new TreeHelper());

		// Init rendering
		proxy.initializeRendering();

		// Create alleles
		createAlleles();

		// Register vanilla and forestry fence ids
		validFences.add(ForestryBlock.fences1.block());
		validFences.add(ForestryBlock.fences2.block());
		validFences.add(Blocks.fence);
		validFences.add(Blocks.fence_gate);
		validFences.add(Blocks.nether_brick_fence);

	}

	@Override
	public void doInit() {
		super.doInit();

		GameRegistry.registerTileEntity(TileSapling.class, "forestry.Sapling");
		GameRegistry.registerTileEntity(TileLeaves.class, "forestry.Leaves");
		GameRegistry.registerTileEntity(TileStairs.class, "forestry.Stairs");
		GameRegistry.registerTileEntity(TileFruitPod.class, "forestry.Pods");
		definitionChest.register();

		createMutations();
		registerTemplates();
		registerErsatzGenomes();

		PluginArboriculture.treeInterface.registerTreekeepingMode(TreekeepingMode.easy);
		PluginArboriculture.treeInterface.registerTreekeepingMode(TreekeepingMode.normal);
		PluginArboriculture.treeInterface.registerTreekeepingMode(TreekeepingMode.hard);
		PluginArboriculture.treeInterface.registerTreekeepingMode(TreekeepingMode.hardcore);
		PluginArboriculture.treeInterface.registerTreekeepingMode(TreekeepingMode.insane);

		MinecraftForge.EVENT_BUS.register(new EventHandlerArboriculture());

		VillagerRegistry.instance().registerVillagerId(Defaults.ID_VILLAGER_LUMBERJACK);
		Proxies.render.registerVillagerSkin(Defaults.ID_VILLAGER_LUMBERJACK, Defaults.TEXTURE_SKIN_LUMBERJACK);
		VillagerRegistry.instance().registerVillageTradeHandler(Defaults.ID_VILLAGER_LUMBERJACK,
				new VillageHandlerArboriculture());
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
	protected void registerBackpackItems() {

		for (ForestryBlock block : logs)
			BackpackManager.definitions.get("forester").addValidItem(block.getWildcard());

		for (ForestryBlock block : fireproofLogs)
			BackpackManager.definitions.get("forester").addValidItem(block.getWildcard());

		BackpackManager.definitions.get("forester").addValidItem(ForestryItem.sapling.getWildcard());
		BackpackManager.definitions.get("forester").addValidItem(ForestryItem.fruits.getWildcard());

		for (ForestryBlock block : slabs)
			BackpackManager.definitions.get("builder").addValidItem(block.getWildcard());
		for (ForestryBlock block : fences)
			BackpackManager.definitions.get("builder").addValidItem(block.getWildcard());
		for (ForestryBlock block : planks)
			BackpackManager.definitions.get("builder").addValidItem(block.getWildcard());

		BackpackManager.definitions.get("builder").addValidItem(ForestryBlock.stairs.getWildcard());
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	protected void registerRecipes() {

		for (ForestryBlock log : logs)
			Proxies.common.addSmelting(log.getWildcard(), new ItemStack(Items.coal, 1, 1), 0.15F);

		// / Plank recipes
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.planks1.getItemStack(4, i), ForestryBlock.log1.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.planks1.getItemStack(4, 4 + i), ForestryBlock.log2.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.planks1.getItemStack(4, 8 + i), ForestryBlock.log3.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.planks1.getItemStack(4, 12 + i), ForestryBlock.log4.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.planks2.getItemStack(4, i), ForestryBlock.log5.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.planks2.getItemStack(4, 4 + i), ForestryBlock.log6.getItemStack(1, i));

		// Fabricator recipes
		if (PluginManager.Module.FACTORY.isEnabled() && PluginManager.Module.APICULTURE.isEnabled()) {

			// Fireproof log recipes
			for (ForestryBlock forestryBlock : logs) {
				BlockLog blockLog = (BlockLog) forestryBlock.block();
				ForestryBlock fireproofLog = BlockFireproofLog.getFireproofLog(blockLog);

				if (forestryBlock == ForestryBlock.log8)
					continue;

				for (int i = 0; i < 4; i++) {
					if (forestryBlock == ForestryBlock.log7 && i > 0)
						break;

					ItemStack logStack = forestryBlock.getItemStack(1, i);
					ItemStack fireproofLogStack = fireproofLog.getItemStack(1, i);
					RecipeManagers.fabricatorManager.addRecipe(null, FluidRegistry.getFluidStack(Defaults.LIQUID_GLASS, 500), fireproofLogStack, new Object[]{
							" # ",
							"#X#",
							" # ",
							'#', ForestryItem.refractoryWax,
							'X', logStack});
				}
			}

			// Fireproof plank recipes
			ForestryBlock plank = ForestryBlock.planks1;
			for (int i = 0; i < 16; i++) {
				ForestryBlock fireproofPlank = BlockFireproofPlanks.getFireproofPlanks((BlockPlanks)plank.block());
				ItemStack plankStack = plank.getItemStack(1, i);
				ItemStack fireproofPlankStack = fireproofPlank.getItemStack(5, i);
				RecipeManagers.fabricatorManager.addRecipe(null, FluidRegistry.getFluidStack(Defaults.LIQUID_GLASS, 500), fireproofPlankStack, new Object[]{
						"X#X",
						"#X#",
						"X#X",
						'#', ForestryItem.refractoryWax,
						'X', plankStack});
			}
			plank = ForestryBlock.planks2;
			for (int i = 0; i < 8; i++) {
				ForestryBlock fireproofPlank = BlockFireproofPlanks.getFireproofPlanks((BlockPlanks)plank.block());
				ItemStack plankStack = plank.getItemStack(1, i);
				ItemStack fireproofPlankStack = fireproofPlank.getItemStack(5, i);
				RecipeManagers.fabricatorManager.addRecipe(null, FluidRegistry.getFluidStack(Defaults.LIQUID_GLASS, 500), fireproofPlankStack, new Object[]{
						"X#X",
						"#X#",
						"X#X",
						'#', ForestryItem.refractoryWax,
						'X', plankStack});
			}
		}

		// / Fireproof Plank recipes
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.fireproofPlanks1.getItemStack(4, i), ForestryBlock.fireproofLog1.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.fireproofPlanks1.getItemStack(4, 4 + i), ForestryBlock.fireproofLog2.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.fireproofPlanks1.getItemStack(4, 8 + i), ForestryBlock.fireproofLog3.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.fireproofPlanks1.getItemStack(4, 12 + i), ForestryBlock.fireproofLog4.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.fireproofPlanks2.getItemStack(4, i), ForestryBlock.fireproofLog5.getItemStack(1, i));
		for (int i = 0; i < 4; i++)
			Proxies.common.addShapelessRecipe(ForestryBlock.fireproofPlanks2.getItemStack(4, 4 + i), ForestryBlock.fireproofLog6.getItemStack(1, i));

		// Slab recipes
		for (int i = 0; i < 8; i++)
			Proxies.common.addPriorityRecipe(ForestryBlock.slabs1.getItemStack(6, i), "###", '#', ForestryBlock.planks1.getItemStack(1, i));
		for (int i = 0; i < 8; i++)
			Proxies.common.addPriorityRecipe(ForestryBlock.slabs2.getItemStack(6, i), "###", '#', ForestryBlock.planks1.getItemStack(1, 8 + i));
		for (int i = 0; i < 8; i++)
			Proxies.common.addPriorityRecipe(ForestryBlock.slabs3.getItemStack(6, i), "###", '#', ForestryBlock.planks2.getItemStack(1, i));

		// Fence recipes
		for (int i = 0; i < 16; i++)
			Proxies.common.addRecipe(ForestryBlock.fences1.getItemStack(4, i), "###", "# #", '#', ForestryBlock.planks1.getItemStack(1, i));
		for (int i = 0; i < 8; i++)
			Proxies.common.addRecipe(ForestryBlock.fences2.getItemStack(4, i), "###", "# #", '#', ForestryBlock.planks2.getItemStack(1, i));

		// Treealyzer
		RecipeManagers.carpenterManager.addRecipe(100, FluidRegistry.getFluidStack(Defaults.LIQUID_WATER, 2000), null, ForestryItem.treealyzer.getItemStack(), "X#X", "X#X", "RDR",
				'#', Blocks.glass_pane,
				'X', "ingotCopper",
				'R', Items.redstone,
				'D', Items.diamond);

		// SQUEEZER RECIPES
		RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[]{ForestryItem.fruits.getItemStack(1, EnumFruit.CHERRY.ordinal())}, FluidRegistry.getFluidStack(Defaults.LIQUID_SEEDOIL, 5 * GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed")), ForestryItem.mulch.getItemStack(), 5);
		RecipeManagers.squeezerManager.addRecipe(60, new ItemStack[]{ForestryItem.fruits.getItemStack(1, EnumFruit.WALNUT.ordinal())}, FluidRegistry.getFluidStack(Defaults.LIQUID_SEEDOIL, 18 * GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed")), ForestryItem.mulch.getItemStack(), 5);
		RecipeManagers.squeezerManager.addRecipe(70, new ItemStack[]{ForestryItem.fruits.getItemStack(1, EnumFruit.CHESTNUT.ordinal())}, FluidRegistry.getFluidStack(Defaults.LIQUID_SEEDOIL, 22 * GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed")), ForestryItem.mulch.getItemStack(), 2);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.fruits.getItemStack(1, EnumFruit.LEMON.ordinal())}, FluidRegistry.getFluidStack(Defaults.LIQUID_JUICE, GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") * 2), ForestryItem.mulch.getItemStack(), (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple") * 0.5f));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.fruits.getItemStack(1, EnumFruit.PLUM.ordinal())}, FluidRegistry.getFluidStack(Defaults.LIQUID_JUICE, (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") * 0.5f)), ForestryItem.mulch.getItemStack(), GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple") * 3);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.fruits.getItemStack(1, EnumFruit.PAPAYA.ordinal())}, FluidRegistry.getFluidStack(Defaults.LIQUID_JUICE, GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") * 3), ForestryItem.mulch.getItemStack(), (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple") * 0.5f));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{ForestryItem.fruits.getItemStack(1, EnumFruit.DATES.ordinal())}, FluidRegistry.getFluidStack(Defaults.LIQUID_JUICE, (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") * 0.25)), ForestryItem.mulch.getItemStack(), (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple")));

		RecipeUtil.injectLeveledRecipe(ForestryItem.sapling.getItemStack(), GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Defaults.LIQUID_BIOMASS);

		// Stairs
		for (int i = 0; i < 16; i++) {
			WoodType type = WoodType.VALUES[i];
			NBTTagCompound compound = new NBTTagCompound();
			type.saveToCompound(compound);

			ItemStack stairs = ForestryBlock.stairs.getItemStack(4, 0);
			stairs.setTagCompound(compound);
			Proxies.common.addPriorityRecipe(stairs,
					"#  ",
					"## ",
					"###", '#', ForestryBlock.planks1.getItemStack(1, i));
		}
		for (int i = 0; i < 8; i++) {
			WoodType type = WoodType.VALUES[16 + i];
			NBTTagCompound compound = new NBTTagCompound();
			type.saveToCompound(compound);

			ItemStack stairs = ForestryBlock.stairs.getItemStack(4, 0);
			stairs.setTagCompound(compound);
			Proxies.common.addPriorityRecipe(stairs,
					"#  ",
					"## ",
					"###",
					'#', ForestryBlock.planks2.getItemStack(1, i));
		}

		// Grafter
		Proxies.common.addRecipe(ForestryItem.grafter.getItemStack(), "  B", " # ", "#  ", 'B', "ingotBronze", '#', Items.stick);
	}

	private void createAlleles() {

		// Divisions
		IClassification angiosperms = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.DIVISION, "angiosperms", "Angiosperms");
		AlleleManager.alleleRegistry.getClassification("kingdom.plantae").addMemberGroup(
				angiosperms);
		IClassification pinophyta = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.DIVISION, "pinophyta", "Pinophyta");
		AlleleManager.alleleRegistry.getClassification("kingdom.plantae").addMemberGroup(pinophyta);
		IClassification magnoliophyta = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.DIVISION, "magnoliophyta",
						"Magnoliophyta");
		AlleleManager.alleleRegistry.getClassification("kingdom.plantae").addMemberGroup(
				magnoliophyta);

		// Classes
		IClassification rosids = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.CLASS, "rosids", "Rosids");
		angiosperms.addMemberGroup(rosids);
		IClassification asterids = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.CLASS, "asterids", "Asterids");
		angiosperms.addMemberGroup(asterids);
		IClassification pinopsida = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.CLASS, "pinopsida", "Pinopsida");
		pinophyta.addMemberGroup(pinopsida);
		IClassification magnoliopsida = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.CLASS, "magnoliopsida",
						"Magnoliopsida");
		pinophyta.addMemberGroup(magnoliopsida);

		// Orders
		IClassification fabales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "fabales", "Fabales");
		rosids.addMemberGroup(fabales);
		IClassification rosales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "rosales", "Rosales");
		rosids.addMemberGroup(rosales);
		IClassification fagales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "fagales", "Fagales");
		rosids.addMemberGroup(fagales);
		IClassification malvales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "malvales", "Malvales");
		rosids.addMemberGroup(malvales);
		IClassification malpighiales = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.ORDER, "malpighiales",
						"Malpighiales");
		rosids.addMemberGroup(malpighiales);

		IClassification ericales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "ericales", "Ericales");
		asterids.addMemberGroup(ericales);
		IClassification lamiales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "lamiales", "Lamiales");
		asterids.addMemberGroup(lamiales);

		IClassification pinales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "pinales", "Pinales");
		pinopsida.addMemberGroup(pinales);

		IClassification laurales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "laurales", "Laurales");
		rosids.addMemberGroup(laurales);
		IClassification sapindales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "sapindales", "Sapindales");
		rosids.addMemberGroup(sapindales);
		IClassification brassicales = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.ORDER, "brassicales", "Brassicales");
		rosids.addMemberGroup(brassicales);

		// Families
		IClassification betulaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "betulaceae", "Betulaceae");
		fagales.addMemberGroup(betulaceae);
		IClassification fagaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "fagaceae", "Fagaceae");
		fagales.addMemberGroup(fagaceae);
		IClassification juglandaceae = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.FAMILY, "juglandaceae",
						"Juglandaceae");
		fagales.addMemberGroup(juglandaceae);

		IClassification malvaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "malvaceae", "Malvaceae");
		malvales.addMemberGroup(malvaceae);
		IClassification dipterocarpaceae = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.FAMILY, "dipterocarpaceae",
						"Dipterocarpaceae");
		malvales.addMemberGroup(dipterocarpaceae);

		IClassification pinaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "pinaceae", "Pinaceae");
		pinales.addMemberGroup(pinaceae);
		IClassification cupressaceae = AlleleManager.alleleRegistry
				.createAndRegisterClassification(EnumClassLevel.FAMILY, "cupressaceae",
						"Cupressaceae");
		pinales.addMemberGroup(cupressaceae);

		IClassification lamiaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "lamiaceae", "Lamiaceae");
		lamiales.addMemberGroup(lamiaceae);

		IClassification ebenaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "ebenaceae", "Ebenaceae");
		ericales.addMemberGroup(ebenaceae);

		IClassification fabaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "fabaceae", "Fabaceae");
		ericales.addMemberGroup(fabaceae);

		IClassification rosaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "rosaceae", "Rosaceae");
		rosales.addMemberGroup(rosaceae);

		IClassification salicaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "salicaceae", "Salicaceae");
		malpighiales.addMemberGroup(salicaceae);

		IClassification lauraceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "lauraceae", "Lauraceae");
		malpighiales.addMemberGroup(lauraceae);

		IClassification rutaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "rutaceae", "Rutaceae");
		sapindales.addMemberGroup(rutaceae);
		IClassification sapindaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "sapindaceae", "Sapindaceae");
		sapindales.addMemberGroup(sapindaceae);

		IClassification caricaceae = AlleleManager.alleleRegistry.createAndRegisterClassification(
				EnumClassLevel.FAMILY, "caricaceae", "Caricaceae");
		brassicales.addMemberGroup(caricaceae);

		// Genii
		IClassification quercus = new BranchTrees("quercus", "Quercus");
		fagaceae.addMemberGroup(quercus);
		IClassification castanea = new BranchTrees("castanea", "Castanea");
		fagaceae.addMemberGroup(castanea);

		IClassification betula = new BranchTrees("betula", "Betula");
		betulaceae.addMemberGroup(betula);

		IClassification tilia = new BranchTrees("tilia", "Tilia");
		malvaceae.addMemberGroup(tilia);
		IClassification ceiba = new BranchTrees("ceiba", "Ceiba");
		malvaceae.addMemberGroup(ceiba);
		IClassification adansonia = new BranchTrees("adansonia", "Adansonia");
		malvaceae.addMemberGroup(adansonia);

		IClassification picea = new BranchTrees("picea", "Picea");
		pinaceae.addMemberGroup(picea);
		IClassification pinus = new BranchTrees("pinus", "Pinus");
		pinaceae.addMemberGroup(pinus);
		IClassification larix = new BranchTrees("larix", "Larix");
		pinaceae.addMemberGroup(larix);

		IClassification juglans = new BranchTrees("juglans", "Juglans");
		juglandaceae.addMemberGroup(juglans);

		IClassification sequoia = new BranchTrees("sequoia", "Sequoia");
		cupressaceae.addMemberGroup(sequoia);
		IClassification sequoiadendron = new BranchTrees("sequoiadendron", "Sequoiadendron");
		cupressaceae.addMemberGroup(sequoiadendron);

		IClassification tectona = new BranchTrees("tectona", "Tectona");
		lamiaceae.addMemberGroup(tectona);

		IClassification diospyros = new BranchTrees("ebony", "Diospyros");
		ebenaceae.addMemberGroup(diospyros);

		IClassification shorea = new BranchTrees("mahogany", "Shorea");
		dipterocarpaceae.addMemberGroup(shorea);

		IClassification acacia = new BranchTrees("acacia", "Acacia");
		fabaceae.addMemberGroup(acacia);
		IClassification millettia = new BranchTrees("millettia", "Millettia");
		fabaceae.addMemberGroup(millettia);

		IClassification ochroma = new BranchTrees("ochroma", "Ochroma");
		malvaceae.addMemberGroup(ochroma);

		IClassification prunus = new BranchTrees("prunus", "Prunus");
		rosaceae.addMemberGroup(prunus);

		IClassification salix = new BranchTrees("salix", "Salix");
		salicaceae.addMemberGroup(salix);

		IClassification chlorocardium = new BranchTrees("chlorocardium", "Chlorocardium");
		salicaceae.addMemberGroup(chlorocardium);

		IClassification talipariti = new BranchTrees("talipariti", "Talipariti");
		malvaceae.addMemberGroup(talipariti);

		IClassification populus = new BranchTrees("populus", "Populus");
		salicaceae.addMemberGroup(populus);

		IClassification citrus = new BranchTrees("citrus", "Citrus");
		rutaceae.addMemberGroup(citrus);
		IClassification acer = new BranchTrees("acer", "Acer");
		sapindaceae.addMemberGroup(acer);

		IClassification tropical = new BranchTrees("Tropical", "");

		IClassification carica = new BranchTrees("carica", "Carica");

		IFruitFamily prunes = new FruitFamily("prunes", "Prunus domestica");
		IFruitFamily pomes = new FruitFamily("pomes", "Pomum");
		IFruitFamily jungle = new FruitFamily("jungle", "Tropicus");
		IFruitFamily nux = new FruitFamily("nuts", "Nux");

		/* Logs */
		final ItemStack oakLog =    new ItemStack(Blocks.log, 1, 0);
		final ItemStack spruceLog = new ItemStack(Blocks.log, 1, 1);
		final ItemStack birchLog =  new ItemStack(Blocks.log, 1, 2);
		final ItemStack jungleLog = new ItemStack(Blocks.log, 1, 3);

		final ItemStack acaciaLog =  new ItemStack(Blocks.log2, 1, 0);
		final ItemStack darkOakLog = new ItemStack(Blocks.log2, 1, 1);

		final ItemStack larchLog =        ForestryBlock.log1.getItemStack(1, 0);
		final ItemStack teakLog =         ForestryBlock.log1.getItemStack(1, 1);
		final ItemStack desertAcaciaLog = ForestryBlock.log1.getItemStack(1, 2);
		final ItemStack limeLog =         ForestryBlock.log1.getItemStack(1, 3);

		final ItemStack chestnutLog = ForestryBlock.log2.getItemStack(1, 0);
		final ItemStack wengeLog =    ForestryBlock.log2.getItemStack(1, 1);
		final ItemStack baobabLog =   ForestryBlock.log2.getItemStack(1, 2);
		final ItemStack sequioaLog =  ForestryBlock.log2.getItemStack(1, 3);

		final ItemStack kapokLog =    ForestryBlock.log3.getItemStack(1, 0);
		final ItemStack ebonyLog =    ForestryBlock.log3.getItemStack(1, 1);
		final ItemStack mahoganyLog = ForestryBlock.log3.getItemStack(1, 2);
		final ItemStack balsaLog =    ForestryBlock.log3.getItemStack(1, 3);

		final ItemStack willowLog = ForestryBlock.log4.getItemStack(1, 0);
		final ItemStack walnutLog = ForestryBlock.log4.getItemStack(1, 1);
		final ItemStack sipiriLog = ForestryBlock.log4.getItemStack(1, 2);
		final ItemStack cherryLog = ForestryBlock.log4.getItemStack(1, 3);

		final ItemStack mahoeLog =  ForestryBlock.log5.getItemStack(1, 0);
		final ItemStack poplarLog = ForestryBlock.log5.getItemStack(1, 1);
		final ItemStack dateLog =   ForestryBlock.log5.getItemStack(1, 2);
		final ItemStack papayaLog = ForestryBlock.log5.getItemStack(1, 3);

		final ItemStack pineLog =  ForestryBlock.log6.getItemStack(1, 0);
		final ItemStack plumLog =  ForestryBlock.log6.getItemStack(1, 1);
		final ItemStack mapleLog = ForestryBlock.log6.getItemStack(1, 2);
		final ItemStack lemonLog = ForestryBlock.log6.getItemStack(1, 3);

		final ItemStack giganteumLog = ForestryBlock.log7.getItemStack(1, 0);

		// Deciduous
		Allele.treeOak = new AlleleTreeSpecies("treeOak", false, "appleOak", quercus, "robur",
				proxy.getFoliageColorBasic(), WorldGenOak.class, oakLog).addFruitFamily(pomes)
				.setVanillaMap(0);

		Allele.treeDarkOak = new AlleleTreeSpecies("treeDarkOak", false, "darkOak", quercus, "velutina",
				proxy.getFoliageColorBasic(), WorldGenDarkOak.class, darkOakLog).addFruitFamily(pomes)
				.setVanillaMap(5);

		Allele.treeBirch = new AlleleTreeSpecies("treeBirch", false, "silverBirch", betula,
				"pendula", proxy.getFoliageColorBirch(), 0xb0c648, WorldGenBirch.class, birchLog)
				.setVanillaMap(2);
		Allele.treeLime = new AlleleTreeSpecies("treeLime", true, "silverLime", tilia,
				"tomentosa", 0x5ea107, WorldGenLime.class, limeLog).addFruitFamily(nux)
				.addFruitFamily(prunes).addFruitFamily(pomes);

		// Nucifera
		Allele.treeWalnut = new AlleleTreeSpecies("treeWalnut", true, "commonWalnut", juglans,
				"regia", 0x798c55, 0xb0c648, WorldGenWalnut.class, walnutLog).addFruitFamily(nux)
				.addFruitFamily(prunes).addFruitFamily(pomes).setGirth(2);
		Allele.treeChestnut = new AlleleTreeSpecies("treeChestnut", true, "sweetChestnut",
				castanea, "sativa", 0x5ea107, 0xb0c648, WorldGenChestnut.class, chestnutLog).addFruitFamily(nux)
				.addFruitFamily(prunes).addFruitFamily(pomes).setGirth(2);

		// Prunus & Citrus
		// <CovertJaguar> fructifer, annifer, bifer, aurifer = bearing fruit,
		// bearing fruit year round, bearing fruit twice a year, bearing golden
		// fruit
		Allele.treeCherry = new AlleleTreeSpecies("treeCherry", true, "hillCherry", prunus,
				"serrulata", 0xe691da, 0xe63e59, WorldGenCherry.class, cherryLog).addFruitFamily(prunes)
				.addFruitFamily(pomes);
		Allele.treeLemon = new AlleleTreeSpecies("treeLemon", true, "lemon", citrus, "limon",
				0x88af54, 0xa3b850, WorldGenLemon.class, lemonLog).addFruitFamily(pomes).addFruitFamily(
						prunes);
		Allele.treePlum = new AlleleTreeSpecies("treePlum", true, "plum", prunus, "domestica",
				0x589246, 0xa3b850, WorldGenPlum.class, plumLog).addFruitFamily(pomes)
				.addFruitFamily(prunes);

		// Maples
		Allele.treeMaple = new AlleleTreeSpecies("treeMaple", true, "sugarMaple", acer,
				"saccharum", 0xd4f425, 0x619a3c, WorldGenMaple.class, mapleLog).addFruitFamily(prunes)
				.addFruitFamily(pomes).setLeafIndices("maple");

		// Conifers
		Allele.treeSpruce = new AlleleTreeSpecies("treeSpruce", false, "redSpruce", picea,
				"abies", proxy.getFoliageColorPine(), 0x539d12, WorldGenSpruce.class, spruceLog)
				.setLeafIndices("conifers").setVanillaMap(1);
		Allele.treeLarch = new AlleleTreeSpecies("treeLarch", true, "mundaneLarch", larix,
				"decidua", 0x698f90, 0x569896, WorldGenLarch.class, larchLog).setLeafIndices("conifers");
		Allele.treePine = new AlleleTreeSpecies("treePine", true, "bullPine", pinus, "sabiniana",
				0xfeff8f, 0xffd98f, WorldGenPine.class, pineLog).setLeafIndices("conifers");

		Allele.treeSequioa = new AlleleTreeSpecies("treeSequioa", false, "coastSequoia", sequoia,
				"sempervirens", 0x418e71, 0x569896, WorldGenSequoia.class, sequioaLog).setLeafIndices(
						"conifers").setGirth(3);
		Allele.treeGiganteum = new AlleleTreeSpecies("treeGigant", false, "giantSequoia",
				sequoiadendron, "giganteum", 0x738434, WorldGenGiganteum.class, giganteumLog).setLeafIndices(
						"conifers").setGirth(4);

		// Jungle
		Allele.treeJungle = new AlleleTreeSpecies("treeJungle", false, "jungle", tropical,
				"tectona", proxy.getFoliageColorBasic(), 0x539d12, WorldGenJungle.class, jungleLog)
				.addFruitFamily(jungle).setLeafIndices("jungle").setVanillaMap(3);
		Allele.treeTeak = new AlleleTreeSpecies("treeTeak", true, "teak", tectona, "grandis",
				0xfeff8f, 0xffd98f, WorldGenTeak.class, teakLog).addFruitFamily(jungle).setLeafIndices(
						"jungle");
		Allele.treeKapok = new AlleleTreeSpecies("treeKapok", true, "kapok", ceiba, "pentandra",
				0x89987b, 0x89aa9e, WorldGenKapok.class, kapokLog).addFruitFamily(jungle)
				.addFruitFamily(prunes).setLeafIndices("jungle");

		// Ebony
		Allele.treeEbony = new AlleleTreeSpecies("treeEbony", true, "myrtleEbony", diospyros,
				"pentamera", 0xa2d24a, 0xc4d24a, WorldGenEbony.class, ebonyLog).addFruitFamily(jungle)
				.addFruitFamily(prunes).setGirth(3).setLeafIndices("jungle");

		// Diospyros mespiliformis, the Jackalberry (also known as African Ebony
		// The Gaub Tree, Malabar ebony, Black-and-white Ebony or Pale Moon
		// Ebony (Diospyros malabarica)
		// Diospyros fasciculosa, is a rainforest tree in the Ebony family.
		// Usually seen as a medium sized tree, but it may grow to 30 metres
		// tall.
		// http://en.wikipedia.org/wiki/Diospyros_ebenum
		// http://en.wikipedia.org/wiki/Diospyros_crassiflora -
		// "The wood this particular tree produces is believed to be the blackest of all timber-producing Diospyros species"
		// Coromandel Ebony or East Indian Ebony (Diospyros melanoxylon) -
		// " locally it is known as temburini or by its Hindi name tendu. In Orissa and Jharkhand it known as kendu."
		// Mahogany
		Allele.treeMahogany = new AlleleTreeSpecies("treeMahogony", true, "yellowMeranti", shorea,
				"gibbosa", 0x8ab154, 0xa9b154, WorldGenMahogany.class, mahoganyLog).addFruitFamily(jungle)
				.setGirth(2).setLeafIndices("jungle");

		// 80+ meters tall:
		// Shorea argentifolia (also called Dark Red Meranti)
		// Shorea gibbosa (also called Yellow Meranti)
		// Shorea smithiana (also called Light Red Meranti)
		// Shorea superba
		// Malva
		Allele.treeAcacia = new AlleleTreeSpecies("treeAcaciaVanilla", true, "acacia", acacia,
				"aneura", 0x616101, 0xb3b302, WorldGenAcaciaVanilla.class, acaciaLog).addFruitFamily(jungle)
				.addFruitFamily(nux).setVanillaMap(4);

		Allele.treeDesertAcacia = new AlleleTreeSpecies("treeAcacia", true, "desertAcacia", acacia,
				"erioloba", 0x748C1C, 0xb3b302, WorldGenAcacia.class, desertAcaciaLog).addFruitFamily(jungle)
				.addFruitFamily(nux);
		Allele.treeBalsa = new AlleleTreeSpecies("treeBalsa", true, "balsa", ochroma, "pyramidale",
				0x59ac00, 0xfeff8f, WorldGenBalsa.class, balsaLog).addFruitFamily(jungle).addFruitFamily(nux);
		Allele.treeWenge = new AlleleTreeSpecies("treeWenge", true, "wenge", millettia,
				"laurentii", 0xada157, 0xad8a57, WorldGenWenge.class, wengeLog).addFruitFamily(jungle)
				.addFruitFamily(nux).setGirth(2);
		Allele.treeBaobab = new AlleleTreeSpecies("treeBaobab", true, "grandidierBaobab",
				adansonia, "digitata", 0xfeff8f, 0xffd98f, WorldGenBaobab.class, baobabLog)
				.addFruitFamily(jungle).addFruitFamily(nux).setGirth(3);
		Allele.treeMahoe = new AlleleTreeSpecies("treeMahoe", true, "blueMahoe", talipariti,
				"elatum", 0xa0ba1b, 0x79a175, WorldGenMahoe.class, mahoeLog).addFruitFamily(jungle)
				.addFruitFamily(pomes).addFruitFamily(prunes);

		// Willows
		Allele.treeWillow = new AlleleTreeSpecies("treeWillow", true, "whiteWillow", salix,
				"alba", 0xa3b8a5, 0xa3b850, WorldGenWillow.class, willowLog).addFruitFamily(nux)
				.addFruitFamily(prunes).addFruitFamily(pomes).setLeafIndices("willow");

		// Lauraceae
		Allele.treeSipiri = new AlleleTreeSpecies("treeSipiri", true, "sipiri", chlorocardium,
				"rodiei", 0x678911, 0x79a175, WorldGenGreenheart.class, sipiriLog).addFruitFamily(jungle);

		// Unclassified
		Allele.treePapaya = new AlleleTreeSpecies("treePapaya", true, "papaya", carica, "papaya",
				0x6d9f58, 0x9ee67f, WorldGenPapaya.class, papayaLog).addFruitFamily(jungle)
				.addFruitFamily(nux).setLeafIndices("palm");
		Allele.treeDate = new AlleleTreeSpecies("treeDate", true, "datePalm", null, "dactylifera",
				0xcbcd79, 0xf0f38f, WorldGenDate.class, dateLog).addFruitFamily(jungle).addFruitFamily(nux)
				.setLeafIndices("palm");

		Allele.treePoplar = new AlleleTreeSpecies("treePoplar", true, "whitePoplar", populus,
				"alba", 0xa3b8a5, 0x539d12, WorldGenPoplar.class, poplarLog).addFruitFamily(pomes)
				.addFruitFamily(prunes);

		// FRUITS
		Allele.fruitNone = new AlleleFruit("fruitNone", new FruitProviderNone("none", null));
		Allele.fruitApple = new AlleleFruit("fruitApple", new FruitProviderRandom("apple", pomes, new ItemStack(Items.apple), 1.0f).setColour(0xff2e2e).setOverlay("pomes"));
		Allele.fruitCocoa = new AlleleFruit("fruitCocoa", new FruitProviderPod("cocoa", jungle, EnumPodType.COCOA));
		// .setColours(0xecdca5, 0xc4d24a), true);
		Allele.fruitChestnut = new AlleleFruit("fruitChestnut", new FruitProviderRipening("chestnut", nux, ForestryItem.fruits.getItemStack(1, EnumFruit.CHESTNUT.ordinal()), 1.0f).setRipeningPeriod(6).setColours(0x7f333d, 0xc4d24a).setOverlay("nuts"), true);
		Allele.fruitWalnut = new AlleleFruit("fruitWalnut", new FruitProviderRipening("walnut", nux, ForestryItem.fruits.getItemStack(1, EnumFruit.WALNUT.ordinal()), 1.0f).setRipeningPeriod(8).setColours(0xfba248, 0xc4d24a).setOverlay("nuts"), true);
		Allele.fruitCherry = new AlleleFruit("fruitCherry", new FruitProviderRipening("cherry", prunes, ForestryItem.fruits.getItemStack(1, EnumFruit.CHERRY.ordinal()), 1.0f).setColours(0xff2e2e, 0xc4d24a).setOverlay("berries"), true);
		Allele.fruitDates = new AlleleFruit("fruitDates", new FruitProviderPod("dates", jungle, EnumPodType.DATES, ForestryItem.fruits.getItemStack(4, EnumFruit.DATES.ordinal())));
		Allele.fruitPapaya = new AlleleFruit("fruitPapaya", new FruitProviderPod("papaya", jungle, EnumPodType.PAPAYA, ForestryItem.fruits.getItemStack(1, EnumFruit.PAPAYA.ordinal())));
		// Allele.fruitCoconut = new AlleleFruit("fruitCoconut", new
		// FruitProviderPod("coconut", jungle, EnumPodType.COCONUT, new
		// ItemStack[] { new ItemStack(
		// ForestryItem.fruits, 1, EnumFruit.COCONUT.ordinal()) }));
		Allele.fruitLemon = new AlleleFruit("fruitLemon", new FruitProviderRipening("lemon", prunes, ForestryItem.fruits.getItemStack(1, EnumFruit.LEMON.ordinal()), 1.0f).setColours(0xeeee00, 0x99ff00).setOverlay("citrus"), true);
		Allele.fruitPlum = new AlleleFruit("fruitPlum", new FruitProviderRipening("plum", prunes, ForestryItem.fruits.getItemStack(1, EnumFruit.PLUM.ordinal()), 1.0f).setColours(0x663446, 0xeeff1a).setOverlay("plums"), true);

		// / TREES // GROWTH PROVIDER 1350 - 1399
		Allele.growthLightlevel = new AlleleGrowth("growthLightlevel", new GrowthProvider());
		Allele.growthAcacia = new AlleleGrowth("growthAcacia", new GrowthProvider());
		Allele.growthTropical = new AlleleGrowth("growthTropical", new GrowthProviderTropical());

		// / TREES // EFFECTS 1900 - 1999
		Allele.leavesNone = new AlleleLeafEffectNone("leavesNone");

	}

	private void registerTemplates() {
		treeInterface.registerTemplate(TreeTemplates.getOakTemplate());
		treeInterface.registerTemplate(TreeTemplates.getBirchTemplate());
		treeInterface.registerTemplate(TreeTemplates.getSpruceTemplate());
		treeInterface.registerTemplate(TreeTemplates.getJungleTemplate());
		treeInterface.registerTemplate(TreeTemplates.getAcaciaTemplate());
		treeInterface.registerTemplate(TreeTemplates.getDarkOakTemplate());

		treeInterface.registerTemplate(TreeTemplates.getLimeTemplate());
		treeInterface.registerTemplate(TreeTemplates.getCherryTemplate());
		treeInterface.registerTemplate(TreeTemplates.getChestnutTemplate());
		treeInterface.registerTemplate(TreeTemplates.getWalnutTemplate());

		treeInterface.registerTemplate(TreeTemplates.getLarchTemplate());
		treeInterface.registerTemplate(TreeTemplates.getPineTemplate());
		treeInterface.registerTemplate(TreeTemplates.getSequoiaTemplate());
		treeInterface.registerTemplate(TreeTemplates.getGiganteumTemplate());

		treeInterface.registerTemplate(TreeTemplates.getBalsaTemplate());
		treeInterface.registerTemplate(TreeTemplates.getDesertAcaciaTemplate());
		treeInterface.registerTemplate(TreeTemplates.getWengeTemplate());
		treeInterface.registerTemplate(TreeTemplates.getBaobabTemplate());

		treeInterface.registerTemplate(TreeTemplates.getTeakTemplate());
		treeInterface.registerTemplate(TreeTemplates.getKapokTemplate());
		treeInterface.registerTemplate(TreeTemplates.getEbonyTemplate());
		treeInterface.registerTemplate(TreeTemplates.getMahoganyTemplate());

		treeInterface.registerTemplate(TreeTemplates.getWillowTemplate());

		treeInterface.registerTemplate(TreeTemplates.getSipiriTemplate());

		treeInterface.registerTemplate(TreeTemplates.getMahoeTemplate());
		treeInterface.registerTemplate(TreeTemplates.getPoplarTemplate());

		treeInterface.registerTemplate(TreeTemplates.getLemonTemplate());
		treeInterface.registerTemplate(TreeTemplates.getPlumTemplate());
		treeInterface.registerTemplate(TreeTemplates.getMapleTemplate());

		treeInterface.registerTemplate(TreeTemplates.getPapayaTemplate());
		treeInterface.registerTemplate(TreeTemplates.getDateTemplate());
	}

	private void registerErsatzGenomes() {
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves, 1, 0), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getOakTemplate())));
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves, 1, 1), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getSpruceTemplate())));
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves, 1, 2), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getBirchTemplate())));
		AlleleManager.ersatzSpecimen.put(new ItemStack(Blocks.leaves, 1, 3), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getJungleTemplate())));

		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 0), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getOakTemplate())));
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 1), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getSpruceTemplate())));
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 2), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getBirchTemplate())));
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 3), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getJungleTemplate())));
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 4), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getAcaciaTemplate())));
		AlleleManager.ersatzSaplings.put(new ItemStack(Blocks.sapling, 1, 5), new Tree(
				TreeTemplates.templateAsGenome(TreeTemplates.getDarkOakTemplate())));
	}

	private void createMutations() {

		// Decidious
		TreeTemplates.limeA = new TreeMutation(Allele.treeBirch, Allele.treeOak,
				TreeTemplates.getLimeTemplate(), 15);
		TreeTemplates.mapleA = new TreeMutation(Allele.treeLime, Allele.treeLarch,
				TreeTemplates.getMapleTemplate(), 5);

		// Fructifera
		TreeTemplates.cherryA = new TreeMutation(Allele.treeLime, Allele.treeOak,
				TreeTemplates.getCherryTemplate(), 10);
		TreeTemplates.cherryB = new TreeMutation(Allele.treeLime, Allele.treeBirch,
				TreeTemplates.getCherryTemplate(), 10);
		TreeTemplates.lemonA = new TreeMutation(Allele.treeLime, Allele.treeCherry,
				TreeTemplates.getLemonTemplate(), 5);
		TreeTemplates.plumA = new TreeMutation(Allele.treeLemon, Allele.treeCherry,
				TreeTemplates.getPlumTemplate(), 5);

		// Nucifera
		TreeTemplates.walnutA = new TreeMutation(Allele.treeLime, Allele.treeCherry,
				TreeTemplates.getWalnutTemplate(), 10);
		TreeTemplates.chestnutA = new TreeMutation(Allele.treeWalnut, Allele.treeLime,
				TreeTemplates.getChestnutTemplate(), 10);
		TreeTemplates.chestnutB = new TreeMutation(Allele.treeWalnut, Allele.treeCherry,
				TreeTemplates.getChestnutTemplate(), 10);

		// Conifera
		TreeTemplates.larchA = new TreeMutation(Allele.treeSpruce, Allele.treeBirch,
				TreeTemplates.getLarchTemplate(), 10);
		TreeTemplates.larchB = new TreeMutation(Allele.treeSpruce, Allele.treeOak,
				TreeTemplates.getLarchTemplate(), 10);
		TreeTemplates.pineA = new TreeMutation(Allele.treeSpruce, Allele.treeLarch,
				TreeTemplates.getPineTemplate(), 10);
		TreeTemplates.sequoiaA = new TreeMutation(Allele.treeLarch, Allele.treePine,
				TreeTemplates.getSequoiaTemplate(), 5);

		// Tropical
		TreeTemplates.teakA = new TreeMutation(Allele.treeLime, Allele.treeJungle,
				TreeTemplates.getTeakTemplate(), 10);
		TreeTemplates.kapokA = new TreeMutation(Allele.treeJungle, Allele.treeTeak,
				TreeTemplates.getKapokTemplate(), 10);
		TreeTemplates.ebonyA = new TreeMutation(Allele.treeKapok, Allele.treeTeak,
				TreeTemplates.getEbonyTemplate(), 10);
		TreeTemplates.mahoganyA = new TreeMutation(Allele.treeKapok, Allele.treeEbony,
				TreeTemplates.getMahoganyTemplate(), 10);

		TreeTemplates.papayaA = new TreeMutation(Allele.treeJungle, Allele.treeCherry,
				TreeTemplates.getPapayaTemplate(), 5);
		TreeTemplates.dateA = new TreeMutation(Allele.treeJungle, Allele.treePapaya,
				TreeTemplates.getDateTemplate(), 5);

		// Malva
		TreeTemplates.balsaA = new TreeMutation(Allele.treeTeak, Allele.treeLime,
				TreeTemplates.getBalsaTemplate(), 10);
		TreeTemplates.acaciaA = new TreeMutation(Allele.treeTeak, Allele.treeBalsa,
				TreeTemplates.getDesertAcaciaTemplate(), 10);
		TreeTemplates.wengeA = new TreeMutation(Allele.treeDesertAcacia, Allele.treeBalsa,
				TreeTemplates.getWengeTemplate(), 10);
		TreeTemplates.baobabA = new TreeMutation(Allele.treeBalsa, Allele.treeWenge,
				TreeTemplates.getBaobabTemplate(), 10);
		TreeTemplates.mahoeA = new TreeMutation(Allele.treeBirch, Allele.treeDesertAcacia,
				TreeTemplates.getMahoeTemplate(), 5);

		TreeTemplates.willowA = new TreeMutation(Allele.treeOak, Allele.treeBirch,
				TreeTemplates.getWillowTemplate(), 10).setTemperatureRainfall(0.7f, 1.5f, 0.9f,
						2.0f);
		TreeTemplates.willowB = new TreeMutation(Allele.treeOak, Allele.treeLime,
				TreeTemplates.getWillowTemplate(), 10).setTemperatureRainfall(0.7f, 1.5f, 0.9f,
						2.0f);
		TreeTemplates.willowC = new TreeMutation(Allele.treeLime, Allele.treeBirch,
				TreeTemplates.getWillowTemplate(), 10).setTemperatureRainfall(0.7f, 1.5f, 0.9f,
						2.0f);

		TreeTemplates.sipiriA = new TreeMutation(Allele.treeKapok, Allele.treeMahogany,
				TreeTemplates.getSipiriTemplate(), 10).setTemperatureRainfall(0.9f, 1.9f, 0.9f,
						2.0f);

		TreeTemplates.poplarA = new TreeMutation(Allele.treeBirch, Allele.treeWillow,
				TreeTemplates.getPoplarTemplate(), 5);
		TreeTemplates.poplarB = new TreeMutation(Allele.treeOak, Allele.treeWillow,
				TreeTemplates.getPoplarTemplate(), 5);
		TreeTemplates.poplarB = new TreeMutation(Allele.treeLime, Allele.treeWillow,
				TreeTemplates.getPoplarTemplate(), 5);

	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerArboriculture();
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[]{new CommandSpawnTree(), new CommandSpawnForest(),
			new CommandTreekeepingMode()};
	}

	@Override
	public IFuelHandler getFuelHandler() {
		return new IFuelHandler() {
			@Override
			public int getBurnTime(ItemStack fuel) {
				if (ForestryItem.sapling.isItemEqual(fuel))
					return 100;

				return 0;
			}
		};
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerArboriculture();
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-fence-block") && message.isStringMessage()) {
			Block block = GameData.getBlockRegistry().getRaw(message.getStringValue());

			if (block == null || block == Blocks.air)
				Proxies.log.warning("invalid add-fence-block IMC: can't resolve block name %s.", message.getStringValue());
			else
				validFences.add(block);
		}
		return super.processIMCMessage(message);
	}

	private void registerDungeonLoot() {
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(ForestryItem.grafter.getItemStack(), 1, 1, 8));

		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getOakTemplate(), EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getSpruceTemplate(), EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getBirchTemplate(), EnumGermlingType.SAPLING), 2, 3, 6));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getLarchTemplate(), EnumGermlingType.SAPLING), 1, 2, 4));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getLimeTemplate(), EnumGermlingType.SAPLING), 1, 2, 4));

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getOakTemplate(), EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getSpruceTemplate(), EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getBirchTemplate(), EnumGermlingType.POLLEN), 2, 3, 4));
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getLarchTemplate(), EnumGermlingType.POLLEN), 1, 2, 3));
			ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getLimeTemplate(), EnumGermlingType.POLLEN), 1, 2, 3));
		}
	}

	private ItemStack getTreeItemFromTemplate(IAllele[] template, EnumGermlingType type) {
		ITree tree = new Tree(PluginArboriculture.treeInterface.templateAsGenome(template));
		ItemStack treeItem;
		switch (type) {
		default:
		case POLLEN:
			treeItem = ForestryItem.pollenFertile.getItemStack();
			break;
		case SAPLING:
			treeItem = ForestryItem.sapling.getItemStack();
		}
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		tree.writeToNBT(nbtTagCompound);
		treeItem.setTagCompound(nbtTagCompound);
		return treeItem;
	}
}
