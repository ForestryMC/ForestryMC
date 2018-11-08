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
package forestry.farming;


import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.Forestry;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.TreeManager;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmRegistry;
import forestry.api.modules.ForestryModule;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.ModuleCore;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.blocks.BlockRegistryCore;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.OreDictUtil;
import forestry.farming.blocks.BlockMushroom;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.logic.FarmLogicArboreal;
import forestry.farming.logic.FarmLogicCocoa;
import forestry.farming.logic.FarmLogicCrops;
import forestry.farming.logic.FarmLogicEnder;
import forestry.farming.logic.FarmLogicGourd;
import forestry.farming.logic.FarmLogicInfernal;
import forestry.farming.logic.FarmLogicMushroom;
import forestry.farming.logic.FarmLogicOrchard;
import forestry.farming.logic.FarmLogicPeat;
import forestry.farming.logic.FarmLogicReeds;
import forestry.farming.logic.FarmLogicSucculent;
import forestry.farming.logic.ForestryFarmIdentifier;
import forestry.farming.logic.farmables.FarmableAgingCrop;
import forestry.farming.logic.farmables.FarmableChorus;
import forestry.farming.logic.farmables.FarmableGE;
import forestry.farming.logic.farmables.FarmableGourd;
import forestry.farming.logic.farmables.FarmableStacked;
import forestry.farming.logic.farmables.FarmableVanillaMushroom;
import forestry.farming.logic.farmables.FarmableVanillaSapling;
import forestry.farming.models.EnumFarmBlockTexture;
import forestry.farming.proxy.ProxyFarming;
import forestry.farming.tiles.TileFarmControl;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmHatch;
import forestry.farming.tiles.TileFarmPlain;
import forestry.farming.tiles.TileFarmValve;
import forestry.farming.triggers.FarmingTriggers;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FARMING, name = "Farming", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.farming.description")
public class ModuleFarming extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.farming.proxy.ProxyFarmingClient", serverSide = "forestry.farming.proxy.ProxyFarming")
	public static ProxyFarming proxy;

	@Nullable
	private static BlockRegistryFarming blocks;

	public static BlockRegistryFarming getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	@Override
	public void setupAPI() {
		ForestryAPI.farmRegistry = FarmRegistry.getInstance();
	}

	@Override
	public void disabledSetupAPI() {
		ForestryAPI.farmRegistry = new DummyFarmRegistry();
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryFarming();
	}

	@Override
	public void preInit() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		BlockRegistryFarming blocks = getBlocks();

		MinecraftForge.EVENT_BUS.register(this);
		IFarmRegistry registry = ForestryAPI.farmRegistry;

		registry.registerFarmables(ForestryFarmIdentifier.ARBOREAL, new FarmableVanillaSapling());
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			registry.registerFarmables(ForestryFarmIdentifier.ARBOREAL, new FarmableGE());
		}

		registry.registerFarmables(ForestryFarmIdentifier.CROPS,
			new FarmableAgingCrop(new ItemStack(Items.WHEAT_SEEDS), Blocks.WHEAT, new ItemStack(Items.WHEAT), BlockCrops.AGE, 7, 0),
			new FarmableAgingCrop(new ItemStack(Items.POTATO), Blocks.POTATOES, new ItemStack(Items.POTATO), BlockCrops.AGE, 7, 0),
			new FarmableAgingCrop(new ItemStack(Items.CARROT), Blocks.CARROTS, new ItemStack(Items.CARROT), BlockCrops.AGE, 7, 0),
			new FarmableAgingCrop(new ItemStack(Items.BEETROOT_SEEDS), Blocks.BEETROOTS, new ItemStack(Items.BEETROOT), BlockBeetroot.BEETROOT_AGE, 3, 0));

		IBlockState plantedBrownMushroom = blocks.mushroom.getDefaultState().withProperty(BlockMushroom.VARIANT, BlockMushroom.MushroomType.BROWN);
		registry.registerFarmables(ForestryFarmIdentifier.SHROOM, new FarmableVanillaMushroom(new ItemStack(Blocks.BROWN_MUSHROOM), plantedBrownMushroom, Blocks.BROWN_MUSHROOM_BLOCK));

		IBlockState plantedRedMushroom = blocks.mushroom.getDefaultState().withProperty(BlockMushroom.VARIANT, BlockMushroom.MushroomType.RED);
		registry.registerFarmables(ForestryFarmIdentifier.SHROOM, new FarmableVanillaMushroom(new ItemStack(Blocks.RED_MUSHROOM), plantedRedMushroom, Blocks.RED_MUSHROOM_BLOCK));

		registry.registerFarmables(ForestryFarmIdentifier.GOURD, new FarmableGourd(new ItemStack(Items.PUMPKIN_SEEDS), Blocks.PUMPKIN_STEM, Blocks.PUMPKIN));
		registry.registerFarmables(ForestryFarmIdentifier.GOURD, new FarmableGourd(new ItemStack(Items.MELON_SEEDS), Blocks.MELON_STEM, Blocks.MELON_BLOCK));

		registry.registerFarmables(ForestryFarmIdentifier.INFERNAL, new FarmableAgingCrop(new ItemStack(Items.NETHER_WART), Blocks.NETHER_WART, BlockNetherWart.AGE, 3));

		registry.registerFarmables(ForestryFarmIdentifier.POALES, new FarmableStacked(new ItemStack(Items.REEDS), Blocks.REEDS, 3));

		registry.registerFarmables(ForestryFarmIdentifier.SUCCULENTES, new FarmableStacked(new ItemStack(Blocks.CACTUS), Blocks.CACTUS, 3));

		registry.registerFarmables(ForestryFarmIdentifier.ENDER, FarmableChorus.INSTANCE);

		//Forestry fertilizer
		registry.registerFertilizer(new ItemStack(coreItems.fertilizerCompound, 1, OreDictionary.WILDCARD_VALUE), 500);

		proxy.initializeModels();

		// Layouts
		ICircuitLayout layoutManaged = new CircuitLayout("farms.managed", CircuitSocketType.FARM);
		ChipsetManager.circuitRegistry.registerLayout(layoutManaged);
		ICircuitLayout layoutManual = new CircuitLayout("farms.manual", CircuitSocketType.FARM);
		ChipsetManager.circuitRegistry.registerLayout(layoutManual);
	}

	@Override
	public void registerTriggers() {
		FarmingTriggers.initialize();
	}

	@Override
	public void doInit() {

		//Load config
		File configFile = new File(Forestry.instance.getConfigFolder(), Config.CATEGORY_FARM + ".cfg");
		LocalizedConfiguration config = new LocalizedConfiguration(configFile, "1.0.0");
		FarmRegistry.getInstance().loadConfig(config);
		config.save();

		TileUtil.registerTile(TileFarmPlain.class, "farm");
		TileUtil.registerTile(TileFarmGearbox.class, "farm_gearbox");
		TileUtil.registerTile(TileFarmHatch.class, "farm_hatch");
		TileUtil.registerTile(TileFarmValve.class, "farm_valve");
		TileUtil.registerTile(TileFarmControl.class, "farm_control");

		IFarmRegistry registry = FarmRegistry.getInstance();
		BlockRegistryCore coreBlocks = ModuleCore.getBlocks();
		ItemRegistryCore coreItems = ModuleCore.getItems();

		IFarmProperties arborealFarm = registry.registerLogic(ForestryFarmIdentifier.ARBOREAL, FarmLogicArboreal::new);
		IFarmProperties cropsFarm = registry.registerLogic(ForestryFarmIdentifier.CROPS, FarmLogicCrops::new);
		IFarmProperties mushroomFarm = registry.registerLogic(ForestryFarmIdentifier.SHROOM, FarmLogicMushroom::new);
		IFarmProperties succulentFarm = registry.registerLogic(ForestryFarmIdentifier.SUCCULENTES, FarmLogicSucculent::new);
		IFarmProperties peatFarm = registry.registerLogic(ForestryFarmIdentifier.PEAT, FarmLogicPeat::new);
		IFarmProperties infernalFarm = registry.registerLogic(ForestryFarmIdentifier.INFERNAL, FarmLogicInfernal::new);
		IFarmProperties poalesFarm = registry.registerLogic(ForestryFarmIdentifier.POALES, FarmLogicReeds::new);
		IFarmProperties orchardFarm = registry.registerLogic(ForestryFarmIdentifier.ORCHARD, FarmLogicOrchard::new);
		IFarmProperties gourdFarm = registry.registerLogic(ForestryFarmIdentifier.GOURD, FarmLogicGourd::new);
		IFarmProperties cocoaFarm = registry.registerLogic(ForestryFarmIdentifier.COCOA, FarmLogicCocoa::new);
		IFarmProperties enderFarm = registry.registerLogic(ForestryFarmIdentifier.ENDER, FarmLogicEnder::new);

		Circuits.farmArborealManaged = new CircuitFarmLogic("managedArboreal", arborealFarm, false);
		Circuits.farmArborealManual = new CircuitFarmLogic("manualArboreal", arborealFarm, true);
		arborealFarm.registerSoil(new ItemStack(Blocks.DIRT), coreBlocks.humus.getDefaultState());
		arborealFarm.registerSoil(new ItemStack(coreBlocks.humus), coreBlocks.humus.getDefaultState());
		arborealFarm.addProducts(new ItemStack(Blocks.SAND));

		Circuits.farmShroomManaged = new CircuitFarmLogic("managedShroom", mushroomFarm, false);
		Circuits.farmShroomManual = new CircuitFarmLogic("manualShroom", mushroomFarm, true);
		mushroomFarm.registerSoil(new ItemStack(Blocks.MYCELIUM), Blocks.MYCELIUM.getDefaultState());
		mushroomFarm.registerSoil(new ItemStack(Blocks.DIRT, 1, 2),
			Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL), true);

		Circuits.farmPeatManaged = new CircuitFarmLogic("managedPeat", peatFarm, false);
		Circuits.farmPeatManual = new CircuitFarmLogic("manualPeat", peatFarm, true);
		peatFarm.registerSoil(coreBlocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, 1), coreBlocks.bogEarth.getDefaultState());
		peatFarm.addProducts(new ItemStack(coreItems.peat), new ItemStack(Blocks.DIRT));

		Circuits.farmCropsManaged = new CircuitFarmLogic("managedCrops", cropsFarm, false);
		Circuits.farmCropsManual = new CircuitFarmLogic("manualCrops", cropsFarm, true);
		cropsFarm.registerSoil(new ItemStack(Blocks.DIRT), Blocks.FARMLAND.getDefaultState());
		cocoaFarm.registerFarmables("farmWheat");

		Circuits.farmInfernalManaged = new CircuitFarmLogic("managedInfernal", infernalFarm, false);
		Circuits.farmInfernalManual = new CircuitFarmLogic("manualInfernal", infernalFarm, true);
		infernalFarm.registerSoil(new ItemStack(Blocks.SOUL_SAND), Blocks.SOUL_SAND.getDefaultState());

		Circuits.farmOrchardManaged = new CircuitFarmLogic("managedOrchard", orchardFarm, false);
		Circuits.farmOrchardManual = new CircuitFarmLogic("manualOrchard", orchardFarm, true);
		/*for(IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles(EnumTreeChromosome.FRUITS)){
			if(allele instanceof IAlleleFruit){
				IAlleleFruit alleleFruit = (IAlleleFruit) allele;
				IFruitProvider fruitProvider = alleleFruit.getProvider();
				orchardFarm.addProducts(fruitProvider.getProducts().keySet());
				orchardFarm.addProducts(fruitProvider.getSpecialty().keySet());
			}
		}*/

		Circuits.farmSucculentManaged = new CircuitFarmLogic("managedSucculent", succulentFarm, false);
		Circuits.farmSucculentManual = new CircuitFarmLogic("manualSucculent", succulentFarm, true);
		succulentFarm.registerSoil(new ItemStack(Blocks.SAND), Blocks.SAND.getDefaultState(), true);

		Circuits.farmPoalesManaged = new CircuitFarmLogic("managedPoales", poalesFarm, false);
		Circuits.farmPoalesManual = new CircuitFarmLogic("manualPoales", poalesFarm, true);
		poalesFarm.registerSoil(new ItemStack(Blocks.SAND), Blocks.SAND.getDefaultState(), true);
		poalesFarm.registerSoil(new ItemStack(Blocks.DIRT), Blocks.DIRT.getDefaultState(), false);

		Circuits.farmGourdManaged = new CircuitFarmLogic("managedGourd", gourdFarm, false);
		Circuits.farmGourdManual = new CircuitFarmLogic("manualGourd", gourdFarm, true);

		Circuits.farmCocoaManaged = new CircuitFarmLogic("managedCocoa", cocoaFarm, false);
		Circuits.farmCocoaManual = new CircuitFarmLogic("manualCocoa", cocoaFarm, true);
		cocoaFarm.registerSoil(new ItemStack(Blocks.LOG, 1, 3),
			Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE));
		cocoaFarm.addGermlings(new ItemStack(Items.DYE, 1, 3));
		cocoaFarm.addProducts(new ItemStack(Items.DYE, 1, 3));

		Circuits.farmEnderManaged = new CircuitFarmLogic("managedEnder", enderFarm, false);
		Circuits.farmEnderManual = new CircuitFarmLogic("manualEnder", enderFarm, true);
		enderFarm.registerSoil(new ItemStack(Blocks.END_STONE), Blocks.END_STONE.getDefaultState());
	}

	@Override
	public void registerRecipes() {
		ItemRegistryCore coreItems = ModuleCore.getItems();
		BlockRegistryFarming blocks = getBlocks();

		ItemStack basic = blocks.farm.get(EnumFarmBlockType.PLAIN, 1);
		ItemStack gearbox = blocks.farm.get(EnumFarmBlockType.GEARBOX, 1);
		ItemStack hatch = blocks.farm.get(EnumFarmBlockType.HATCH, 1);
		ItemStack valve = blocks.farm.get(EnumFarmBlockType.VALVE, 1);
		ItemStack control = blocks.farm.get(EnumFarmBlockType.CONTROL, 1);

		for (EnumFarmBlockTexture block : EnumFarmBlockTexture.values()) {
			NBTTagCompound compound = new NBTTagCompound();
			block.saveToCompound(compound);

			basic.setTagCompound(compound);
			gearbox.setTagCompound(compound);
			hatch.setTagCompound(compound);
			valve.setTagCompound(compound);
			control.setTagCompound(compound);

			RecipeUtil.addRecipe("farm_basic_" + block.getUid(), basic,
				"I#I",
				"WCW",
				'#', block.getBase(),
				'W', "slabWood",
				'C', coreItems.tubes.get(EnumElectronTube.TIN, 1),
				'I', "ingotCopper");

			RecipeUtil.addRecipe("farm_gearbox_" + block.getUid(), gearbox,
				" # ",
				"TTT",
				'#', basic,
				'T', "gearTin");

			RecipeUtil.addRecipe("farm_hatch_" + block.getUid(), hatch,
				" # ",
				"TDT",
				'#', basic,
				'T', "gearTin",
				'D', OreDictUtil.TRAPDOOR_WOOD);

			RecipeUtil.addRecipe("farm_valve_" + block.getUid(), valve,
				" # ",
				"XTX",
				'#', basic,
				'T', "gearTin",
				'X', "blockGlass");

			RecipeUtil.addRecipe("farm_control_" + block.getUid(), control,
				" # ",
				"XTX",
				'#', basic,
				'T', coreItems.tubes.get(EnumElectronTube.GOLD, 1),
				'X', "dustRedstone");
		}

		// Circuits
		ICircuitLayout layoutManaged = ChipsetManager.circuitRegistry.getLayout("forestry.farms.managed");
		ICircuitLayout layoutManual = ChipsetManager.circuitRegistry.getLayout("forestry.farms.manual");

		ChipsetManager.circuitRegistry.registerDeprecatedCircuitReplacement("managedCereal", Circuits.farmCropsManaged);
		ChipsetManager.circuitRegistry.registerDeprecatedCircuitReplacement("manualCereal", Circuits.farmCropsManual);
		ChipsetManager.circuitRegistry.registerDeprecatedCircuitReplacement("managedVegetable", Circuits.farmCropsManaged);
		ChipsetManager.circuitRegistry.registerDeprecatedCircuitReplacement("manualVegetable", Circuits.farmCropsManual);

		if (layoutManaged == null || layoutManual == null) {
			return;
		}

		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.GOLD, 1), Circuits.farmArborealManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.COPPER, 1), Circuits.farmSucculentManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.OBSIDIAN, 1), Circuits.farmPeatManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.BRONZE, 1), Circuits.farmCropsManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.BLAZE, 1), Circuits.farmInfernalManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.TIN, 1), Circuits.farmPoalesManual);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.LAPIS, 1), Circuits.farmGourdManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.APATITE, 1), Circuits.farmShroomManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.DIAMOND, 1), Circuits.farmCocoaManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.EMERALD, 1), Circuits.farmOrchardManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, coreItems.tubes.get(EnumElectronTube.ENDER, 1), Circuits.farmEnderManaged);

		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.GOLD, 1), Circuits.farmArborealManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.COPPER, 1), Circuits.farmSucculentManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.OBSIDIAN, 1), Circuits.farmPeatManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.BRONZE, 1), Circuits.farmCropsManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.TIN, 1), Circuits.farmPoalesManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.LAPIS, 1), Circuits.farmGourdManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.APATITE, 1), Circuits.farmShroomManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.DIAMOND, 1), Circuits.farmCocoaManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.EMERALD, 1), Circuits.farmOrchardManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, coreItems.tubes.get(EnumElectronTube.ENDER, 1), Circuits.farmEnderManual);
	}

	@Override
	public void postInit() {
		IFarmProperties orchardFarm = FarmRegistry.getInstance().getProperties(ForestryFarmIdentifier.ORCHARD);
		if (orchardFarm != null && ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			ITreeRoot treeRoot = TreeManager.treeRoot;
			if (treeRoot != null) {
				for (ITree tree : treeRoot.getIndividualTemplates()) {
					IFruitProvider fruitProvider = tree.getGenome().getFruitProvider();
					if (fruitProvider != AlleleFruits.fruitNone.getProvider()) {
						orchardFarm.addGermlings(treeRoot.getMemberStack(tree, EnumGermlingType.SAPLING));
						orchardFarm.addProducts(fruitProvider.getProducts().keySet());
						orchardFarm.addProducts(fruitProvider.getSpecialty().keySet());
					}
				}
			}
		}
	}

	@Override
	public void getHiddenItems(List<ItemStack> hiddenItems) {
		// mushrooms are a workaround for the farm and should not be obtainable
		hiddenItems.add(new ItemStack(getBlocks().mushroom, 1, OreDictionary.WILDCARD_VALUE));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		EnumFarmBlockType.registerSprites();
	}
}
