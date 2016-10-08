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

import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

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
import forestry.api.recipes.RecipeManagers;
import forestry.apiculture.PluginApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.PluginCore;
import forestry.core.PluginFluids;
import forestry.core.blocks.BlockBogEarth;
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
import forestry.factory.triggers.FactoryTriggers;
import forestry.plugins.BlankForestryPlugin;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.ForestryPluginUids;
import forestry.storage.PluginStorage;

@ForestryPlugin(pluginID = ForestryPluginUids.FACTORY, name = "Factory", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.plugin.factory.description")
public class PluginFactory extends BlankForestryPlugin {
	public static BlockRegistryFactory blocks;

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

		FuelManager.bronzeEngineFuel.put(Fluids.BIOMASS.getFluid(), new EngineBronzeFuel(Fluids.BIOMASS.getFluid(),
				Constants.ENGINE_FUEL_VALUE_BIOMASS, (int) (Constants.ENGINE_CYCLE_DURATION_BIOMASS * ForestryAPI.activeMode.getFloatSetting("fuel.biomass.biogas")), 1));
		FuelManager.bronzeEngineFuel.put(FluidRegistry.WATER, new EngineBronzeFuel(FluidRegistry.WATER,
				Constants.ENGINE_FUEL_VALUE_WATER, Constants.ENGINE_CYCLE_DURATION_WATER, 3));
		FuelManager.bronzeEngineFuel.put(Fluids.MILK.getFluid(), new EngineBronzeFuel(Fluids.MILK.getFluid(),
				Constants.ENGINE_FUEL_VALUE_MILK, Constants.ENGINE_CYCLE_DURATION_MILK, 3));
		FuelManager.bronzeEngineFuel.put(Fluids.SEED_OIL.getFluid(), new EngineBronzeFuel(Fluids.SEED_OIL.getFluid(),
				Constants.ENGINE_FUEL_VALUE_SEED_OIL, Constants.ENGINE_CYCLE_DURATION_SEED_OIL, 1));
		FuelManager.bronzeEngineFuel.put(Fluids.FOR_HONEY.getFluid(), new EngineBronzeFuel(Fluids.FOR_HONEY.getFluid(),
				Constants.ENGINE_FUEL_VALUE_HONEY, Constants.ENGINE_CYCLE_DURATION_HONEY, 1));
		FuelManager.bronzeEngineFuel.put(Fluids.JUICE.getFluid(), new EngineBronzeFuel(Fluids.JUICE.getFluid(),
				Constants.ENGINE_FUEL_VALUE_JUICE, Constants.ENGINE_CYCLE_DURATION_JUICE, 1));

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
		super.addLootPoolNames(lootPoolNames);
		lootPoolNames.add("forestry_factory_items");
	}

	@Override
	public void registerTriggers() {
		FactoryTriggers.initialize();
	}

	@Override
	public void doInit() {
		super.doInit();

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
		blocks.worktable.init();

		Circuit.machineSpeedUpgrade1 = new CircuitSpeedUpgrade("machine.speed.boost.1", 0.125f, 0.05f);
		Circuit.machineSpeedUpgrade2 = new CircuitSpeedUpgrade("machine.speed.boost.2", 0.250f, 0.10f);
		Circuit.machineEfficiencyUpgrade1 = new CircuitSpeedUpgrade("machine.efficiency.1", 0, -0.10f);
	}

	@Override
	public void registerRecipes() {

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
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', Blocks.OBSIDIAN});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.BLAZE, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', Items.BLAZE_POWDER});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.EMERALD, 4), new Object[]{
				" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "gemEmerald"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.APATITE, 4),
				new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', "gemApatite"});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.LAPIS, 4),
				new Object[]{" X ", "#X#", "XXX", '#', "dustRedstone", 'X', new ItemStack(Items.DYE, 1, 4)});
		RecipeManagers.fabricatorManager.addRecipe(null, Fluids.GLASS.getFluid(500), electronTube.get(EnumElectronTube.ENDER, 4),
				new Object[]{" X ", "#X#", "XXX", '#', new ItemStack(Items.ENDER_EYE, 1, 0), 'X', new ItemStack(Blocks.END_STONE, 1, 0)});

		String[] dyes = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime",
				"dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};

		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = PluginApiculture.items;
			FluidStack liquidGlass = Fluids.GLASS.getFluid(Fluid.BUCKET_VOLUME);
			FluidStack liquidGlassX4 = Fluids.GLASS.getFluid(Fluid.BUCKET_VOLUME * 4);
			for (int i = 0; i < 16; i++) {
				RecipeManagers.fabricatorManager.addRecipe(beeItems.waxCast.getWildcard(), liquidGlass, new ItemStack(Blocks.STAINED_GLASS, 4, 15 - i), new Object[]{
						"#", "X",
						'#', dyes[i],
						'X', beeItems.propolis.getWildcard()});
			}
			RecipeManagers.fabricatorManager.addRecipe(beeItems.waxCast.getWildcard(), liquidGlassX4, new ItemStack(Blocks.GLASS, 1, 0), new Object[]{
					"#", "X",
					'X', beeItems.propolis.getWildcard()});
		}

		// / SQUEEZER
		int appleMulchAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.mulch.apple");
		int appleJuiceAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.apple");
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.APPLE)}, Fluids.JUICE.getFluid(appleJuiceAmount),
				PluginCore.items.mulch.getItemStack(), appleMulchAmount);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.CARROT)}, Fluids.JUICE.getFluid(appleJuiceAmount),
				PluginCore.items.mulch.getItemStack(), appleMulchAmount);

		int seedOilAmount = ForestryAPI.activeMode.getIntegerSetting("squeezer.liquid.seed");
		FluidStack seedOil = Fluids.SEED_OIL.getFluid(seedOilAmount);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.WHEAT_SEEDS)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.PUMPKIN_SEEDS)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.MELON_SEEDS)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.BEETROOT_SEEDS)}, seedOil);
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{PluginCore.items.phosphor.getItemStack(2), new ItemStack(Blocks.COBBLESTONE)}, new FluidStack(FluidRegistry.LAVA, 1600));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Blocks.CACTUS)}, new FluidStack(FluidRegistry.WATER, 500));
		RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[]{new ItemStack(Items.SNOWBALL), PluginCore.items.craftingMaterial.getIceShard(4)}, Fluids.ICE.getFluid(4000));

		// STILL
		RecipeManagers.stillManager.addRecipe(Constants.STILL_DESTILLATION_DURATION, Fluids.BIOMASS.getFluid(Constants.STILL_DESTILLATION_INPUT),
				Fluids.BIO_ETHANOL.getFluid(Constants.STILL_DESTILLATION_OUTPUT));

		// MOISTENER
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Blocks.MYCELIUM), 5000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.MOSSY_COBBLESTONE), 20000);
		RecipeManagers.moistenerManager.addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 1), 20000);

		// FERMENTER
		for (int i = 0; i < 6; i++) {
			RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.SAPLING, 1, i), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.sapling"), Fluids.BIOMASS);
		}

		RecipeUtil.addFermenterRecipes(new ItemStack(Blocks.CACTUS), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.cactus"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Items.WHEAT), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Items.POTATO), 2 * ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.wheat"), Fluids.BIOMASS);
		RecipeUtil.addFermenterRecipes(new ItemStack(Items.REEDS), ForestryAPI.activeMode.getIntegerSetting("fermenter.yield.cane"), Fluids.BIOMASS);
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
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEED_OIL.getFluid(250), null, PluginCore.items.impregnatedCasing.getItemStack(),
				"###",
				"# #",
				"###",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(50, Fluids.SEED_OIL.getFluid(500), null,
				new ItemStack(PluginCore.blocks.escritoire),
				"#  ",
				"###",
				"# #",
				'#', "plankWood");

		// RESOURCES
		RecipeManagers.carpenterManager.addRecipe(10, Fluids.SEED_OIL.getFluid(100), null,
				PluginCore.items.stickImpregnated.getItemStack(2),
				"#",
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 250), null,
				PluginCore.items.woodPulp.getItemStack(4),
				"#",
				'#', "logWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 250), null,
				new ItemStack(Items.PAPER, 1),
				"#",
				"#",
				'#', "pulpWood");
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), null,
				new ItemStack(PluginCore.blocks.humus, 9),
				"###",
				"#X#",
				"###",
				'#', Blocks.DIRT,
				'X', PluginCore.items.mulch);
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), null,
				PluginCore.blocks.bogEarth.get(BlockBogEarth.SoilType.BOG_EARTH, 8),
				"#X#",
				"XYX", "#X#",
				'#', Blocks.DIRT,
				'X', "sand",
				'Y', PluginCore.items.mulch);
		RecipeManagers.carpenterManager.addRecipe(75, new FluidStack(FluidRegistry.WATER, 5000), null, PluginCore.items.hardenedCasing.getItemStack(),
				"# #",
				" Y ",
				"# #",
				'#', "gemDiamond",
				'Y', PluginCore.items.sturdyCasing);

		// / CHIPSETS
		ItemStack basicCircuitboard = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.BASIC, null, new ICircuit[]{});
		ItemStack enhancedCircuitboard = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.ENHANCED, null, new ICircuit[]{});
		ItemStack refinedCircuitboard = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.REFINED, null, new ICircuit[]{});
		ItemStack intricateCircuitboard = ItemCircuitBoard.createCircuitboard(EnumCircuitBoardType.INTRICATE, null, new ICircuit[]{});

		RecipeManagers.carpenterManager.addRecipe(20, new FluidStack(FluidRegistry.WATER, 1000), null, basicCircuitboard,
				"R R", "R#R", "R R", '#', "ingotTin", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(40, new FluidStack(FluidRegistry.WATER, 1000), null, enhancedCircuitboard,
				"R#R", "R#R", "R#R", '#', "ingotBronze", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(80, new FluidStack(FluidRegistry.WATER, 1000), null, refinedCircuitboard,
				"R#R", "R#R", "R#R", '#', "ingotIron", 'R', "dustRedstone");

		RecipeManagers.carpenterManager.addRecipe(80, new FluidStack(FluidRegistry.WATER, 1000), null, intricateCircuitboard,
				"R#R", "R#R", "R#R", '#', "ingotGold", 'R', "dustRedstone");
		RecipeManagers.carpenterManager.addRecipe(40, new FluidStack(FluidRegistry.WATER, 1000), null, PluginCore.items.solderingIron.getItemStack(),
				" # ", "# #", "  B", '#', "ingotIron", 'B', "ingotBronze");

		// RAIN SUBSTRATES
		ItemRegistryApiculture beeItems = PluginApiculture.items;
		if (beeItems != null) {
			RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), null, PluginCore.items.iodineCharge.getItemStack(),
					"Z#Z",
					"#Y#",
					"X#X",
					'#', beeItems.pollenCluster.getWildcard(),
					'X', Items.GUNPOWDER,
					'Y', PluginFluids.items.canEmpty,
					'Z', beeItems.honeyDrop);
			RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), null, PluginCore.items.craftingMaterial.getDissipationCharge(),
					"Z#Z",
					"#Y#",
					"X#X",
					'#', beeItems.royalJelly,
					'X', Items.GUNPOWDER,
					'Y', PluginFluids.items.canEmpty,
					'Z', beeItems.honeydew);
		}

		// Ender pearl
		RecipeManagers.carpenterManager.addRecipe(100, null, new ItemStack(Items.ENDER_PEARL, 1), " # ", "###", " # ", '#',
				PluginCore.items.craftingMaterial.getPulsatingMesh());

		// Woven Silk
		RecipeManagers.carpenterManager.addRecipe(10, new FluidStack(FluidRegistry.WATER, 500), null, PluginCore.items.craftingMaterial.getWovenSilk(),
				"###",
				"###",
				"###",
				'#', PluginCore.items.craftingMaterial.getSilkWisp());

		// Boxes
		RecipeManagers.carpenterManager.addRecipe(5, new FluidStack(FluidRegistry.WATER, 1000), null, PluginCore.items.carton.getItemStack(2),
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
		ItemStack ingotBronze = PluginCore.items.ingotBronze.copy();
		ingotBronze.stackSize = 2;
		RecipeManagers.carpenterManager.addRecipe(null, ingotBronze, "#", '#', PluginCore.items.brokenBronzePickaxe);

		ingotBronze = ingotBronze.copy();
		ingotBronze.stackSize = 1;
		RecipeManagers.carpenterManager.addRecipe(null, ingotBronze, "#", '#', PluginCore.items.brokenBronzeShovel);

		// Crating and uncrating
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.STORAGE)) {
			PluginStorage.createCrateRecipes();
		}
		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.machine.upgrade");

		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.EMERALD, 1), Circuit.machineSpeedUpgrade1);
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.BLAZE, 1), Circuit.machineSpeedUpgrade2);
		ChipsetManager.solderManager.addRecipe(layout, PluginCore.items.tubes.get(EnumElectronTube.GOLD, 1), Circuit.machineEfficiencyUpgrade1);

		RecipeUtil.addRecipe(blocks.bottler,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', PluginFluids.items.canEmpty,
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.carpenter,
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotBronze",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.centrifuge,
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotCopper",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.fermenter,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearBronze",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.moistener,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearCopper",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.squeezer,
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotTin",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.still,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "dustRedstone",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.rainmaker,
				"X#X",
				"#Y#",
				"X#X",
				'#', "blockGlass",
				'X', "gearTin",
				'Y', PluginCore.items.hardenedCasing);

		RecipeUtil.addRecipe(blocks.fabricator,
				"X#X",
				"#Y#",
				"XZX",
				'#', "blockGlass",
				'X', "ingotGold",
				'Y', PluginCore.items.sturdyCasing,
				'Z', "chestWood");

		RecipeUtil.addRecipe(blocks.raintank,
				"X#X",
				"XYX",
				"X#X",
				'#', "blockGlass",
				'X', "ingotIron",
				'Y', PluginCore.items.sturdyCasing);

		RecipeUtil.addRecipe(blocks.worktable,
				"B",
				"W",
				"C",
				'B', Items.BOOK,
				'W', "craftingTableWood",
				'C', "chestWood");
	}
}
