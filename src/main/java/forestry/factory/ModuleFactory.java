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

import javax.annotation.Nullable;
import java.util.Set;

import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.book.IBookCategory;
import forestry.api.book.IForesterBook;
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
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.items.ItemRegistryFluids;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.core.utils.datastructures.FluidMap;
import forestry.core.utils.datastructures.ItemStackMap;
import forestry.factory.blocks.BlockRegistryFactory;
import forestry.factory.circuits.CircuitSpeedUpgrade;
import forestry.factory.network.PacketRegistryFactory;
import forestry.factory.recipes.CarpenterRecipeManager;
import forestry.factory.recipes.CentrifugeRecipeManager;
import forestry.factory.recipes.FabricatorRecipeManager;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;
import forestry.factory.recipes.FermenterRecipeManager;
import forestry.factory.recipes.MoistenerRecipeManager;
import forestry.factory.recipes.SqueezerRecipeManager;
import forestry.factory.recipes.StillRecipeManager;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.storage.ModuleCrates;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FACTORY, name = "Factory", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.factory.description", lootTable = "factory")
public class ModuleFactory extends BlankForestryModule {
	@Nullable
	private static BlockRegistryFactory blocks;

	public static BlockRegistryFactory getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public void setupAPI() {
		RecipeManagers.carpenterManager = new CarpenterRecipeManager();
		RecipeManagers.centrifugeManager = new CentrifugeRecipeManager();
		RecipeManagers.fabricatorManager = new FabricatorRecipeManager();
		RecipeManagers.fabricatorSmeltingManager = new FabricatorSmeltingRecipeManager();
		RecipeManagers.fermenterManager = new FermenterRecipeManager();
		RecipeManagers.moistenerManager = new MoistenerRecipeManager();
		RecipeManagers.squeezerManager = new SqueezerRecipeManager();
		RecipeManagers.stillManager = new StillRecipeManager();

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
		FuelManager.fermenterFuel = new ItemStackMap<>();
		FuelManager.moistenerResource = new ItemStackMap<>();
		FuelManager.rainSubstrate = new ItemStackMap<>();
		FuelManager.bronzeEngineFuel = new FluidMap<>();
		FuelManager.copperEngineFuel = new ItemStackMap<>();
		FuelManager.generatorFuel = new FluidMap<>();
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryFactory();
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

		Fluid biomass = Fluids.BIOMASS.getFluid();
		if (biomass != null) {
			FuelManager.bronzeEngineFuel.put(biomass, new EngineBronzeFuel(biomass,
					Constants.ENGINE_FUEL_VALUE_BIOMASS, (int) (Constants.ENGINE_CYCLE_DURATION_BIOMASS * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.biogas")), 1));
		}

		FuelManager.bronzeEngineFuel.put(FluidRegistry.WATER, new EngineBronzeFuel(FluidRegistry.WATER,
				Constants.ENGINE_FUEL_VALUE_WATER, Constants.ENGINE_CYCLE_DURATION_WATER, 3));

		Fluid milk = Fluids.MILK.getFluid();
		if (milk != null) {
			FuelManager.bronzeEngineFuel.put(milk, new EngineBronzeFuel(milk,
					Constants.ENGINE_FUEL_VALUE_MILK, Constants.ENGINE_CYCLE_DURATION_MILK, 3));
		}

		Fluid seedOil = Fluids.SEED_OIL.getFluid();
		if (seedOil != null) {
			FuelManager.bronzeEngineFuel.put(seedOil, new EngineBronzeFuel(seedOil,
					Constants.ENGINE_FUEL_VALUE_SEED_OIL, Constants.ENGINE_CYCLE_DURATION_SEED_OIL, 1));
		}

		Fluid honey = Fluids.FOR_HONEY.getFluid();
		if (honey != null) {
			FuelManager.bronzeEngineFuel.put(honey, new EngineBronzeFuel(honey,
					Constants.ENGINE_FUEL_VALUE_HONEY, Constants.ENGINE_CYCLE_DURATION_HONEY, 1));
		}

		Fluid juice = Fluids.JUICE.getFluid();
		if (juice != null) {
			FuelManager.bronzeEngineFuel.put(juice, new EngineBronzeFuel(juice,
					Constants.ENGINE_FUEL_VALUE_JUICE, Constants.ENGINE_CYCLE_DURATION_JUICE, 1));
		}

		// Set rain substrates
		ItemStack iodineCharge = coreItems.iodineCharge.getItemStack();
		ItemStack dissipationCharge = coreItems.craftingMaterial.getDissipationCharge();
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

		ItemElectronTube electronTube = coreItems.tubes;

		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.COPPER, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotCopper"});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.TIN, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotTin"});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.BRONZE, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotBronze"});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.IRON, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotIron"});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.GOLD, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "ingotGold"});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.DIAMOND, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "gemDiamond"});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.OBSIDIAN, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', Blocks.OBSIDIAN});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.BLAZE, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', Items.BLAZE_POWDER});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.EMERALD, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "gemEmerald"});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.APATITE, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', "gemApatite"});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.LAPIS, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', "dustRedstone",
				'X', new ItemStack(Items.DYE, 1, 4)});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.ENDER, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', new ItemStack(Items.ENDER_EYE, 1, 0),
				'X', new ItemStack(Blocks.END_STONE, 1, 0)});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.ORCHID, 4), new Object[]{
				" X ",
				"#X#",
				"XXX",
				'#', new ItemStack(Items.REPEATER, 1, 0),
				'X', new ItemStack(Blocks.REDSTONE_ORE, 1, 0)});
		RecipeManagers.fabricatorManager.addRecipe(ItemStack.EMPTY, Fluids.GLASS.getFluid(500), coreItems.flexibleCasing.getItemStack(), new Object[]{
			"#E#",
			"B B",
			"#E#",
			'#', OreDictUtil.INGOT_BRONZE,
			'B', OreDictUtil.SLIMEBALL,
			'E', "gemEmerald"});
		String[] dyes = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime",
				"dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();

			FluidStack liquidGlass = Fluids.GLASS.getFluid(Fluid.BUCKET_VOLUME);
			FluidStack liquidGlassX4 = Fluids.GLASS.getFluid(Fluid.BUCKET_VOLUME * 4);
			for (int i = 0; i < 16; i++) {
				RecipeManagers.fabricatorManager.addRecipe(beeItems.waxCast.getWildcard(), liquidGlass, new ItemStack(Blocks.STAINED_GLASS, 4, 15 - i), new Object[]{
						"#", "X",
						'#', dyes[i],
						'X', beeItems.propolis.getWildcard()});
			}
			RecipeManagers.fabricatorManager.addRecipe(beeItems.waxCast.getWildcard(), liquidGlassX4, new ItemStack(Blocks.GLASS, 1, 0), new Object[]{
					"X",
					'X', beeItems.propolis.getWildcard()});
		}

		// / SQUEEZER
		int appleMulchAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
		int appleJuiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.APPLE), Fluids.JUICE.getFluid(appleJuiceAmount),
				coreItems.mulch.getItemStack(), appleMulchAmount);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.CARROT), Fluids.JUICE.getFluid(appleJuiceAmount),
				coreItems.mulch.getItemStack(), appleMulchAmount);

		int seedOilAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedOil = Fluids.SEED_OIL.getFluid(seedOilAmount);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.WHEAT_SEEDS), seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.PUMPKIN_SEEDS), seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.MELON_SEEDS), seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.BEETROOT_SEEDS), seedOil);

		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Blocks.CACTUS), new FluidStack(FluidRegistry.WATER, 500));

		NonNullList<ItemStack> lavaRecipeResources = NonNullList.create();
		lavaRecipeResources.add(coreItems.phosphor.getItemStack(2));
		lavaRecipeResources.add(new ItemStack(Blocks.COBBLESTONE));
		RecipeManagers.squeezerManager.addRecipe(10, lavaRecipeResources, new FluidStack(FluidRegistry.LAVA, 1600));

		NonNullList<ItemStack> iceRecipeResources = NonNullList.create();
		iceRecipeResources.add(new ItemStack(Items.SNOWBALL));
		iceRecipeResources.add(coreItems.craftingMaterial.getIceShard(4));
		RecipeManagers.squeezerManager.addRecipe(10, iceRecipeResources, Fluids.ICE.getFluid(4000));

		// STILL
		RecipeManagers.stillManager.addRecipe(Constants.STILL_DESTILLATION_DURATION, Fluids.BIOMASS.getFluid(Constants.STILL_DESTILLATION_INPUT),
				Fluids.BIO_ETHANOL.getFluid(Constants.STILL_DESTILLATION_OUTPUT));

		// MOISTENER
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Blocks.MYCELIUM), 5000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.MOSSY_COBBLESTONE), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 1), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.LEAVES, 1, EnumType.SPRUCE.ordinal()), new ItemStack(Blocks.DIRT, 1, DirtType.PODZOL.ordinal()), 5000);

		// FERMENTER
		RecipeUtil.addFermenterRecipes(OreDictUtil.TREE_SAPLING, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);

		RecipeUtil.addFermenterRecipes(OreDictUtil.BLOCK_CACTUS, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.cactus"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(OreDictUtil.CROP_WHEAT, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(OreDictUtil.CROP_POTATO, 2 * ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(OreDictUtil.SUGARCANE, ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.cane"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.BROWN_MUSHROOM), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.RED_MUSHROOM), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"), Fluids.BIOMASS);

		// FABRICATOR

		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.GLASS), Fluids.GLASS.getFluid(1000), 1000);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.GLASS_PANE), Fluids.GLASS.getFluid(375), 1000);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.SAND), Fluids.GLASS.getFluid(1000), 3000);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.SAND, 1, 1), Fluids.GLASS.getFluid(1000), 3000);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.SANDSTONE), Fluids.GLASS.getFluid(4000), 4800);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.SANDSTONE, 1, 1), Fluids.GLASS.getFluid(4000), 4800);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.SANDSTONE, 1, 2), Fluids.GLASS.getFluid(4000), 4800);

		// / CARPENTER
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEED_OIL.getFluid(250), ItemStack.EMPTY, coreItems.impregnatedCasing.getItemStack(),
				"###",
				"# #",
				"###",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEED_OIL.getFluid(500), ItemStack.EMPTY,
				new ItemStack(coreBlocks.escritoire),
				"#  ",
				"###",
				"# #",
				'#', "plankWood");

		// RESOURCES
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEED_OIL.getFluid(100), ItemStack.EMPTY,
				coreItems.stickImpregnated.getItemStack(2),
				"#",
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 250), ItemStack.EMPTY,
				coreItems.woodPulp.getItemStack(4),
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 250), ItemStack.EMPTY,
				new ItemStack(Items.PAPER, 1),
				"#",
				"#",
				'#', "pulpWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY,
				new ItemStack(coreBlocks.humus, 9),
				"###",
				"#X#",
				"###",
				'#', Blocks.DIRT,
				'X', coreItems.mulch);
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY,
				coreBlocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, 8),
				"#X#",
				"XYX", "#X#",
				'#', Blocks.DIRT,
				'X', "sand",
				'Y', coreItems.mulch);
		RecipeManagers.carpenterManager.addRecipe(75, new FluidStack(FluidRegistry.WATER, 5000), ItemStack.EMPTY, coreItems.hardenedCasing.getItemStack(),
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

		RecipeManagers.carpenterManager.addRecipe(20, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY, basicCircuitboard,
				"R R", "R#R", "R R", '#', "ingotTin", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(40, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY, enhancedCircuitboard,
				"R#R", "R#R", "R#R", '#', "ingotBronze", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(80, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY, refinedCircuitboard,
				"R#R", "R#R", "R#R", '#', "ingotIron", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(80, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY, intricateCircuitboard,
				"R#R", "R#R", "R#R", '#', "ingotGold", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(40, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY, coreItems.solderingIron.getItemStack(),
				" # ", "# #", "  B", '#', "ingotIron", 'B', "ingotBronze");

		// RAIN SUBSTRATES


		if (ModuleHelper.isEnabled(Constants.MOD_ID, ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();
			RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY, coreItems.iodineCharge.getItemStack(),
					"Z#Z",
					"#Y#",
					"X#X",
					'#', beeItems.pollenCluster.getWildcard(),
					'X', Items.GUNPOWDER,
					'Y', fluidItems.canEmpty,
					'Z', beeItems.honeyDrop);
			RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY, coreItems.craftingMaterial.getDissipationCharge(),
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
				coreItems.craftingMaterial.getPulsatingMesh());

		// Woven Silk
		RecipeManagers.carpenterManager.addRecipe(10, new FluidStack(FluidRegistry.WATER, 500), ItemStack.EMPTY, coreItems.craftingMaterial.getWovenSilk(),
				"###",
				"###",
				"###",
				'#', coreItems.craftingMaterial.getSilkWisp());

		// Boxes
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), ItemStack.EMPTY, coreItems.carton.getItemStack(2),
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
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.EMERALD, 1), Circuits.machineSpeedUpgrade1);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.BLAZE, 1), Circuits.machineSpeedUpgrade2);
		ChipsetManager.solderManager.addRecipe(layout, coreItems.tubes.get(EnumElectronTube.GOLD, 1), Circuits.machineEfficiencyUpgrade1);

		RecipeUtil.addRecipe("bottler", blocks.bottler,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', fluidItems.canEmpty,
				'Y', coreItems.sturdyCasing);

		RecipeUtil.addRecipe("carpenter", blocks.carpenter,
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotBronze",
				'Y', coreItems.sturdyCasing);

		RecipeUtil.addRecipe("centrifuge", blocks.centrifuge,
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotCopper",
				'Y', coreItems.sturdyCasing);

		RecipeUtil.addRecipe("fermenter", blocks.fermenter,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearBronze",
				'Y', coreItems.sturdyCasing);

		RecipeUtil.addRecipe("moistener", blocks.moistener,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearCopper",
				'Y', coreItems.sturdyCasing);

		RecipeUtil.addRecipe("squeezer", blocks.squeezer,
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotTin",
				'Y', coreItems.sturdyCasing);

		RecipeUtil.addRecipe("still", blocks.still,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "dustRedstone",
				'Y', coreItems.sturdyCasing);

		RecipeUtil.addRecipe("rainmaker", blocks.rainmaker,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearTin",
				'Y', coreItems.hardenedCasing);

		RecipeUtil.addRecipe("fabricator", blocks.fabricator,
				"X#X",
				"#Y#",
				"XZX",
				'#', "blockGlass",
				'X', "ingotGold",
				'Y', coreItems.sturdyCasing,
				'Z', "chestWood");

		RecipeUtil.addRecipe("raintank", blocks.raintank,
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotIron",
				'Y', coreItems.sturdyCasing);
	}

	@Override
	public void registerBookEntries(IForesterBook book) {
		IBookCategory category = book.addCategory("core")
				.addEntry("carpenter", new ItemStack(getBlocks().carpenter))
				.addEntry("centrifuge", new ItemStack(getBlocks().centrifuge))
				.addEntry("fabricator", new ItemStack(getBlocks().fabricator))
				.addEntry("moistener", new ItemStack(getBlocks().moistener))
				.addEntry("fermenter", new ItemStack(getBlocks().fermenter))
				.addEntry("raintank", new ItemStack(getBlocks().raintank))
				.addEntry("bottler", new ItemStack(getBlocks().bottler))
				.addEntry("still", new ItemStack(getBlocks().still))
				.addEntry("rainmaker", new ItemStack(getBlocks().rainmaker));
	}
}
