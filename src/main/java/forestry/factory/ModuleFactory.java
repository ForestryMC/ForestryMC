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
package forestry.factory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.EngineCopperFuel;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.fuels.RainSubstrate;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.EnumHoneyDrop;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.ModuleCore;
import forestry.core.ModuleFluids;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.blocks.BlockRegistryCore;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.EnumCraftingMaterial;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.items.ItemRegistryFluids;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.datastructures.DummyMap;
import forestry.core.utils.datastructures.FluidMap;
import forestry.core.utils.datastructures.ItemStackMap;
import forestry.factory.blocks.BlockRegistryFactory;
import forestry.factory.circuits.CircuitSpeedUpgrade;
import forestry.factory.gui.FactoryContainerTypes;
import forestry.factory.gui.GuiBottler;
import forestry.factory.gui.GuiCarpenter;
import forestry.factory.gui.GuiCentrifuge;
import forestry.factory.gui.GuiFabricator;
import forestry.factory.gui.GuiFermenter;
import forestry.factory.gui.GuiMoistener;
import forestry.factory.gui.GuiRaintank;
import forestry.factory.gui.GuiSqueezer;
import forestry.factory.gui.GuiStill;
import forestry.factory.network.PacketRegistryFactory;
import forestry.factory.recipes.CarpenterRecipeManager;
import forestry.factory.recipes.CentrifugeRecipeManager;
import forestry.factory.recipes.FabricatorRecipeManager;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;
import forestry.factory.recipes.FermenterRecipeManager;
import forestry.factory.recipes.MoistenerRecipeManager;
import forestry.factory.recipes.SqueezerRecipeManager;
import forestry.factory.recipes.StillRecipeManager;
import forestry.factory.tiles.TileRegistryFactory;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.storage.ModuleCrates;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FACTORY, name = "Factory", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.factory.description", lootTable = "factory")
public class ModuleFactory extends BlankForestryModule {

	public static final Map<String, Boolean> MACHINE_ENABLED = Maps.newHashMap();

	@Nullable
	private static BlockRegistryFactory blocks;
	@Nullable
	private static TileRegistryFactory tiles;
	@Nullable
	private static FactoryContainerTypes containerTypes;

	public static BlockRegistryFactory getBlocks() {
		Preconditions.checkNotNull(blocks);
		return blocks;
	}

	public static TileRegistryFactory getTiles() {
		Preconditions.checkNotNull(tiles);
		return tiles;
	}

	public static FactoryContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containerTypes);
		return containerTypes;
	}

	public ModuleFactory() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@Override
	public void setupAPI() {
		RecipeManagers.carpenterManager = machineEnabled(MachineUIDs.CARPENTER) ? new CarpenterRecipeManager() : new DummyManagers.DummyCarpenterManager();
		RecipeManagers.centrifugeManager = machineEnabled(MachineUIDs.CENTRIFUGE) ? new CentrifugeRecipeManager() : new DummyManagers.DummyCentrifugeManager();
		RecipeManagers.fabricatorManager = machineEnabled(MachineUIDs.FABRICATOR) ? new FabricatorRecipeManager() : new DummyManagers.DummyFabricatorManager();
		RecipeManagers.fabricatorSmeltingManager = machineEnabled(MachineUIDs.FABRICATOR) ? new FabricatorSmeltingRecipeManager() : new DummyManagers.DummyFabricatorSmeltingManager();
		RecipeManagers.fermenterManager = machineEnabled(MachineUIDs.FERMENTER) ? new FermenterRecipeManager() : new DummyManagers.DummyFermenterManager();
		RecipeManagers.moistenerManager = machineEnabled(MachineUIDs.MOISTENER) ? new MoistenerRecipeManager() : new DummyManagers.DummyMoistenerManager();
		RecipeManagers.squeezerManager = machineEnabled(MachineUIDs.SQUEEZER) ? new SqueezerRecipeManager() : new DummyManagers.DummySqueezerManager();
		RecipeManagers.stillManager = machineEnabled(MachineUIDs.STILL) ? new StillRecipeManager() : new DummyManagers.DummyStillManager();

		setupFuelManager();
	}

	@Override
	public void disabledSetupAPI() {
		RecipeManagers.carpenterManager = new DummyManagers.DummyCarpenterManager();
		RecipeManagers.centrifugeManager = new DummyManagers.DummyCentrifugeManager();
		RecipeManagers.fabricatorManager = new DummyManagers.DummyFabricatorManager();
		RecipeManagers.fabricatorSmeltingManager = new DummyManagers.DummyFabricatorSmeltingManager();
		RecipeManagers.fermenterManager = new DummyManagers.DummyFermenterManager();
		RecipeManagers.moistenerManager = new DummyManagers.DummyMoistenerManager();
		RecipeManagers.squeezerManager = new DummyManagers.DummySqueezerManager();
		RecipeManagers.stillManager = new DummyManagers.DummyStillManager();

		setupFuelManager();
	}

	private static void setupFuelManager() {
		FuelManager.fermenterFuel = machineEnabled(MachineUIDs.FERMENTER) ? new ItemStackMap<>() : new DummyMap<>();
		FuelManager.moistenerResource = machineEnabled(MachineUIDs.MOISTENER) ? new ItemStackMap<>() : new DummyMap<>();
		FuelManager.rainSubstrate = machineEnabled(MachineUIDs.RAINMAKER) ? new ItemStackMap<>() : new DummyMap<>();
		FuelManager.bronzeEngineFuel = new FluidMap<>();
		FuelManager.copperEngineFuel = new ItemStackMap<>();
		FuelManager.generatorFuel = new FluidMap<>();
	}

	@Override
	public void registerBlocks() {
		blocks = new BlockRegistryFactory();
	}

	@Override
	public void registerTiles() {
		tiles = new TileRegistryFactory();
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containerTypes = new FactoryContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		FactoryContainerTypes containerTypes = getContainerTypes();
		ScreenManager.registerFactory(containerTypes.BOTTLER, GuiBottler::new);
		ScreenManager.registerFactory(containerTypes.CARPENTER, GuiCarpenter::new);
		ScreenManager.registerFactory(containerTypes.CENTRIFUGE, GuiCentrifuge::new);
		ScreenManager.registerFactory(containerTypes.FABRICATOR, GuiFabricator::new);
		ScreenManager.registerFactory(containerTypes.FERMENTER, GuiFermenter::new);
		ScreenManager.registerFactory(containerTypes.MOISTENER, GuiMoistener::new);
		ScreenManager.registerFactory(containerTypes.RAINTANK, GuiRaintank::new);
		ScreenManager.registerFactory(containerTypes.SQUEEZER, GuiSqueezer::new);
		ScreenManager.registerFactory(containerTypes.STILL, GuiStill::new);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryFactory();
	}

	@Override
	public void preInit() {
		ItemRegistryCore coreItems = ModuleCore.getItems();

		// Set fuels and resources for the fermenter
		ItemStack fertilizerCompound = coreItems.fertilizerCompound.getItemStack();
		FuelManager.fermenterFuel.put(fertilizerCompound, new FermenterFuel(fertilizerCompound,
			ForestryAPI.activeMode.getIntegerSetting("fermenter.value.fertilizer"), ForestryAPI.activeMode.getIntegerSetting("fermenter.cycles.fertilizer")));

		int cyclesCompost = ForestryAPI.activeMode.getIntegerSetting("fermenter.cycles.compost");
		int valueCompost = ForestryAPI.activeMode.getIntegerSetting("fermenter.value.compost");
		ItemStack fertilizerBio = coreItems.compost.getItemStack();
		ItemStack mulch = coreItems.mulch.getItemStack();
		FuelManager.fermenterFuel.put(fertilizerBio, new FermenterFuel(fertilizerBio, valueCompost, cyclesCompost));
		FuelManager.fermenterFuel.put(mulch, new FermenterFuel(mulch, valueCompost, cyclesCompost));

		// Add moistener resources
		ItemStack wheat = new ItemStack(Items.WHEAT);
		ItemStack mouldyWheat = coreItems.mouldyWheat.getItemStack();
		ItemStack decayingWheat = coreItems.decayingWheat.getItemStack();
		FuelManager.moistenerResource.put(wheat, new MoistenerFuel(wheat, mouldyWheat, 0, 300));
		FuelManager.moistenerResource.put(mouldyWheat, new MoistenerFuel(mouldyWheat, decayingWheat, 1, 600));
		FuelManager.moistenerResource.put(decayingWheat, new MoistenerFuel(decayingWheat, mulch, 2, 900));

		// Set fuels for our own engines
		ItemStack peat = coreItems.peat.getItemStack();
		FuelManager.copperEngineFuel.put(peat, new EngineCopperFuel(peat, Constants.ENGINE_COPPER_FUEL_VALUE_PEAT, Constants.ENGINE_COPPER_CYCLE_DURATION_PEAT));

		ItemStack bituminousPeat = coreItems.bituminousPeat.getItemStack();
		FuelManager.copperEngineFuel.put(bituminousPeat, new EngineCopperFuel(bituminousPeat, Constants.ENGINE_COPPER_FUEL_VALUE_BITUMINOUS_PEAT, Constants.ENGINE_COPPER_CYCLE_DURATION_BITUMINOUS_PEAT));

		Fluid biomass = ForestryFluids.BIOMASS.getFluid();
		FuelManager.bronzeEngineFuel.put(biomass, new EngineBronzeFuel(biomass,
			Constants.ENGINE_FUEL_VALUE_BIOMASS, (int) (Constants.ENGINE_CYCLE_DURATION_BIOMASS * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.biogas")), 1));

		FuelManager.bronzeEngineFuel.put(Fluids.WATER, new EngineBronzeFuel(Fluids.WATER,
			Constants.ENGINE_FUEL_VALUE_WATER, Constants.ENGINE_CYCLE_DURATION_WATER, 3));

		Fluid milk = ForestryFluids.MILK.getFluid();
		FuelManager.bronzeEngineFuel.put(milk, new EngineBronzeFuel(milk,
			Constants.ENGINE_FUEL_VALUE_MILK, Constants.ENGINE_CYCLE_DURATION_MILK, 3));

		Fluid seedOil = ForestryFluids.SEED_OIL.getFluid();
		FuelManager.bronzeEngineFuel.put(seedOil, new EngineBronzeFuel(seedOil,
			Constants.ENGINE_FUEL_VALUE_SEED_OIL, Constants.ENGINE_CYCLE_DURATION_SEED_OIL, 1));

		Fluid honey = ForestryFluids.HONEY.getFluid();
		FuelManager.bronzeEngineFuel.put(honey, new EngineBronzeFuel(honey,
			Constants.ENGINE_FUEL_VALUE_HONEY, Constants.ENGINE_CYCLE_DURATION_HONEY, 1));

		Fluid juice = ForestryFluids.JUICE.getFluid();
		FuelManager.bronzeEngineFuel.put(juice, new EngineBronzeFuel(juice,
			Constants.ENGINE_FUEL_VALUE_JUICE, Constants.ENGINE_CYCLE_DURATION_JUICE, 1));

		// Set rain substrates
		ItemStack iodineCharge = coreItems.iodineCharge.getItemStack();
		ItemStack dissipationCharge = coreItems.getCraftingMaterial(EnumCraftingMaterial.DISSIPATION_CHARGE, 1);
		FuelManager.rainSubstrate.put(iodineCharge, new RainSubstrate(iodineCharge, Constants.RAINMAKER_RAIN_DURATION_IODINE, 0.01f));
		FuelManager.rainSubstrate.put(dissipationCharge, new RainSubstrate(dissipationCharge, 0.075f));

		ICircuitLayout layoutMachineUpgrade = new CircuitLayout("machine.upgrade", CircuitSocketType.MACHINE);
		ChipsetManager.circuitRegistry.registerLayout(layoutMachineUpgrade);

	}

	@Override
	public void addLootPoolNames(Set<String> lootPoolNames) {
		lootPoolNames.add("forestry_factory_items");
	}

	@Override
	public void registerTriggers() {
		//		FactoryTriggers.initialize();
	}

	@Override
	public void doInit() {
		BlockRegistryFactory blocks = getBlocks();

		blocks.bottler.init();
		blocks.carpenter.init();
		blocks.centrifuge.init();
		blocks.fermenter.init();
		blocks.moistener.init();
		blocks.squeezer.init();
		blocks.still.init();
		blocks.rainmaker.init();

		blocks.fabricator.init();
		blocks.raintank.init();

		Circuits.machineSpeedUpgrade1 = new CircuitSpeedUpgrade("machine.speed.boost.1", 0.125f, 0.05f);
		Circuits.machineSpeedUpgrade2 = new CircuitSpeedUpgrade("machine.speed.boost.2", 0.250f, 0.10f);
		Circuits.machineEfficiencyUpgrade1 = new CircuitSpeedUpgrade("machine.efficiency.1", 0, -0.10f);
	}

	@Override
	public void registerRecipes() {
		// / FABRICATOR
		ItemRegistryCore coreItems = ModuleCore.getItems();
		BlockRegistryCore coreBlocks = ModuleCore.getBlocks();
		ItemRegistryFluids fluidItems = ModuleFluids.getItems();
		BlockRegistryFactory blocks = getBlocks();

		FluidStack liquidGlass = ForestryFluids.GLASS.getFluid(500);
		if (!liquidGlass.isEmpty()) {
			//TODO json
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.COPPER, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotCopper"});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.TIN, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotTin"});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.BRONZE, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotBronze"});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.IRON, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotIron"});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.GOLD, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotGold"});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.DIAMOND, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "gemDiamond"});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.OBSIDIAN, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', Blocks.OBSIDIAN});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.BLAZE, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', Items.BLAZE_POWDER});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.EMERALD, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "gemEmerald"});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.APATITE, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "gemApatite"});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.LAPIS, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', new ItemStack(Items.LAPIS_LAZULI)});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.ENDER, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', new ItemStack(Items.ENDER_EYE),
				'X', new ItemStack(Blocks.END_STONE)});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.getElectronTube(EnumElectronTube.ORCHID, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', new ItemStack(Items.REPEATER),
				'X', new ItemStack(Blocks.REDSTONE_ORE)});
			RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, liquidGlass, coreItems.flexibleCasing.getItemStack(), new Object[]{
				"#E#",
				"B B",
				"#E#",
				'#', OreDictUtil.INGOT_BRONZE,
				'B', OreDictUtil.SLIMEBALL,
				'E', "gemEmerald"});
		}
		String[] dyes = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime",
			"dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};

		FluidStack liquidGlassBucket = ForestryFluids.GLASS.getFluid(FluidAttributes.BUCKET_VOLUME);
		FluidStack liquidGlassX4 = ForestryFluids.GLASS.getFluid(FluidAttributes.BUCKET_VOLUME * 4);

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();

			if (!liquidGlassBucket.isEmpty() && !liquidGlassX4.isEmpty()) {
				for (int i = 0; i < 16; i++) {
					//TODO - needs tag loop or tag match in recipe
					//					RecipeManagers.fabricatorManager.addRecipe(beeItems.waxCast.getWildcard(), liquidGlassBucket, new ItemStack(Blocks.STAINED_GLASS, 4, 15 - i), new Object[]{
					//						"#", "X",
					//						'#', dyes[i],
					//						'X', beeItems.propolis.getWildcard()});
				}
				//				RecipeManagers.fabricatorManager.addRecipe(beeItems.waxCast.getWildcard(), liquidGlassX4, new ItemStack(Blocks.GLASS), new Object[]{
				//					"X",
				//					'X', beeItems.propolis.getWildcard()});	//TODO needs tag
			}
		}

		// / SQUEEZER
		int appleMulchAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
		int appleJuiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		FluidStack appleJuice = ForestryFluids.JUICE.getFluid(appleJuiceAmount);
		if (!appleJuice.isEmpty()) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.APPLE), appleJuice,
				coreItems.mulch.getItemStack(), appleMulchAmount);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.CARROT), appleJuice,
				coreItems.mulch.getItemStack(), appleMulchAmount);
		}
		int seedOilAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedOil = ForestryFluids.SEED_OIL.getFluid(seedOilAmount);
		if (!seedOil.isEmpty()) {
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.WHEAT_SEEDS), seedOil);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.PUMPKIN_SEEDS), seedOil);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.MELON_SEEDS), seedOil);
			RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.BEETROOT_SEEDS), seedOil);
		}

		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Blocks.CACTUS), new FluidStack(Fluids.WATER, 500));

		NonNullList<ItemStack> lavaRecipeResources = NonNullList.create();
		lavaRecipeResources.add(coreItems.phosphor.getItemStack(2));
		lavaRecipeResources.add(new ItemStack(Blocks.COBBLESTONE));
		RecipeManagers.squeezerManager.addRecipe(10, lavaRecipeResources, new FluidStack(Fluids.LAVA, 1600));

		NonNullList<ItemStack> iceRecipeResources = NonNullList.create();
		iceRecipeResources.add(new ItemStack(Items.SNOWBALL));
		iceRecipeResources.add(coreItems.getCraftingMaterial(EnumCraftingMaterial.ICE_SHARD, 4));
		FluidStack liquidIce = ForestryFluids.ICE.getFluid(4000);
		if (!liquidIce.isEmpty()) {
			RecipeManagers.squeezerManager.addRecipe(10, iceRecipeResources, liquidIce);
		}
		// STILL
		FluidStack biomass = ForestryFluids.BIOMASS.getFluid(Constants.STILL_DESTILLATION_INPUT);
		FluidStack ethanol = ForestryFluids.BIO_ETHANOL.getFluid(Constants.STILL_DESTILLATION_OUTPUT);
		if (!biomass.isEmpty() && !ethanol.isEmpty()) {
			RecipeManagers.stillManager.addRecipe(Constants.STILL_DESTILLATION_DURATION, biomass, ethanol);
		}
		// MOISTENER
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Blocks.MYCELIUM), 5000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.MOSSY_COBBLESTONE), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.STONE_BRICKS), new ItemStack(Blocks.MOSSY_STONE_BRICKS), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.SPRUCE_LEAVES), new ItemStack(Blocks.PODZOL), 5000);

		// FERMENTER
		RecipeUtil.addFermenterRecipes(OreDictUtil.TREE_SAPLING, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), ForestryFluids.BIOMASS);

		RecipeUtil.addFermenterRecipes(OreDictUtil.BLOCK_CACTUS, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.cactus"), ForestryFluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(OreDictUtil.CROP_WHEAT, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), ForestryFluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(OreDictUtil.CROP_POTATO, 2 * ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), ForestryFluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(OreDictUtil.SUGARCANE, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.cane"), ForestryFluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.BROWN_MUSHROOM), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"), ForestryFluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.RED_MUSHROOM), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"), ForestryFluids.BIOMASS);

		// FABRICATOR
		FluidStack liquidGlass375 = ForestryFluids.GLASS.getFluid(375);
		if (!liquidGlass375.isEmpty() && !liquidGlassBucket.isEmpty() && !liquidGlassX4.isEmpty()) {
			RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.GLASS), liquidGlassBucket, 1000);
			RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.GLASS_PANE), liquidGlass375, 1000);
			RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.SAND), liquidGlassBucket, 3000);
			RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.RED_SAND), liquidGlassBucket, 3000);
			RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.SANDSTONE), liquidGlassX4, 4800);
			RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.SMOOTH_SANDSTONE), liquidGlassX4, 4800);
			RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.CHISELED_SANDSTONE), liquidGlassX4, 4800);
			//TODO red sandstone
		}
		// / CARPENTER
		RecipeManagers.carpenterManager.addRecipe(50, ForestryFluids.SEED_OIL.getFluid(250), ItemStack.EMPTY, coreItems.impregnatedCasing.getItemStack(),
			"###",
			"# #",
			"###",
			'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(50, ForestryFluids.SEED_OIL.getFluid(500), ItemStack.EMPTY,
			new ItemStack(coreBlocks.escritoire),
			"#  ",
			"###",
			"# #",
			'#', "plankWood");

		// RESOURCES
		RecipeManagers.carpenterManager.addRecipe(10, ForestryFluids.SEED_OIL.getFluid(100), ItemStack.EMPTY,
			coreItems.stickImpregnated.getItemStack(2),
			"#",
			"#",
			'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(Fluids.WATER, 250), ItemStack.EMPTY,
			coreItems.woodPulp.getItemStack(4),
			"#",
			'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(Fluids.WATER, 250), ItemStack.EMPTY,
			new ItemStack(Items.PAPER, 1),
			"#",
			"#",
			'#', "pulpWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY,
			new ItemStack(coreBlocks.humus, 9),
			"###",
			"#X#",
			"###",
			'#', Blocks.DIRT,
			'X', coreItems.mulch);
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY,
			coreBlocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, 8),
			"#X#",
			"XYX", "#X#",
			'#', Blocks.DIRT,
			'X', "sand",
			'Y', coreItems.mulch);
		RecipeManagers.carpenterManager.addRecipe(75, new FluidStack(Fluids.WATER, 5000), ItemStack.EMPTY, coreItems.hardenedCasing.getItemStack(),
			"# #",
			" Y ",
			"# #",
			'#', "gemDiamond",
			'Y', coreItems.sturdyCasing);

		// / CHIPSETS
		ItemStack basicCircuitboard = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.BASIC, null, new ICircuit[]{});
		ItemStack enhancedCircuitboard = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.ENHANCED, null, new ICircuit[]{});
		ItemStack refinedCircuitboard = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{});
		ItemStack intricateCircuitboard = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.INTRICATE, null, new ICircuit[]{});

		RecipeManagers.carpenterManager.addRecipe(20, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY, basicCircuitboard,
			"R R", "R#R", "R R", '#', "ingotTin", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(40, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY, enhancedCircuitboard,
			"R#R", "R#R", "R#R", '#', "ingotBronze", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(80, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY, refinedCircuitboard,
			"R#R", "R#R", "R#R", '#', "ingotIron", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(80, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY, intricateCircuitboard,
			"R#R", "R#R", "R#R", '#', "ingotGold", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(40, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY, coreItems.solderingIron.getItemStack(),
			" # ", "# #", "  B", '#', "ingotIron", 'B', "ingotBronze");

		// RAIN SUBSTRATES


		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();
			RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY, coreItems.iodineCharge.getItemStack(),
				"Z#Z",
				"#Y#",
				"X#X",
				'#', beeItems.getPollen(EnumPollenCluster.NORMAL, 1),    //TODO was a tag before
				'X', Items.GUNPOWDER,
				'Y', fluidItems.canEmpty,
				'Z', beeItems.getHoneyDrop(EnumHoneyDrop.HONEY, 1));
			RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY, coreItems.getCraftingMaterial(EnumCraftingMaterial.DISSIPATION_CHARGE, 1),
				"Z#Z",
				"#Y#",
				"X#X",
				'#', beeItems.royalJelly,
				'X', Items.GUNPOWDER,
				'Y', fluidItems.canEmpty,
				'Z', beeItems.honeydew);
		}

		// Ender pearl
		RecipeManagers.carpenterManager.addRecipe(100, ItemStack.EMPTY, new ItemStack(Items.ENDER_PEARL, 1), " # ", "###", " # ", '#',
			coreItems.getCraftingMaterial(EnumCraftingMaterial.PULSATING_MESH, 1));

		// Woven Silk
		RecipeManagers.carpenterManager.addRecipe(10, new FluidStack(Fluids.WATER, 500), ItemStack.EMPTY, coreItems.getCraftingMaterial(EnumCraftingMaterial.WOVEN_SILK, 1),
			"###",
			"###",
			"###",
			'#', coreItems.getCraftingMaterial(EnumCraftingMaterial.SILK_WISP, 1));

		// Boxes
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(Fluids.WATER, 1000), ItemStack.EMPTY, coreItems.carton.getItemStack(2),
			" # ", "# #", " # ", '#', "pulpWood");

		// Assembly Kits
		RecipeManagers.carpenterManager.addRecipe(20, null, coreItems.carton.getItemStack(), coreItems.kitPickaxe.getItemStack(), new Object[]{
			"###",
			" X ",
			" X ",
			'#', "ingotBronze",
			'X', "stickWood"});

		RecipeManagers.carpenterManager.addRecipe(20, null, coreItems.carton.getItemStack(), coreItems.kitShovel.getItemStack(),
			new Object[]{" # ", " X ", " X ", '#', "ingotBronze", 'X', "stickWood"});

		// Reclamation
		ItemStack ingotBronze = coreItems.ingotBronze.copy();
		ingotBronze.setCount(2);
		RecipeManagers.carpenterManager.addRecipe(ItemStack.EMPTY, ingotBronze, "#", '#', coreItems.brokenBronzePickaxe);

		ingotBronze = ingotBronze.copy();
		ingotBronze.setCount(1);
		RecipeManagers.carpenterManager.addRecipe(ItemStack.EMPTY, ingotBronze, "#", '#', coreItems.brokenBronzeShovel);

		// Crating and uncrating
		if (ModuleHelper.isEnabled(ForestryModuleUids.CRATE)) {
			ModuleCrates.createCrateRecipes();
		}
		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.machine.upgrade");

		// / Solder Manager
		if (layout != null) {
			ChipsetManager.solderManager.addRecipe(layout, coreItems.getElectronTube(EnumElectronTube.EMERALD, 1), Circuits.machineSpeedUpgrade1);
			ChipsetManager.solderManager.addRecipe(layout, coreItems.getElectronTube(EnumElectronTube.BLAZE, 1), Circuits.machineSpeedUpgrade2);
			ChipsetManager.solderManager.addRecipe(layout, coreItems.getElectronTube(EnumElectronTube.GOLD, 1), Circuits.machineEfficiencyUpgrade1);
		}
	}

	public static void loadMachineConfig(LocalizedConfiguration config) {
		List<String> enabled = Arrays.asList(config.getStringListLocalized("machines", "enabled", MachineUIDs.ALL.toArray(new String[0]), MachineUIDs.ALL.toArray(new String[0])));
		for (String machineID : MachineUIDs.ALL) {
			MACHINE_ENABLED.put(machineID, enabled.contains(machineID));
		}
	}

	public static boolean machineEnabled(String machineName) {
		Boolean ret = MACHINE_ENABLED.get(machineName);
		return ret != null && ret;
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onClientSetup(FMLClientSetupEvent event) {
		blocks.bottler.clientInit();
		blocks.carpenter.clientInit();
		blocks.centrifuge.clientInit();
		blocks.fermenter.clientInit();
		blocks.moistener.clientInit();
		blocks.squeezer.clientInit();
		blocks.still.clientInit();
		blocks.rainmaker.clientInit();

		blocks.fabricator.clientInit();
		blocks.raintank.clientInit();
	}
}

