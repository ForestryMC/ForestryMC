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

import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.fml.DistExecutor;

import forestry.Forestry;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeRoot;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmRegistry;
import forestry.api.modules.ForestryModule;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.core.items.EnumElectronTube;
import forestry.farming.blocks.BlockMushroom;
import forestry.farming.blocks.BlockRegistryFarming;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.circuits.CircuitFarmLogic;
import forestry.farming.gui.FarmingContainerTypes;
import forestry.farming.gui.GuiFarm;
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
import forestry.farming.proxy.ProxyFarming;
import forestry.farming.proxy.ProxyFarmingClient;
import forestry.farming.tiles.TileRegistryFarming;
import forestry.farming.triggers.FarmingTriggers;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

//import forestry.arboriculture.genetics.alleles.AlleleFruits;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FARMING, name = "Farming", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.farming.description")
public class ModuleFarming extends BlankForestryModule {

	@SuppressWarnings("NullableProblems")
	public static ProxyFarming proxy;

	@Nullable
	private static BlockRegistryFarming blocks;
	@Nullable
	private static TileRegistryFarming tiles;
	@Nullable
	private static FarmingContainerTypes containerTypes;

	public ModuleFarming() {
		proxy = DistExecutor.runForDist(() -> () -> new ProxyFarmingClient(), () -> () -> new ProxyFarming());
		MinecraftForge.EVENT_BUS.register(this);
	}


	public static TileRegistryFarming getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	public static BlockRegistryFarming getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static FarmingContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
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
	public void registerBlocks() {
		blocks = new BlockRegistryFarming();
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryFarming();
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new FarmingContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		FarmingContainerTypes containerTypes = getContainerTypes();
		ScreenManager.registerFactory(containerTypes.FARM, GuiFarm::new);
	}

	@Override
	public void preInit() {
		BlockRegistryFarming blocks = getBlocks();

		MinecraftForge.EVENT_BUS.register(this);
		IFarmRegistry registry = ForestryAPI.farmRegistry;
		registry.registerFarmables(ForestryFarmIdentifier.ARBOREAL, new FarmableVanillaSapling());
		if (ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			registry.registerFarmables(ForestryFarmIdentifier.ARBOREAL, new FarmableGE());
		}

		registry.registerFarmables(ForestryFarmIdentifier.CROPS,
			new FarmableAgingCrop(new ItemStack(Items.WHEAT_SEEDS), Blocks.WHEAT, new ItemStack(Items.WHEAT), CropsBlock.AGE, 7, 0),
			new FarmableAgingCrop(new ItemStack(Items.POTATO), Blocks.POTATOES, new ItemStack(Items.POTATO), CropsBlock.AGE, 7, 0),
			new FarmableAgingCrop(new ItemStack(Items.CARROT), Blocks.CARROTS, new ItemStack(Items.CARROT), CropsBlock.AGE, 7, 0),
			new FarmableAgingCrop(new ItemStack(Items.BEETROOT_SEEDS), Blocks.BEETROOTS, new ItemStack(Items.BEETROOT), BeetrootBlock.BEETROOT_AGE, 3, 0));

		BlockState plantedBrownMushroom = blocks.mushroom.getDefaultState().with(BlockMushroom.VARIANT, BlockMushroom.MushroomType.BROWN);
		registry.registerFarmables(ForestryFarmIdentifier.SHROOM, new FarmableVanillaMushroom(new ItemStack(Blocks.BROWN_MUSHROOM), plantedBrownMushroom, Blocks.BROWN_MUSHROOM_BLOCK));

		BlockState plantedRedMushroom = blocks.mushroom.getDefaultState().with(BlockMushroom.VARIANT, BlockMushroom.MushroomType.RED);
		registry.registerFarmables(ForestryFarmIdentifier.SHROOM, new FarmableVanillaMushroom(new ItemStack(Blocks.RED_MUSHROOM), plantedRedMushroom, Blocks.RED_MUSHROOM_BLOCK));

		registry.registerFarmables(ForestryFarmIdentifier.GOURD, new FarmableGourd(new ItemStack(Items.PUMPKIN_SEEDS), Blocks.PUMPKIN_STEM, Blocks.PUMPKIN));
		registry.registerFarmables(ForestryFarmIdentifier.GOURD, new FarmableGourd(new ItemStack(Items.MELON_SEEDS), Blocks.MELON_STEM, Blocks.MELON));

		registry.registerFarmables(ForestryFarmIdentifier.INFERNAL, new FarmableAgingCrop(new ItemStack(Items.NETHER_WART), Blocks.NETHER_WART, NetherWartBlock.AGE, 3));

		registry.registerFarmables(ForestryFarmIdentifier.POALES, new FarmableStacked(new ItemStack(Items.SUGAR_CANE), Blocks.SUGAR_CANE, 3));

		registry.registerFarmables(ForestryFarmIdentifier.SUCCULENTES, new FarmableStacked(new ItemStack(Blocks.CACTUS), Blocks.CACTUS, 3));

		registry.registerFarmables(ForestryFarmIdentifier.ENDER, FarmableChorus.INSTANCE);

		//Forestry fertilizer
		//TODO - tags
		registry.registerFertilizer(CoreItems.FERTILIZER_COMPOUND.stack(), 500);

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

		IFarmRegistry registry = FarmRegistry.getInstance();

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
		arborealFarm.registerSoil(new ItemStack(Blocks.DIRT), CoreBlocks.HUMUS.defaultState());
		arborealFarm.registerSoil(CoreBlocks.HUMUS.stack(), CoreBlocks.HUMUS.defaultState());
		arborealFarm.addProducts(new ItemStack(Blocks.SAND));

		Circuits.farmShroomManaged = new CircuitFarmLogic("managedShroom", mushroomFarm, false);
		Circuits.farmShroomManual = new CircuitFarmLogic("manualShroom", mushroomFarm, true);
		mushroomFarm.registerSoil(new ItemStack(Blocks.MYCELIUM), Blocks.MYCELIUM.getDefaultState());
		mushroomFarm.registerSoil(new ItemStack(Blocks.PODZOL), Blocks.PODZOL.getDefaultState(), true);

		Circuits.farmPeatManaged = new CircuitFarmLogic("managedPeat", peatFarm, false);
		Circuits.farmPeatManual = new CircuitFarmLogic("manualPeat", peatFarm, true);
		peatFarm.registerSoil(CoreBlocks.BOG_EARTH.stack(), CoreBlocks.BOG_EARTH.defaultState());
		peatFarm.addProducts(CoreItems.PEAT.stack(), new ItemStack(Blocks.DIRT));

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
		cocoaFarm.registerSoil(new ItemStack(Blocks.JUNGLE_LOG), Blocks.JUNGLE_LOG.getDefaultState());
		cocoaFarm.addGermlings(new ItemStack(Items.COCOA_BEANS));
		cocoaFarm.addProducts(new ItemStack(Items.COCOA_BEANS));

		Circuits.farmEnderManaged = new CircuitFarmLogic("managedEnder", enderFarm, false);
		Circuits.farmEnderManual = new CircuitFarmLogic("manualEnder", enderFarm, true);
		enderFarm.registerSoil(new ItemStack(Blocks.END_STONE), Blocks.END_STONE.getDefaultState());
	}

	@Override
	public void registerRecipes() {
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

		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.GOLD, 1), Circuits.farmArborealManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.COPPER, 1), Circuits.farmSucculentManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.OBSIDIAN, 1), Circuits.farmPeatManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.BRONZE, 1), Circuits.farmCropsManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.BLAZE, 1), Circuits.farmInfernalManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.TIN, 1), Circuits.farmPoalesManual);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.LAPIS, 1), Circuits.farmGourdManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.APATITE, 1), Circuits.farmShroomManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.DIAMOND, 1), Circuits.farmCocoaManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.EMERALD, 1), Circuits.farmOrchardManaged);
		ChipsetManager.solderManager.addRecipe(layoutManaged, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.ENDER, 1), Circuits.farmEnderManaged);

		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.GOLD, 1), Circuits.farmArborealManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.COPPER, 1), Circuits.farmSucculentManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.OBSIDIAN, 1), Circuits.farmPeatManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.BRONZE, 1), Circuits.farmCropsManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.TIN, 1), Circuits.farmPoalesManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.LAPIS, 1), Circuits.farmGourdManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.APATITE, 1), Circuits.farmShroomManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.DIAMOND, 1), Circuits.farmCocoaManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.EMERALD, 1), Circuits.farmOrchardManual);
		ChipsetManager.solderManager.addRecipe(layoutManual, CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.ENDER, 1), Circuits.farmEnderManual);
	}

	@Override
	public void postInit() {
		IFarmProperties orchardFarm = FarmRegistry.getInstance().getProperties(ForestryFarmIdentifier.ORCHARD);
		if (orchardFarm != null && ModuleHelper.isEnabled(ForestryModuleUids.ARBORICULTURE)) {
			ITreeRoot treeRoot = TreeManager.treeRoot;
			if (treeRoot != null) {
				for (ITree tree : treeRoot.getIndividualTemplates()) {
					IFruitProvider fruitProvider = tree.getGenome().getActiveAllele(TreeChromosomes.FRUITS).getProvider();
					if (fruitProvider != AlleleFruits.fruitNone.getProvider()) {
						orchardFarm.addGermlings(treeRoot.getTypes().createStack(tree, EnumGermlingType.SAPLING));
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
		//		hiddenItems.add(new ItemStack(getBlocks().mushroom, 1, OreDictionary.WILDCARD_VALUE));
		//TODO - tag
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		EnumFarmBlockType.registerSprites();
	}
}
