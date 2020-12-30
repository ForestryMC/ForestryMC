/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.factory;

import com.google.common.collect.Maps;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.core.ForestryAPI;
import forestry.api.fuels.*;
import forestry.api.modules.ForestryModule;
import forestry.api.recipes.RecipeManagers;
import forestry.core.circuits.CircuitLayout;
import forestry.core.circuits.Circuits;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.config.Constants;
import forestry.core.config.LocalizedConfiguration;
import forestry.core.features.CoreItems;
import forestry.core.fluids.ForestryFluids;
import forestry.core.items.EnumCraftingMaterial;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.ForgeUtils;
import forestry.core.utils.datastructures.DummyMap;
import forestry.core.utils.datastructures.FluidMap;
import forestry.core.utils.datastructures.ItemStackMap;
import forestry.factory.circuits.CircuitSpeedUpgrade;
import forestry.factory.features.FactoryContainers;
import forestry.factory.gui.*;
import forestry.factory.network.PacketRegistryFactory;
import forestry.factory.recipes.*;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.storage.ModuleCrates;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.FACTORY, name = "Factory", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.factory.description", lootTable = "factory")
public class ModuleFactory extends BlankForestryModule {

    public static final Map<String, Boolean> MACHINE_ENABLED = Maps.newHashMap();

    public ModuleFactory() {
        ForgeUtils.registerSubscriber(this);
    }

    @Override
    public void setupAPI() {
        RecipeManagers.carpenterManager = machineEnabled(MachineUIDs.CARPENTER) ? new CarpenterRecipeManager()
                                                                                : new DummyManagers.DummyCarpenterManager();
        RecipeManagers.centrifugeManager = machineEnabled(MachineUIDs.CENTRIFUGE) ? new CentrifugeRecipeManager()
                                                                                  : new DummyManagers.DummyCentrifugeManager();
        RecipeManagers.fabricatorManager = machineEnabled(MachineUIDs.FABRICATOR) ? new FabricatorRecipeManager()
                                                                                  : new DummyManagers.DummyFabricatorManager();
        RecipeManagers.fabricatorSmeltingManager =
                machineEnabled(MachineUIDs.FABRICATOR) ? new FabricatorSmeltingRecipeManager()
                                                       : new DummyManagers.DummyFabricatorSmeltingManager();
        RecipeManagers.fermenterManager = machineEnabled(MachineUIDs.FERMENTER) ? new FermenterRecipeManager()
                                                                                : new DummyManagers.DummyFermenterManager();
        RecipeManagers.moistenerManager = machineEnabled(MachineUIDs.MOISTENER) ? new MoistenerRecipeManager()
                                                                                : new DummyManagers.DummyMoistenerManager();
        RecipeManagers.squeezerManager = machineEnabled(MachineUIDs.SQUEEZER) ? new SqueezerRecipeManager()
                                                                              : new DummyManagers.DummySqueezerManager();
        RecipeManagers.stillManager =
                machineEnabled(MachineUIDs.STILL) ? new StillRecipeManager() : new DummyManagers.DummyStillManager();

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
    @OnlyIn(Dist.CLIENT)
    public void registerGuiFactories() {
        ScreenManager.registerFactory(FactoryContainers.BOTTLER.containerType(), GuiBottler::new);
        ScreenManager.registerFactory(FactoryContainers.CARPENTER.containerType(), GuiCarpenter::new);
        ScreenManager.registerFactory(FactoryContainers.CENTRIFUGE.containerType(), GuiCentrifuge::new);
        ScreenManager.registerFactory(FactoryContainers.FABRICATOR.containerType(), GuiFabricator::new);
        ScreenManager.registerFactory(FactoryContainers.FERMENTER.containerType(), GuiFermenter::new);
        ScreenManager.registerFactory(FactoryContainers.MOISTENER.containerType(), GuiMoistener::new);
        ScreenManager.registerFactory(FactoryContainers.RAINTANK.containerType(), GuiRaintank::new);
        ScreenManager.registerFactory(FactoryContainers.SQUEEZER.containerType(), GuiSqueezer::new);
        ScreenManager.registerFactory(FactoryContainers.STILL.containerType(), GuiStill::new);
    }

    @Override
    public IPacketRegistry getPacketRegistry() {
        return new PacketRegistryFactory();
    }

    @Override
    public void preInit() {
        // Set fuels and resources for the fermenter
        ItemStack fertilizerCompound = CoreItems.FERTILIZER_COMPOUND.stack();
        FuelManager.fermenterFuel.put(fertilizerCompound, new FermenterFuel(
                fertilizerCompound,
                ForestryAPI.activeMode.getIntegerSetting("fermenter.value.fertilizer"),
                ForestryAPI.activeMode.getIntegerSetting("fermenter.cycles.fertilizer")
        ));

        int cyclesCompost = ForestryAPI.activeMode.getIntegerSetting("fermenter.cycles.compost");
        int valueCompost = ForestryAPI.activeMode.getIntegerSetting("fermenter.value.compost");
        ItemStack fertilizerBio = CoreItems.COMPOST.stack();
        ItemStack mulch = CoreItems.MULCH.stack();
        FuelManager.fermenterFuel.put(fertilizerBio, new FermenterFuel(fertilizerBio, valueCompost, cyclesCompost));
        FuelManager.fermenterFuel.put(mulch, new FermenterFuel(mulch, valueCompost, cyclesCompost));

        // Add moistener resources
        ItemStack wheat = new ItemStack(Items.WHEAT);
        ItemStack mouldyWheat = CoreItems.MOULDY_WHEAT.stack();
        ItemStack decayingWheat = CoreItems.DECAYING_WHEAT.stack();
        FuelManager.moistenerResource.put(wheat, new MoistenerFuel(Ingredient.fromStacks(wheat), mouldyWheat, 0, 300));
        FuelManager.moistenerResource.put(
                mouldyWheat,
                new MoistenerFuel(Ingredient.fromStacks(mouldyWheat), decayingWheat, 1, 600)
        );
        FuelManager.moistenerResource.put(
                decayingWheat,
                new MoistenerFuel(Ingredient.fromStacks(decayingWheat), mulch, 2, 900)
        );

        // Set fuels for our own engines
        ItemStack peat = CoreItems.PEAT.stack();
        FuelManager.copperEngineFuel.put(
                peat,
                new EngineCopperFuel(
                        peat,
                        Constants.ENGINE_COPPER_FUEL_VALUE_PEAT,
                        Constants.ENGINE_COPPER_CYCLE_DURATION_PEAT
                )
        );

        ItemStack bituminousPeat = CoreItems.BITUMINOUS_PEAT.stack();
        FuelManager.copperEngineFuel.put(
                bituminousPeat,
                new EngineCopperFuel(
                        bituminousPeat,
                        Constants.ENGINE_COPPER_FUEL_VALUE_BITUMINOUS_PEAT,
                        Constants.ENGINE_COPPER_CYCLE_DURATION_BITUMINOUS_PEAT
                )
        );

        Fluid biomass = ForestryFluids.BIOMASS.getFluid();
        FuelManager.bronzeEngineFuel.put(biomass, new EngineBronzeFuel(
                biomass,
                Constants.ENGINE_FUEL_VALUE_BIOMASS,
                (int) (
                        Constants.ENGINE_CYCLE_DURATION_BIOMASS * ForestryAPI.activeMode.getFloatSetting(
                                "fuel.biomass.biogas")),
                1
        ));

        FuelManager.bronzeEngineFuel.put(Fluids.WATER, new EngineBronzeFuel(Fluids.WATER,
                Constants.ENGINE_FUEL_VALUE_WATER, Constants.ENGINE_CYCLE_DURATION_WATER, 3
        ));

        Fluid milk = ForestryFluids.MILK.getFluid();
        FuelManager.bronzeEngineFuel.put(milk, new EngineBronzeFuel(milk,
                Constants.ENGINE_FUEL_VALUE_MILK, Constants.ENGINE_CYCLE_DURATION_MILK, 3
        ));

        Fluid seedOil = ForestryFluids.SEED_OIL.getFluid();
        FuelManager.bronzeEngineFuel.put(seedOil, new EngineBronzeFuel(seedOil,
                Constants.ENGINE_FUEL_VALUE_SEED_OIL, Constants.ENGINE_CYCLE_DURATION_SEED_OIL, 1
        ));

        Fluid honey = ForestryFluids.HONEY.getFluid();
        FuelManager.bronzeEngineFuel.put(honey, new EngineBronzeFuel(honey,
                Constants.ENGINE_FUEL_VALUE_HONEY, Constants.ENGINE_CYCLE_DURATION_HONEY, 1
        ));

        Fluid juice = ForestryFluids.JUICE.getFluid();
        FuelManager.bronzeEngineFuel.put(juice, new EngineBronzeFuel(juice,
                Constants.ENGINE_FUEL_VALUE_JUICE, Constants.ENGINE_CYCLE_DURATION_JUICE, 1
        ));

        // Set rain substrates
        ItemStack iodineCharge = CoreItems.IODINE_CHARGE.stack();
        ItemStack dissipationCharge = CoreItems.CRAFTING_MATERIALS.stack(EnumCraftingMaterial.DISSIPATION_CHARGE, 1);
        FuelManager.rainSubstrate.put(
                iodineCharge,
                new RainSubstrate(iodineCharge, Constants.RAINMAKER_RAIN_DURATION_IODINE, 0.01f)
        );
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
        Circuits.machineSpeedUpgrade1 = new CircuitSpeedUpgrade("machine.speed.boost.1", 0.125f, 0.05f);
        Circuits.machineSpeedUpgrade2 = new CircuitSpeedUpgrade("machine.speed.boost.2", 0.250f, 0.10f);
        Circuits.machineEfficiencyUpgrade1 = new CircuitSpeedUpgrade("machine.efficiency.1", 0, -0.10f);
    }

    @Override
    public void registerRecipes() {
        // CHIPSETS
        ItemStack basicCircuitboard = ItemCircuitBoard.createCircuitboard(
                EnumCircuitBoardType.BASIC,
                null,
                new ICircuit[]{}
        );
        ItemStack enhancedCircuitboard = ItemCircuitBoard.createCircuitboard(
                EnumCircuitBoardType.ENHANCED,
                null,
                new ICircuit[]{}
        );
        ItemStack refinedCircuitboard = ItemCircuitBoard.createCircuitboard(
                EnumCircuitBoardType.REFINED,
                null,
                new ICircuit[]{}
        );
        ItemStack intricateCircuitboard = ItemCircuitBoard.createCircuitboard(
                EnumCircuitBoardType.INTRICATE,
                null,
                new ICircuit[]{}
        );
    }

    public static void loadMachineConfig(LocalizedConfiguration config) {
        List<String> enabled = Arrays.asList(config.getStringListLocalized(
                "machines",
                "enabled",
                MachineUIDs.ALL.toArray(new String[0]),
                MachineUIDs.ALL.toArray(new String[0])
        ));
        for (String machineID : MachineUIDs.ALL) {
            MACHINE_ENABLED.put(machineID, enabled.contains(machineID));
        }
    }

    public static boolean machineEnabled(String machineName) {
        Boolean ret = MACHINE_ENABLED.get(machineName);
        return ret != null && ret;
    }
}

