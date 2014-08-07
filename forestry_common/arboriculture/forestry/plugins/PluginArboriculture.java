/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.plugins;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
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
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.PluginInfo;
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
import forestry.arboriculture.gadgets.BlockFruitPod;
import forestry.arboriculture.gadgets.BlockLeaves;
import forestry.arboriculture.gadgets.BlockLog;
import forestry.arboriculture.gadgets.BlockLog.LogCat;
import forestry.arboriculture.gadgets.BlockPlanks;
import forestry.arboriculture.gadgets.BlockPlanks.PlankCat;
import forestry.arboriculture.gadgets.BlockSapling;
import forestry.arboriculture.gadgets.BlockSlab;
import forestry.arboriculture.gadgets.BlockSlab.SlabCat;
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
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.genetics.Allele;
import forestry.core.genetics.FruitFamily;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemForestryBlock;
import forestry.core.items.ItemFruit.EnumFruit;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.RecipeUtil;
import forestry.core.utils.ShapedRecipeCustom;

@PluginInfo(pluginID = "Arboriculture", name = "Arboriculture", author = "Binnie & SirSengir", url = Defaults.URL, description = "Adds additional tree species and products.")
public class PluginArboriculture extends NativePlugin implements IFuelHandler {

	@SidedProxy(clientSide = "forestry.arboriculture.proxy.ClientProxyArboriculture", serverSide = "forestry.arboriculture.proxy.ProxyArboriculture")
	public static ProxyArboriculture proxy;
	public static String treekeepingMode = "NORMAL";
	public static int modelIdSaplings;
	public static int modelIdLeaves;
	public static int modelIdFences;
	public static int modelIdPods;
	public static ITreeRoot treeInterface;
	public static MachineDefinition definitionChest;
	public static List<Block> validFences = new ArrayList<Block>();

	@Override
	public boolean isAvailable() {
		return !Config.disableArboriculture;
	}

	@Override
	public void preInit() {
		super.preInit();

		ForestryBlock.log1 = new BlockLog(LogCat.CAT0).setBlockName("for.log1");
		Proxies.common.registerBlock(ForestryBlock.log1, ItemWoodBlock.class);
		ForestryBlock.log1.setHarvestLevel("axe", 0);

		ForestryBlock.log2 = new BlockLog(LogCat.CAT1).setBlockName("for.log2");
		Proxies.common.registerBlock(ForestryBlock.log2, ItemWoodBlock.class);
		ForestryBlock.log2.setHarvestLevel("axe", 0);

		ForestryBlock.log3 = new BlockLog(LogCat.CAT2).setBlockName("for.log3");
		Proxies.common.registerBlock(ForestryBlock.log3, ItemWoodBlock.class);
		ForestryBlock.log3.setHarvestLevel("axe", 0);

		ForestryBlock.log4 = new BlockLog(LogCat.CAT3).setBlockName("for.log4");
		Proxies.common.registerBlock(ForestryBlock.log4, ItemWoodBlock.class);
		ForestryBlock.log4.setHarvestLevel("axe", 0);

		ForestryBlock.log5 = new BlockLog(LogCat.CAT4).setBlockName("for.log5");
		Proxies.common.registerBlock(ForestryBlock.log5, ItemWoodBlock.class);
		ForestryBlock.log5.setHarvestLevel("axe", 0);

		ForestryBlock.log6 = new BlockLog(LogCat.CAT5).setBlockName("for.log6");
		Proxies.common.registerBlock(ForestryBlock.log6, ItemWoodBlock.class);
		ForestryBlock.log6.setHarvestLevel("axe", 0);

		ForestryBlock.log7 = new BlockLog(LogCat.CAT6).setBlockName("for.log7");
		Proxies.common.registerBlock(ForestryBlock.log7, ItemWoodBlock.class);
		ForestryBlock.log7.setHarvestLevel("axe", 0);

		// Register as workableLogs
		OreDictionary.registerOre("logWood",
				new ItemStack(ForestryBlock.log1, 1, Defaults.WILDCARD));
		OreDictionary.registerOre("logWood",
				new ItemStack(ForestryBlock.log2, 1, Defaults.WILDCARD));
		OreDictionary.registerOre("logWood",
				new ItemStack(ForestryBlock.log3, 1, Defaults.WILDCARD));
		OreDictionary.registerOre("logWood",
				new ItemStack(ForestryBlock.log4, 1, Defaults.WILDCARD));
		OreDictionary.registerOre("logWood",
				new ItemStack(ForestryBlock.log5, 1, Defaults.WILDCARD));
		OreDictionary.registerOre("logWood",
				new ItemStack(ForestryBlock.log6, 1, Defaults.WILDCARD));
		OreDictionary.registerOre("logWood",
				new ItemStack(ForestryBlock.log7, 1, Defaults.WILDCARD));

		// Register smelting
		FurnaceRecipes.smelting().func_151393_a(ForestryBlock.log1,
				new ItemStack(Items.coal, 1, 1), 0.15F);
		FurnaceRecipes.smelting().func_151393_a(ForestryBlock.log2,
				new ItemStack(Items.coal, 1, 1), 0.15F);
		FurnaceRecipes.smelting().func_151393_a(ForestryBlock.log3,
				new ItemStack(Items.coal, 1, 1), 0.15F);
		FurnaceRecipes.smelting().func_151393_a(ForestryBlock.log4,
				new ItemStack(Items.coal, 1, 1), 0.15F);
		FurnaceRecipes.smelting().func_151393_a(ForestryBlock.log5,
				new ItemStack(Items.coal, 1, 1), 0.15F);
		FurnaceRecipes.smelting().func_151393_a(ForestryBlock.log6,
				new ItemStack(Items.coal, 1, 1), 0.15F);

		ForestryBlock.planks1 = new BlockPlanks(PlankCat.CAT0).setBlockName("for.planks");
		Proxies.common.registerBlock(ForestryBlock.planks1, ItemWoodBlock.class);
		ForestryBlock.planks1.setHarvestLevel("axe", 0);

		ForestryBlock.planks2 = new BlockPlanks(PlankCat.CAT1).setBlockName("for.planks2");
		Proxies.common.registerBlock(ForestryBlock.planks2, ItemWoodBlock.class);
		ForestryBlock.planks2.setHarvestLevel("axe", 0);

		// Register as craftablePlanks
		OreDictionary.registerOre("plankWood", new ItemStack(ForestryBlock.planks1, 1,
				Defaults.WILDCARD));
		OreDictionary.registerOre("plankWood", new ItemStack(ForestryBlock.planks2, 1,
				Defaults.WILDCARD));

		ForestryBlock.slabs1 = new BlockSlab(SlabCat.CAT0).setBlockName("for.slabs1");
		Proxies.common.registerBlock(ForestryBlock.slabs1, ItemWoodBlock.class);
		ForestryBlock.slabs1.setHarvestLevel("axe", 0);

		ForestryBlock.slabs2 = new BlockSlab(SlabCat.CAT1).setBlockName("for.slabs2");
		Proxies.common.registerBlock(ForestryBlock.slabs2, ItemWoodBlock.class);
		ForestryBlock.slabs2.setHarvestLevel("axe", 0);

		ForestryBlock.slabs3 = new BlockSlab(SlabCat.CAT2).setBlockName("for.slabs3");
		Proxies.common.registerBlock(ForestryBlock.slabs3, ItemWoodBlock.class);
		ForestryBlock.slabs3.setHarvestLevel("axe", 0);

		OreDictionary.registerOre("slabWood", new ItemStack(ForestryBlock.slabs1, 1,
				Defaults.WILDCARD));
		OreDictionary.registerOre("slabWood", new ItemStack(ForestryBlock.slabs2, 1,
				Defaults.WILDCARD));
		OreDictionary.registerOre("slabWood", new ItemStack(ForestryBlock.slabs3, 1,
				Defaults.WILDCARD));

		// Fences
		ForestryBlock.fences1 = new BlockArbFence(FenceCat.CAT0).setBlockName("for.fences");
		Proxies.common.registerBlock(ForestryBlock.fences1, ItemWoodBlock.class);
		ForestryBlock.fences1.setHarvestLevel("axe", 0);

		ForestryBlock.fences2 = new BlockArbFence(FenceCat.CAT1).setBlockName("for.fences2");
		Proxies.common.registerBlock(ForestryBlock.fences2, ItemWoodBlock.class);
		ForestryBlock.fences2.setHarvestLevel("axe", 0);

		// Stairs
		ForestryBlock.stairs = new BlockArbStairs(ForestryBlock.planks1, 0) .setBlockName("for.stairs");
		Proxies.common.registerBlock(ForestryBlock.stairs, ItemStairs.class);
		ForestryBlock.stairs.setHarvestLevel("axe", 0);

		// Saplings
		ForestryBlock.saplingGE = new BlockSapling().setBlockName("saplingGE");
		Proxies.common.registerBlock(ForestryBlock.saplingGE, ItemForestryBlock.class);

		// Leaves
		ForestryBlock.leaves = new BlockLeaves().setBlockName("leaves");
		Proxies.common.registerBlock(ForestryBlock.leaves, ItemForestryBlock.class);

		// Pods
		ForestryBlock.pods = new BlockFruitPod().setBlockName("for.pods");
		Proxies.common.registerBlock(ForestryBlock.pods, ItemForestryBlock.class);

		// Machines
		ForestryBlock.arboriculture = new BlockBase(Material.iron);
		ForestryBlock.arboriculture.setBlockName("for.arboriculture").setCreativeTab(Tabs.tabArboriculture);
		Proxies.common.registerBlock(ForestryBlock.arboriculture, ItemForestryBlock.class);

		definitionChest = ForestryBlock.arboriculture.addDefinition(new MachineDefinition(Defaults.DEFINITION_ARBCHEST_META,
				"forestry.ArbChest", TileArboristChest.class,
				ShapedRecipeCustom.createShapedRecipe(new ItemStack(ForestryBlock.arboriculture,
						1, Defaults.DEFINITION_ARBCHEST_META), new Object[] { " # ", "XYX", "XXX", '#',
					Blocks.glass, 'X', "treeSapling", 'Y', Blocks.chest })).setFaces(0, 1, 2, 3, 4, 4, 0, 7));



		// Init tree interface
		AlleleManager.alleleRegistry
		.registerSpeciesRoot(PluginArboriculture.treeInterface = TreeManager.treeInterface = new TreeHelper());

		// Init rendering
		proxy.initializeRendering();

		// Create alleles
		createAlleles();

		// Register vanilla and forestry fence ids
		validFences.add(ForestryBlock.fences1);
		validFences.add(ForestryBlock.fences2);
		validFences.add(Blocks.fence);
		validFences.add(Blocks.fence_gate);
		validFences.add(Blocks.nether_brick_fence);

	}

	@Override
	public void doInit() {
		super.doInit();

		proxy.addLocalizations();

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
		OreDictionary.registerOre("treeSapling", ForestryItem.sapling.getItemStack(1, -1));

		ForestryItem.pollenFertile.registerItem(new ItemGermlingGE(EnumGermlingType.POLLEN), "pollenFertile");

		ForestryItem.treealyzer.registerItem(new ItemTreealyzer(), "treealyzer");
		ForestryItem.grafter.registerItem(new ItemGrafter(4), "grafter");
		ForestryItem.grafterProven.registerItem(new ItemGrafter(149), "grafterProven");

	}

	@Override
	protected void registerBackpackItems() {
		if (BackpackManager.backpackItems == null)
			return;

		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log1, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log2, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log3, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log4, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log5, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log6, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryBlock.log7, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryItem.sapling.item(), 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("forester").addValidItem(new ItemStack(ForestryItem.fruits.item(), 1, Defaults.WILDCARD));

		BackpackManager.definitions.get("builder").addValidItem(
				new ItemStack(ForestryBlock.stairs, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("builder").addValidItem(
				new ItemStack(ForestryBlock.slabs1, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("builder").addValidItem(
				new ItemStack(ForestryBlock.slabs2, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("builder").addValidItem(
				new ItemStack(ForestryBlock.slabs3, 1, Defaults.WILDCARD));
		// BackpackManager.definitions.item("builder").addValidItem(new
		// ItemStack(ForestryBlock.slabs4, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("builder").addValidItem(
				new ItemStack(ForestryBlock.fences1, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("builder").addValidItem(
				new ItemStack(ForestryBlock.fences2, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("builder").addValidItem(
				new ItemStack(ForestryBlock.planks1, 1, Defaults.WILDCARD));
		BackpackManager.definitions.get("builder").addValidItem(
				new ItemStack(ForestryBlock.planks2, 1, Defaults.WILDCARD));
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	protected void registerRecipes() {

		// / Plank recipes
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks1, 4, i),
					new ItemStack(ForestryBlock.log1, 1, i));
		}
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks1, 4, 4 + i),
					new ItemStack(ForestryBlock.log2, 1, i));
		}
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks1, 4, 8 + i),
					new ItemStack(ForestryBlock.log3, 1, i));
		}
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks1, 4, 12 + i),
					new ItemStack(ForestryBlock.log4, 1, i));
		}
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks2, 4, i),
					new ItemStack(ForestryBlock.log5, 1, i));
		}
		for (int i = 0; i < 4; i++) {
			Proxies.common.addShapelessRecipe(new ItemStack(ForestryBlock.planks2, 4, 4 + i),
					new ItemStack(ForestryBlock.log6, 1, i));
		}

		// Slab recipes
		for (int i = 0; i < 8; i++) {
			Proxies.common.addPriorityRecipe(new ItemStack(ForestryBlock.slabs1, 6, i), "###", '#', new ItemStack(ForestryBlock.planks1, 1, i));
		}
		for (int i = 0; i < 8; i++) {
			Proxies.common.addPriorityRecipe(new ItemStack(ForestryBlock.slabs2, 6, i), "###", '#', new ItemStack(ForestryBlock.planks1, 1, 8 + i));
		}
		for (int i = 0; i < 8; i++) {
			Proxies.common.addPriorityRecipe(new ItemStack(ForestryBlock.slabs3, 6, i), "###", '#', new ItemStack(ForestryBlock.planks2, 1, i));
		}

		// Fence recipes
		for (int i = 0; i < 16; i++) {
			Proxies.common.addRecipe(new ItemStack(ForestryBlock.fences1, 4, i), "###", "# #", '#', new ItemStack(ForestryBlock.planks1, 1, i));
		}
		for (int i = 0; i < 8; i++) {
			Proxies.common.addRecipe(new ItemStack(ForestryBlock.fences2, 4, i), "###", "# #", '#', new ItemStack(ForestryBlock.planks2, 1, i));
		}

		// Treealyzer
		RecipeManagers.carpenterManager.addRecipe(100, LiquidHelper.getLiquid(Defaults.LIQUID_WATER, 2000), null, ForestryItem.treealyzer.getItemStack(), new Object[] {
			"X#X", "X#X", "RDR",
			'#', Blocks.glass_pane,
			'X', "ingotCopper",
			'R', Items.redstone,
			'D', Items.diamond });

		// SQUEEZER RECIPES
		RecipeManagers.squeezerManager.addRecipe(20, new ItemStack[] { ForestryItem.fruits.getItemStack(1, EnumFruit.CHERRY.ordinal()) }, LiquidHelper.getLiquid(Defaults.LIQUID_SEEDOIL, 5 * GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed")), ForestryItem.mulch.getItemStack(), 5);
		RecipeManagers.squeezerManager.addRecipe(60, new ItemStack[] { ForestryItem.fruits.getItemStack(1, EnumFruit.WALNUT.ordinal()) }, LiquidHelper.getLiquid(Defaults.LIQUID_SEEDOIL, 18 * GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed")), ForestryItem.mulch.getItemStack(), 5);
		RecipeManagers.squeezerManager.addRecipe(70, new ItemStack[] { ForestryItem.fruits.getItemStack(1, EnumFruit.CHESTNUT.ordinal()) }, LiquidHelper.getLiquid(Defaults.LIQUID_SEEDOIL, 22 * GameMode.getGameMode().getIntegerSetting("squeezer.liquid.seed")), ForestryItem.mulch.getItemStack(), 2);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { ForestryItem.fruits.getItemStack(1, EnumFruit.LEMON.ordinal()) }, LiquidHelper.getLiquid(Defaults.LIQUID_JUICE, GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") * 2), ForestryItem.mulch.getItemStack(), (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple") * 0.5f));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { ForestryItem.fruits.getItemStack(1, EnumFruit.PLUM.ordinal()) }, LiquidHelper.getLiquid(Defaults.LIQUID_JUICE, (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") * 0.5f)), ForestryItem.mulch.getItemStack(), GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple") * 3);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { ForestryItem.fruits.getItemStack(1, EnumFruit.PAPAYA.ordinal()) }, LiquidHelper.getLiquid(Defaults.LIQUID_JUICE, GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") * 3), ForestryItem.mulch.getItemStack(), (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple") * 0.5f));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { ForestryItem.fruits.getItemStack(1, EnumFruit.DATES.ordinal()) }, LiquidHelper.getLiquid(Defaults.LIQUID_JUICE, (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.liquid.apple") * 0.25)), ForestryItem.mulch.getItemStack(), (int) Math.floor(GameMode.getGameMode().getIntegerSetting("squeezer.mulch.apple")));

		RecipeUtil.injectLeveledRecipe(ForestryItem.sapling.getItemStack(), GameMode.getGameMode().getIntegerSetting("fermenter.yield.sapling"), Defaults.LIQUID_BIOMASS);

		// Stairs
		for (int i = 0; i < 16; i++) {
			WoodType type = WoodType.VALUES[i];
			NBTTagCompound compound = new NBTTagCompound();
			type.saveToCompound(compound);

			ItemStack stairs = new ItemStack(ForestryBlock.stairs, 4, 0);
			stairs.setTagCompound(compound);
			Proxies.common.addPriorityRecipe(stairs, new Object[] { "#  ", "## ", "###", '#',
					new ItemStack(ForestryBlock.planks1, 1, i) });
		}
		for (int i = 0; i < 8; i++) {
			WoodType type = WoodType.VALUES[16 + i];
			NBTTagCompound compound = new NBTTagCompound();
			type.saveToCompound(compound);

			ItemStack stairs = new ItemStack(ForestryBlock.stairs, 4, 0);
			stairs.setTagCompound(compound);
			Proxies.common.addPriorityRecipe(stairs, new Object[] { "#  ", "## ", "###", '#',
					new ItemStack(ForestryBlock.planks2, 1, i) });
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

		// Deciduous
		Allele.treeOak = new AlleleTreeSpecies("treeOak", false, "Apple Oak", quercus, "robur",
				proxy.getFoliageColorBasic(), WorldGenOak.class, new ItemStack(Blocks.log, 1, 0)).addFruitFamily(pomes)
				.setVanillaMap(0).setIsSecret();
		
		Allele.treeDarkOak = new AlleleTreeSpecies("treeDarkOak", false, "Dark Oak", quercus, "velutina",
				proxy.getFoliageColorBasic(), WorldGenDarkOak.class, new ItemStack(Blocks.log2, 1, 1)).addFruitFamily(pomes)
				.setVanillaMap(5);
		
		Allele.treeBirch = new AlleleTreeSpecies("treeBirch", false, "Silver Birch", betula,
				"pendula", proxy.getFoliageColorBirch(), 0xb0c648, WorldGenBirch.class, new ItemStack(Blocks.log, 1, 2))
		.setVanillaMap(2).setIsSecret();
		Allele.treeLime = new AlleleTreeSpecies("treeLime", true, "Silver Lime", tilia,
				"tomentosa", 0x5ea107, WorldGenLime.class, new ItemStack(ForestryBlock.log1, 1, 3)).addFruitFamily(nux)
				.addFruitFamily(prunes).addFruitFamily(pomes);

		// Nucifera
		Allele.treeWalnut = new AlleleTreeSpecies("treeWalnut", true, "Common Walnut", juglans,
				"regia", 0x798c55, 0xb0c648, WorldGenWalnut.class, new ItemStack(ForestryBlock.log4, 1, 1)).addFruitFamily(nux)
				.addFruitFamily(prunes).addFruitFamily(pomes).setGirth(2);
		Allele.treeChestnut = new AlleleTreeSpecies("treeChestnut", true, "Sweet Chestnut",
				castanea, "sativa", 0x5ea107, 0xb0c648, WorldGenChestnut.class, new ItemStack(ForestryBlock.log2, 1, 0)).addFruitFamily(nux)
				.addFruitFamily(prunes).addFruitFamily(pomes).setGirth(2);

		// Prunus & Citrus
		// <CovertJaguar> fructifer, annifer, bifer, aurifer = bearing fruit,
		// bearing fruit year round, bearing fruit twice a year, bearing golden
		// fruit
		Allele.treeCherry = new AlleleTreeSpecies("treeCherry", true, "Hill Cherry", prunus,
				"serrulata", 0xe691da, 0xe63e59, WorldGenCherry.class, new ItemStack(ForestryBlock.log4, 1, 3)).addFruitFamily(prunes)
				.addFruitFamily(pomes);
		Allele.treeLemon = new AlleleTreeSpecies("treeLemon", true, "Lemon", citrus, "limon",
				0x88af54, 0xa3b850, WorldGenLemon.class, new ItemStack(ForestryBlock.log6, 1, 3)).addFruitFamily(pomes).addFruitFamily(
						prunes);
		Allele.treePlum = new AlleleTreeSpecies("treePlum", true, "Plum", prunus, "domestica",
				0x589246, 0xa3b850, WorldGenPlum.class, new ItemStack(ForestryBlock.log6, 1, 1)).addFruitFamily(pomes)
				.addFruitFamily(prunes);

		// Maples
		Allele.treeMaple = new AlleleTreeSpecies("treeMaple", true, "Sugar Maple", acer,
				"saccharum", 0xd4f425, 0x619a3c, WorldGenMaple.class, new ItemStack(ForestryBlock.log6, 1, 2)).addFruitFamily(prunes)
				.addFruitFamily(pomes).setLeafIndices("maple");

		Allele.treeSpruce = new AlleleTreeSpecies("treeSpruce", false, "Red Spruce", picea,
				"abies", proxy.getFoliageColorPine(), 0x539d12, WorldGenSpruce.class, new ItemStack(Blocks.log, 1, 1))
		.setLeafIndices("conifers").setVanillaMap(1).setIsSecret();
		Allele.treeLarch = new AlleleTreeSpecies("treeLarch", true, "Mundane Larch", larix,
				"decidua", 0x698f90, 0x569896, WorldGenLarch.class, new ItemStack(ForestryBlock.log1, 1, 0)).setLeafIndices("conifers");
		Allele.treePine = new AlleleTreeSpecies("treePine", true, "Bull Pine", pinus, "sabiniana",
				0xfeff8f, 0xffd98f, WorldGenPine.class, new ItemStack(ForestryBlock.log6, 1, 0)).setLeafIndices("conifers");

		Allele.treeSequioa = new AlleleTreeSpecies("treeSequioa", false, "Coast Sequoia", sequoia,
				"sempervirens", 0x418e71, 0x569896, WorldGenSequoia.class, new ItemStack(ForestryBlock.log2, 1, 3)).setLeafIndices(
						"conifers").setGirth(3);
		Allele.treeGiganteum = new AlleleTreeSpecies("treeGigant", false, "Giant Sequoia",
				sequoiadendron, "giganteum", 0x738434, WorldGenGiganteum.class, new ItemStack(ForestryBlock.log2, 1, 3)).setLeafIndices(
						"conifers").setGirth(4);

		// Jungle
		Allele.treeJungle = new AlleleTreeSpecies("treeJungle", false, "Jungle", tropical,
				"tectona", proxy.getFoliageColorBasic(), 0x539d12, WorldGenJungle.class, new ItemStack(Blocks.log, 1, 3))
		.addFruitFamily(jungle).setLeafIndices("jungle").setVanillaMap(3).setIsSecret();
		Allele.treeTeak = new AlleleTreeSpecies("treeTeak", true, "Teak", tectona, "grandis",
				0xfeff8f, 0xffd98f, WorldGenTeak.class, new ItemStack(ForestryBlock.log1, 1, 3)).addFruitFamily(jungle).setLeafIndices(
						"jungle");
		Allele.treeKapok = new AlleleTreeSpecies("treeKapok", true, "Kapok", ceiba, "pentandra",
				0x89987b, 0x89aa9e, WorldGenKapok.class, new ItemStack(ForestryBlock.log3, 1, 0)).addFruitFamily(jungle)
				.addFruitFamily(prunes).setLeafIndices("jungle");

		// Ebony
		Allele.treeEbony = new AlleleTreeSpecies("treeEbony", true, "Myrtle Ebony", diospyros,
				"pentamera", 0xa2d24a, 0xc4d24a, WorldGenEbony.class, new ItemStack(ForestryBlock.log3, 1, 1)).addFruitFamily(jungle)
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
		Allele.treeMahogany = new AlleleTreeSpecies("treeMahogony", true, "Yellow Meranti", shorea,
				"gibbosa", 0x8ab154, 0xa9b154, WorldGenMahogany.class, new ItemStack(ForestryBlock.log3, 1, 2)).addFruitFamily(jungle)
				.setGirth(2).setLeafIndices("jungle");

		// 80+ meters tall:
		// Shorea argentifolia (also called Dark Red Meranti)
		// Shorea gibbosa (also called Yellow Meranti)
		// Shorea smithiana (also called Light Red Meranti)
		// Shorea superba

		// Malva
		
		Allele.treeAcacia = new AlleleTreeSpecies("treeAcaciaVanilla", true, "Acacia", acacia,
				"aneura", 0x616101, 0xb3b302, WorldGenAcaciaVanilla.class, new ItemStack(Blocks.log2, 1, 0)).addFruitFamily(jungle)
				.addFruitFamily(nux).setVanillaMap(4);
		
		Allele.treeDesertAcacia = new AlleleTreeSpecies("treeAcacia", true, "Desert Acacia", acacia,
				"erioloba", 0x748C1C, 0xb3b302, WorldGenAcacia.class, new ItemStack(ForestryBlock.log1, 1, 2)).addFruitFamily(jungle)
				.addFruitFamily(nux);
		Allele.treeBalsa = new AlleleTreeSpecies("treeBalsa", true, "Balsa", ochroma, "pyramidale",
				0x59ac00, 0xfeff8f, WorldGenBalsa.class, new ItemStack(ForestryBlock.log3, 1, 3)).addFruitFamily(jungle).addFruitFamily(nux);
		Allele.treeWenge = new AlleleTreeSpecies("treeWenge", true, "Wenge", millettia,
				"laurentii", 0xada157, 0xad8a57, WorldGenWenge.class, new ItemStack(ForestryBlock.log2, 1, 1)).addFruitFamily(jungle)
				.addFruitFamily(nux).setGirth(2);
		Allele.treeBaobab = new AlleleTreeSpecies("treeBaobab", true, "Grandidier's Baobab",
				adansonia, "digitata", 0xfeff8f, 0xffd98f, WorldGenBaobab.class, new ItemStack(ForestryBlock.log2, 1, 2))
		.addFruitFamily(jungle).addFruitFamily(nux).setGirth(3);
		Allele.treeMahoe = new AlleleTreeSpecies("treeMahoe", true, "Blue Mahoe", talipariti,
				"elatum", 0xa0ba1b, 0x79a175, WorldGenMahoe.class, new ItemStack(ForestryBlock.log5, 1, 0)).addFruitFamily(jungle)
				.addFruitFamily(pomes).addFruitFamily(prunes);

		// Willows
		Allele.treeWillow = new AlleleTreeSpecies("treeWillow", true, "White Willow", salix,
				"alba", 0xa3b8a5, 0xa3b850, WorldGenWillow.class, new ItemStack(ForestryBlock.log4, 1, 0)).addFruitFamily(nux)
				.addFruitFamily(prunes).addFruitFamily(pomes).setLeafIndices("willow");

		// Lauraceae
		Allele.treeSipiri = new AlleleTreeSpecies("treeSipiri", true, "Sipiri", chlorocardium,
				"rodiei", 0x678911, 0x79a175, WorldGenGreenheart.class, new ItemStack(ForestryBlock.log3, 1, 2)).addFruitFamily(jungle);

		// Unclassified
		Allele.treePapaya = new AlleleTreeSpecies("treePapaya", true, "Papaya", carica, "papaya",
				0x6d9f58, 0x9ee67f, WorldGenPapaya.class, new ItemStack(ForestryBlock.log5, 1, 3)).addFruitFamily(jungle)
				.addFruitFamily(nux).setLeafIndices("palm");
		Allele.treeDate = new AlleleTreeSpecies("treeDate", true, "Date Palm", null, "dactylifera",
				0xcbcd79, 0xf0f38f, WorldGenDate.class, new ItemStack(ForestryBlock.log4, 1, 2)).addFruitFamily(jungle).addFruitFamily(nux)
				.setLeafIndices("palm");

		Allele.treePoplar = new AlleleTreeSpecies("treePoplar", true, "White Poplar", populus,
				"alba", 0xa3b8a5, 0x539d12, WorldGenPoplar.class, new ItemStack(ForestryBlock.log5, 1, 1)).addFruitFamily(pomes)
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
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}

	@Override
	public ICommand[] getConsoleCommands() {
		return new ICommand[] { new CommandSpawnTree(), new CommandSpawnForest(),
				new CommandTreekeepingMode() };
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		if (ForestryItem.sapling.isItemEqual(fuel))
			return 100;

		return 0;
	}

	@Override
	public IPacketHandler getPacketHandler() {
		return new PacketHandlerArboriculture();
	}

	@Override
	public boolean processIMCMessage(IMCMessage message) {
		if (message.key.equals("add-fence-block") && message.isStringMessage()) {
			Block block = GameData.getBlockRegistry().getRaw(message.getStringValue());

			if (block == null || block == Blocks.air) {
				Proxies.log.warning("invalid add-fence-block IMC: can't resolve block name %s.", message.getStringValue());
			} else {
				validFences.add(block);
			}
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

		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getOakTemplate(), EnumGermlingType.POLLEN), 2, 3, 4));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getSpruceTemplate(), EnumGermlingType.POLLEN), 2, 3, 4));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getBirchTemplate(), EnumGermlingType.POLLEN), 2, 3, 4));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getLarchTemplate(), EnumGermlingType.POLLEN), 1, 2, 3));
		ChestGenHooks.addItem(Defaults.CHEST_GEN_HOOK_NATURALIST_CHEST, new WeightedRandomChestContent(this.getTreeItemFromTemplate(TreeTemplates.getLimeTemplate(), EnumGermlingType.POLLEN), 1, 2, 3));
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
