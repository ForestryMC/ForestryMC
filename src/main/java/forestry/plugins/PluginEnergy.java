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

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.GeneratorFuel;
import forestry.core.GameMode;
import forestry.core.circuits.Circuit;
import forestry.core.circuits.CircuitId;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.gadgets.BlockBase;
import forestry.core.gadgets.MachineDefinition;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.items.ItemForestryBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.ShapedRecipeCustom;
import forestry.energy.GuiHandlerEnergy;
import forestry.energy.circuits.CircuitElectricBoost;
import forestry.energy.circuits.CircuitElectricChoke;
import forestry.energy.circuits.CircuitElectricEfficiency;
import forestry.energy.circuits.CircuitFireDampener;
import forestry.energy.gadgets.EngineBronze;
import forestry.energy.gadgets.EngineClockwork;
import forestry.energy.gadgets.EngineCopper;
import forestry.energy.gadgets.EngineDefinition;
import forestry.energy.gadgets.EngineTin;
import forestry.energy.gadgets.MachineGenerator;
import forestry.energy.proxy.ProxyEnergy;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidStack;

@Plugin(pluginID = "Energy", name = "Energy", author = "SirSengir", url = Defaults.URL, description = "Adds several engines compatible with BuildCraft 3 as well as a generator for IC2.")
public class PluginEnergy extends ForestryPlugin {

	@SidedProxy(clientSide = "forestry.energy.proxy.ClientProxyEnergy", serverSide = "forestry.energy.proxy.ProxyEnergy")
	public static ProxyEnergy proxy;
	public static MachineDefinition definitionEngineCopper;
	public static MachineDefinition definitionEngineBronze;
	public static MachineDefinition definitionEngineClockwork;

	@Override
	public void preInit() {
		super.preInit();

		ForestryBlock.engine.registerBlock(new BlockBase(Material.iron, true), ItemForestryBlock.class, "engine");

		definitionEngineCopper = ((BlockBase) ForestryBlock.engine.block()).addDefinition(new EngineDefinition(Defaults.DEFINITION_ENGINECOPPER_META, "forestry.EngineCopper", EngineCopper.class,
				PluginEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_copper_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.engine.getItemStack(1, Defaults.DEFINITION_ENGINECOPPER_META),
						"###",
						" X ",
						"YVY",
						'#', "ingotCopper",
						'X', Blocks.glass,
						'Y', "gearCopper",
						'V', Blocks.piston)));
		definitionEngineBronze = ((BlockBase) ForestryBlock.engine.block()).addDefinition(new EngineDefinition(Defaults.DEFINITION_ENGINEBRONZE_META, "forestry.EngineBronze", EngineBronze.class,
				PluginEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_bronze_"), ShapedRecipeCustom.createShapedRecipe(
						ForestryBlock.engine.getItemStack(1, Defaults.DEFINITION_ENGINEBRONZE_META),
						"###",
						" X ",
						"YVY",
						'#', "ingotBronze",
						'X', Blocks.glass,
						'Y', "gearBronze",
						'V', Blocks.piston)));

		ShapedRecipeCustom clockworkRecipe = null;
		if (GameMode.getGameMode().getBooleanSetting("energy.engine.clockwork"))
			clockworkRecipe = ShapedRecipeCustom.createShapedRecipe(
					ForestryBlock.engine.getItemStack(1, Defaults.DEFINITION_ENGINECLOCKWORK_META),
					"###",
					" X ",
					"ZVY",
					'#', "plankWood",
					'X', Blocks.glass,
					'Y', Items.clock,
					'Z', ForestryItem.gearCopper,
					'V', Blocks.piston);

		definitionEngineClockwork = ((BlockBase) ForestryBlock.engine.block()).addDefinition(new EngineDefinition(Defaults.DEFINITION_ENGINECLOCKWORK_META, "forestry.EngineClockwork", EngineClockwork.class,
				PluginEnergy.proxy.getRenderDefaultEngine(Defaults.TEXTURE_PATH_BLOCKS + "/engine_clock_"), clockworkRecipe));

		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.ELECTRIC_CHOKE_I, "forestry.energyChoke1");
		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.FIRE_DAMPENER_I, "forestry.energyDampener1");
		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.ELECTRIC_EFFICIENCY_I, "forestry.energyEfficiency1");
		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.ELECTRIC_BOOST_I, "forestry.energyBoost1");
		ChipsetManager.circuitRegistry.registerLegacyMapping(CircuitId.ELECTRIC_BOOST_II, "forestry.energyBoost2");
	}

	@Override
	public void doInit() {
		super.doInit();

		definitionEngineCopper.register();
		definitionEngineBronze.register();
		definitionEngineClockwork.register();

		FluidStack ethanol = LiquidHelper.getLiquid(Defaults.LIQUID_ETHANOL, 1);
		GeneratorFuel ethanolFuel = new GeneratorFuel(ethanol, (int) (32 * GameMode.getGameMode().getFloatSetting("fuel.ethanol.generator")), 4);
		FuelManager.generatorFuel.put(ethanol.getFluid(), ethanolFuel);

		FluidStack biomass = LiquidHelper.getLiquid(Defaults.LIQUID_BIOMASS, 1);
		GeneratorFuel biomassFuel = new GeneratorFuel(biomass, (int) (8 * GameMode.getGameMode().getFloatSetting("fuel.biomass.generator")), 1);
		FuelManager.generatorFuel.put(biomass.getFluid(), biomassFuel);

		Circuit.energyElectricChoke1 = new CircuitElectricChoke("electric.choke.1");
		Circuit.energyFireDampener1 = new CircuitFireDampener("dampener.1");
		Circuit.energyElectricEfficiency1 = new CircuitElectricEfficiency("electric.efficiency.1");
		Circuit.energyElectricBoost1 = new CircuitElectricBoost("electric.boost.1", 2, 7, 2);
		Circuit.energyElectricBoost2 = new CircuitElectricBoost("electric.boost.2", 2, 15, 4);
	}

	@Override
	protected void registerItems() {
	}

	@Override
	protected void registerBackpackItems() {
	}

	@Override
	protected void registerRecipes() {

		ICircuitLayout layout = ChipsetManager.circuitRegistry.getLayout("forestry.engine.tin");

		// / Solder Manager
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 0), Circuit.energyElectricChoke1);
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 1), Circuit.energyElectricBoost1);
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 2), Circuit.energyElectricBoost2);
		ChipsetManager.solderManager.addRecipe(layout, ForestryItem.tubes.getItemStack(1, 3), Circuit.energyElectricEfficiency1);
	}

	@Override
	protected void registerCrates() {
	}

	@Override
	public IGuiHandler getGuiHandler() {
		return new GuiHandlerEnergy();
	}

	@Override
	public ISaveEventHandler getSaveEventHandler() {
		return null;
	}

	@Override
	public IOreDictionaryHandler getDictionaryHandler() {
		return null;
	}
}
