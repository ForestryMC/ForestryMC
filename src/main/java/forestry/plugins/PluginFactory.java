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

import com.google.common.collect.ImmutableList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.EngineCopperFuel;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.fuels.RainSubstrate;
import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.blocks.BlockCoreType;
import forestry.core.blocks.BlockSoil;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.items.EnumElectronTube;
import forestry.core.items.ItemElectronTube;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.core.recipes.craftguide.CraftGuideIntegration;
import forestry.core.tiles.MachineDefinition;
import forestry.core.utils.Log;
import forestry.core.utils.ModUtil;
import forestry.core.utils.datastructures.FluidMap;
import forestry.core.utils.datastructures.ItemStackMap;
import forestry.factory.DummyManagers;
import forestry.factory.blocks.BlockFactoryPlainType;
import forestry.factory.blocks.BlockFactoryTesrType;
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
import forestry.factory.triggers.FactoryTriggers;

@Plugin(pluginID = "Factory", name = "Factory", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.factory.description")
public class PluginFactory extends ForestryPlugin {
	public static BlockRegistryFactory blocks;

	@Override
	protected void setupAPI() {
		super.setupAPI();

		RecipeManagers.craftingProviders = ImmutableList.<ICraftingProvider>of(
				RecipeManagers.carpenterManager = new CarpenterRecipeManager(),
				RecipeManagers.centrifugeManager = new CentrifugeRecipeManager(),
				RecipeManagers.fabricatorManager = new FabricatorRecipeManager(),
				RecipeManagers.fabricatorSmeltingManager = new FabricatorSmeltingRecipeManager(),
				RecipeManagers.fermenterManager = new FermenterRecipeManager(),
				RecipeManagers.moistenerManager = new MoistenerRecipeManager(),
				RecipeManagers.squeezerManager = new SqueezerRecipeManager(),
				RecipeManagers.stillManager = new StillRecipeManager()
		);

		setupFuelManager();
	}

	@Override
	protected void disabledSetupAPI() {
		super.disabledSetupAPI();

		RecipeManagers.craftingProviders = ImmutableList.<ICraftingProvider>of(
				RecipeManagers.carpenterManager = new DummyManagers.DummyCarpenterManager(),
				RecipeManagers.centrifugeManager = new DummyManagers.DummyCentrifugeManager(),
				RecipeManagers.fabricatorManager = new DummyManagers.DummyFabricatorManager(),
				RecipeManagers.fabricatorSmeltingManager = new DummyManagers.DummyFabricatorSmeltingManager(),
				RecipeManagers.fermenterManager = new DummyManagers.DummyFermenterManager(),
				RecipeManagers.moistenerManager = new DummyManagers.DummyMoistenerManager(),
				RecipeManagers.squeezerManager = new DummyManagers.DummySqueezerManager(),
				RecipeManagers.stillManager = new DummyManagers.DummyStillManager()
		);

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
	protected void registerItemsAndBlocks() {
		blocks = new BlockRegistryFactory();
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryFactory();
	}

	@Override
	public void preInit() {
		super.preInit();

		ItemRegistryCore coreItems = PluginCore.items;

		// Set fuels and resources for the fermenter
		ItemStack fertilizerCompound = coreItems.fertilizerCompound.getItemStack();
		FuelManager.fermenterFuel.put(fertilizerCompound, new FermenterFuel(fertilizerCompound,
				ForestryAPI.activeMode.getIntegerSetting("fermenter.value.fertilizer"), ForestryAPI.activeMode.getIntegerSetting("fermenter.cycles.fertilizer")));

		int cyclesCompost = ForestryAPI.activeMode.getIntegerSetting("fermenter.cycles.compost");
		int valueCompost = ForestryAPI.activeMode.getIntegerSetting("fermenter.value.compost");
		ItemStack fertilizerBio = coreItems.fertilizerBio.getItemStack();
		ItemStack mulch = coreItems.mulch.getItemStack();
		FuelManager.fermenterFuel.put(fertilizerBio, new FermenterFuel(fertilizerBio, valueCompost, cyclesCompost));
		FuelManager.fermenterFuel.put(mulch, new FermenterFuel(mulch, valueCompost, cyclesCompost));

		// Add moistener resources
		ItemStack wheat = new ItemStack(Items.wheat);
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

		FuelManager.bronzeEngineFuel.put(Fluids.BIOMASS.getFluid(), new EngineBronzeFuel(Fluids.BIOMASS.getFluid(),
				Constants.ENGINE_FUEL_VALUE_BIOMASS, (int) (Constants.ENGINE_CYCLE_DURATION_BIOMASS * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.biogas")), 1));
		FuelManager.bronzeEngineFuel.put(Fluids.WATER.getFluid(), new EngineBronzeFuel(Fluids.WATER.getFluid(),
				Constants.ENGINE_FUEL_VALUE_WATER, Constants.ENGINE_CYCLE_DURATION_WATER, 3));
		FuelManager.bronzeEngineFuel.put(Fluids.MILK.getFluid(), new EngineBronzeFuel(Fluids.MILK.getFluid(),
				Constants.ENGINE_FUEL_VALUE_MILK, Constants.ENGINE_CYCLE_DURATION_MILK, 3));
		FuelManager.bronzeEngineFuel.put(Fluids.SEEDOIL.getFluid(), new EngineBronzeFuel(Fluids.SEEDOIL.getFluid(),
				Constants.ENGINE_FUEL_VALUE_SEED_OIL, Constants.ENGINE_CYCLE_DURATION_SEED_OIL, 1));
		FuelManager.bronzeEngineFuel.put(Fluids.HONEY.getFluid(), new EngineBronzeFuel(Fluids.HONEY.getFluid(),
				Constants.ENGINE_FUEL_VALUE_HONEY, Constants.ENGINE_CYCLE_DURATION_HONEY, 1));
		FuelManager.bronzeEngineFuel.put(Fluids.JUICE.getFluid(), new EngineBronzeFuel(Fluids.JUICE.getFluid(),
				Constants.ENGINE_FUEL_VALUE_JUICE, Constants.ENGINE_CYCLE_DURATION_JUICE, 1));

		// Set rain substrates
		ItemStack iodineCharge = coreItems.iodineCharge.getItemStack();
		ItemStack dissipationCharge = coreItems.craftingMaterial.getDissipationCharge();
		FuelManager.rainSubstrate.put(iodineCharge, new RainSubstrate(iodineCharge, Constants.RAINMAKER_RAIN_DURATION_IODINE, 0.01f));
		FuelManager.rainSubstrate.put(dissipationCharge, new RainSubstrate(dissipationCharge, 0.075f));

		for (BlockFactoryTesrType type : BlockFactoryTesrType.VALUES) {
			MachineDefinition machineDefinition = new MachineDefinition(type);
			blocks.factoryTESR.addDefinition(machineDefinition);
		}

		blocks.factoryPlain.addDefinitions(
				new MachineDefinition(BlockFactoryPlainType.FABRICATOR).setFaces(0, 1, 2, 3, 4, 4),
				new MachineDefinition(BlockFactoryPlainType.RAINTANK).setFaces(0, 1, 0, 0, 0, 0),
				new MachineDefinition(BlockFactoryPlainType.WORKTABLE).setFaces(0, 1, 2, 3, 4, 4)
		);

		ICircuitLayout layoutMachineUpgrade = new CircuitLayout("machine.upgrade", CircuitSocketType.MACHINE);
		ChipsetManager.circuitRegistry.registerLayout(layoutMachineUpgrade);

	}

	@Override
	protected void registerTriggers() {
		FactoryTriggers.initialize();
	}

	@Override
	public void doInit() {
		super.doInit();

		blocks.factoryTESR.init();
		blocks.factoryPlain.init();

		Circuit.machineSpeedUpgrade1 = new CircuitSpeedUpgrade("machine.speed.boost.1", 0.125f, 0.05f, 4);
		Circuit.machineSpeedUpgrade2 = new CircuitSpeedUpgrade("machine.speed.boost.2", 0.250f, 0.10f, 4);
		Circuit.machineEfficiencyUpgrade1 = new CircuitSpeedUpgrade("machine.efficiency.1", 0, -0.10f, 2);
	}

	@Override
	public void postInit() {
		super.postInit();

		if (ModUtil.isModLoaded("craftguide")) {
			CraftGuideIntegration.register();
		} else {
			Log.info("Skipping CraftGuide integration.");
		}
	}

	@Override
	protected void registerRecipes() {

		// / FABRICATOR
		ItemElectronTube electronTube = PluginCore.items.tubes;
		
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.COPPER, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotCopper"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.TIN, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotTin"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.BRONZE, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotBronze"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.IRON, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotIron"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.GOLD, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "ingotGold"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.DIAMOND, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "gemDiamond"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.OBSIDIAN, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', Blocks.obsidian});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.BLAZE, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', Items.blaze_powder});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.EMERALD, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "gemEmerald"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.APATITE, 4),
				new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "gemApatite"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.LAPIS, 4),
				new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', new ItemStack(Items.dye, 1, 4)});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.ENDER, 4),
				new Object[]{" X ", "#X#", "XXX", '#', new ItemStack(Items.ender_eye, 1, 0), 'X', new ItemStack(Blocks.end_stone, 1, 0)});

		String[] dyes = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime",
				"dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};

		if (PluginManager.Module.APICULTURE.isEnabled()) {
			ItemRegistryApiculture beeItems = PluginApiculture.items;
			FluidStack liquidGlassX4 = Fluids.GLASS.getFluid(Constants.BUCKET_VOLUME * 4);
			for (int i = 0; i < 16; i++) {
				RecipeManagers.fabricatorManager.addRecipe(beeItems.waxCast.getWildcard(), liquidGlassX4, new ItemStack(Blocks.stained_glass, 4, 15 - i), new Object[]{
						"#", "X",
						'#', dyes[i],
						'X', beeItems.propolis.getWildcard()});
			}
			RecipeManagers.fabricatorManager.addRecipe(beeItems.waxCast.getWildcard(), liquidGlassX4, new ItemStack(Blocks.glass, 4, 0), new Object[]{
					"#", "X",
					'X', beeItems.propolis.getWildcard()});
		}

		// / SQUEEZER
		int appleMulchAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
		int appleJuiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.apple)}, Fluids.JUICE.getFluid(appleJuiceAmount),
				PluginCore.items.mulch.getItemStack(), appleMulchAmount);

		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.carrot)}, Fluids.JUICE.getFluid(appleJuiceAmount),
				PluginCore.items.mulch.getItemStack(), appleMulchAmount);
		int seedOilAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedOil = Fluids.SEEDOIL.getFluid(seedOilAmount);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.wheat_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.pumpkin_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.melon_seeds)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{PluginCore.items.phosphor.getItemStack(2), new ItemStack(Blocks.cobblestone)}, Fluids.LAVA.getFluid(1600));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Blocks.cactus)}, Fluids.WATER.getFluid(500));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.snowball), PluginCore.items.craftingMaterial.getIceShard(4)}, Fluids.ICE.getFluid(4000));

		// STILL
		RecipeManagers.stillManager.addRecipe(Constants.STILL_DESTILLATION_DURATION, Fluids.BIOMASS.getFluid(Constants.STILL_DESTILLATION_INPUT),
				Fluids.ETHANOL.getFluid(Constants.STILL_DESTILLATION_OUTPUT));

		// convert old honey to new honey
		if (FluidRegistry.isFluidRegistered(Fluids.LEGACY_HONEY.name())) {
			RecipeManagers.stillManager.addRecipe(1, Fluids.LEGACY_HONEY.getFluid(1000), Fluids.HONEY.getFluid(1000));
		}

		// MOISTENER
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Items.wheat_seeds), new ItemStack(Blocks.mycelium), 5000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.cobblestone), new ItemStack(Blocks.mossy_cobblestone), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.stonebrick), new ItemStack(Blocks.stonebrick, 1, 1), 20000);

		// FERMENTER
		for (int i = 0; i < 6; i++) {
			RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.sapling, 1, i), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		}

		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.cactus), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.cactus"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Items.wheat), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Items.potato), (2 * ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat")), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Items.reeds), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.cane"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.brown_mushroom), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.red_mushroom), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.mushroom"), Fluids.BIOMASS);

		// FABRICATOR

		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.glass), Fluids.GLASS.getFluid(1000), 1000);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.glass_pane), Fluids.GLASS.getFluid(375), 1000);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.sand), Fluids.GLASS.getFluid(1000), 3000);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.sand,1,1), Fluids.GLASS.getFluid(1000), 3000);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.sandstone), Fluids.GLASS.getFluid(4000), 4800);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.sandstone,1,1), Fluids.GLASS.getFluid(4000), 4800);
		RecipeManagers.fabricatorSmeltingManager.addSmelting(new ItemStack(Blocks.sandstone,1,2), Fluids.GLASS.getFluid(4000), 4800);

		// / CARPENTER
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEEDOIL.getFluid(250), null, PluginCore.items.impregnatedCasing.getItemStack(),
				"###",
				"# #",
				"###",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEEDOIL.getFluid(500), null,
				PluginCore.blocks.core.get(BlockCoreType.ESCRITOIRE),
				"#  ",
				"###",
				"# #",
				'#', "plankWood");

		// RESOURCES
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEEDOIL.getFluid(100), null,
				PluginCore.items.stickImpregnated.getItemStack(2),
				"#",
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(250), null,
				PluginCore.items.woodPulp.getItemStack(4),
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(250), null,
				new ItemStack(Items.paper, 1),
				"#",
				"#",
				'#', "pulpWood");
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null,
				PluginCore.blocks.soil.get(BlockSoil.SoilType.HUMUS, 9),
				"###",
				"#X#",
				"###",
				'#', Blocks.dirt,
				'X', PluginCore.items.mulch);
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null,
				PluginCore.blocks.soil.get(BlockSoil.SoilType.BOG_EARTH, 8),
				"#X#",
				"XYX", "#X#",
				'#', Blocks.dirt,
				'X', "sand",
				'Y', PluginCore.items.mulch);
		RecipeManagers.carpenterManager.addRecipe(75, Fluids.WATER.getFluid(5000), null, PluginCore.items.hardenedCasing.getItemStack(),
				"# #",
				" Y ",
				"# #",
				'#', "gemDiamond",
				'Y', PluginCore.items.sturdyCasing);

		// / CHIPSETS
		ItemCircuitBoard circuitBoard = PluginCore.items.circuitboards;
		RecipeManagers.carpenterManager.addRecipe(20, Fluids.WATER.getFluid(1000), null, circuitBoard.get(EnumCircuitBoardType.BASIC),
				"R R", "R#R", "R R", '#', "ingotTin", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(40, Fluids.WATER.getFluid(1000), null, circuitBoard.get(EnumCircuitBoardType.ENHANCED),
				"R#R", "R#R", "R#R", '#', "ingotBronze", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(80, Fluids.WATER.getFluid(1000), null, circuitBoard.get(EnumCircuitBoardType.REFINED),
				"R#R", "R#R", "R#R", '#', "ingotIron", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(80, Fluids.WATER.getFluid(1000), null, circuitBoard.get(EnumCircuitBoardType.INTRICATE),
				"R#R", "R#R", "R#R", '#', "ingotGold", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(40, Fluids.WATER.getFluid(1000), null, PluginCore.items.solderingIron.getItemStack(),
				" # ", "# #", "  B", '#', "ingotIron", 'B', "ingotBronze");

		// RAIN SUBSTRATES
		ItemRegistryApiculture beeItems = PluginApiculture.items;
		if (beeItems != null) {
			RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null, PluginCore.items.iodineCharge.getItemStack(),
					"Z#Z",
					"#Y#",
					"X#X",
					'#', beeItems.pollenCluster.getWildcard(),
					'X', Items.gunpowder,
					'Y', PluginFluids.items.canEmpty,
					'Z', beeItems.honeyDrop);
			RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null, PluginCore.items.craftingMaterial.getDissipationCharge(),
					"Z#Z",
					"#Y#",
					"X#X",
					'#', beeItems.royalJelly,
					'X', Items.gunpowder,
					'Y', PluginFluids.items.canEmpty,
					'Z', beeItems.honeydew);
		}

		// Ender pearl
		RecipeManagers.carpenterManager.addRecipe(100, null, new ItemStack(Items.ender_pearl, 1), " # ", "###", " # ", '#',
				PluginCore.items.craftingMaterial.getPulsatingMesh());
		// Woven Silk
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.WATER.getFluid(500), null, PluginCore.items.craftingMaterial.getWovenSilk(),
				"###", "###", "###", '#', PluginCore.items.craftingMaterial.getSilkWisp());

		// Boxes
		RecipeManagers.carpenterManager.addRecipe(5, Fluids.WATER.getFluid(1000), null, PluginCore.items.carton.getItemStack(2),
				" # ", "# #", " # ", '#', "pulpWood");

		// Assembly Kits
		RecipeManagers.carpenterManager.addRecipe(20, null, PluginCore.items.carton.getItemStack(), PluginCore.items.kitPickaxe.getItemStack(), new Object[]{
				"###",
				" X ",
				" X ",
				'#', "ingotBronze",
				'X', "stickWood"});

		RecipeManagers.carpenterManager.addRecipe(20, null, PluginCore.items.carton.getItemStack(), PluginCore.items.kitShovel.getItemStack(),
				new Object[]{" # ", " X ", " X ", '#', "ingotBronze", 'X', "stickWood"});

		// Reclamation
		RecipeManagers.carpenterManager.addRecipe(null, PluginCore.items.ingotBronze.getItemStack(2),
				"#", '#', PluginCore.items.brokenBronzePickaxe);
		RecipeManagers.carpenterManager.addRecipe(null, PluginCore.items.ingotBronze.getItemStack(), "#", '#', PluginCore.items.brokenBronzeShovel);

		// Crating and uncrating
		if (PluginManager.Module.STORAGE.isEnabled()) {
			PluginStorage.createCrateRecipes();
		}
		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.machine.upgrade");

		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.EMERALD, 1), Circuit.machineSpeedUpgrade1);
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.BLAZE, 1), Circuit.machineSpeedUpgrade2);
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1), Circuit.machineEfficiencyUpgrade1);

		RecipeUtil.addRecipe(blocks.factoryTESR.get(BlockFactoryTesrType.BOTTLER),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', PluginFluids.items.canEmpty,
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.factoryTESR.get(BlockFactoryTesrType.CARPENTER),
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotBronze",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.factoryTESR.get(BlockFactoryTesrType.CENTRIFUGE),
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotCopper",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.factoryTESR.get(BlockFactoryTesrType.FERMENTER),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearBronze",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.factoryTESR.get(BlockFactoryTesrType.MOISTENER),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearCopper",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.factoryTESR.get(BlockFactoryTesrType.SQUEEZER),
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotTin",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.factoryTESR.get(BlockFactoryTesrType.STILL),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "dustRedstone",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.factoryTESR.get(BlockFactoryTesrType.RAINMAKER),
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearTin",
				'Y', PluginCore.items.hardenedCasing);

		RecipeUtil.addRecipe(blocks.factoryPlain.get(BlockFactoryPlainType.FABRICATOR),
				"X#X",
				"#Y#",
				"XZX",
				'#', "blockGlass",
				'X', "ingotGold",
				'Y', PluginCore.items.sturdyCasing,
				'Z', "chestWood");

		RecipeUtil.addRecipe(blocks.factoryPlain.get(BlockFactoryPlainType.RAINTANK),
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotIron",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.factoryPlain.get(BlockFactoryPlainType.WORKTABLE),
				"B",
				"W",
				"C",
				'B', Items.book,
				'W', "craftingTableWood",
				'C', "chestWood");
	}
}
