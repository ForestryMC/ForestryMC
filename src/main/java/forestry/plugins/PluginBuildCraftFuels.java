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

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.ICoolant;
import buildcraft.api.fuels.ICoolantManager;
import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.BUILDCRAFT_FUELS, name = "BuildCraft 6 Fuels", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.module.buildcraft6.description")
public class PluginBuildCraftFuels extends BlankForestryModule {

	@Override
	public boolean isAvailable() {
		return ModUtil.isAPILoaded("buildcraft.api.fuels", "[2.0, 3.0)");
	}

	@Override
	public String getFailMessage() {
		return "Compatible BuildCraftAPI|fuels version not found";
	}

	@Optional.Method(modid = "BuildCraftAPI|fuels")
	@Override
	public void doInit() {
		ICoolantManager coolantManager = BuildcraftFuelRegistry.coolant;
		FluidStack water = new FluidStack(FluidRegistry.WATER, 1);
		ICoolant waterCoolant = coolantManager.getCoolant(water);
		float waterCooling = waterCoolant.getDegreesCoolingPerMB(water, 100);

		coolantManager.addCoolant(Fluids.ICE.getFluid(), Constants.ICE_COOLING_MULTIPLIER * waterCooling);

		Fluid ethanol = Fluids.BIO_ETHANOL.getFluid();
		if (ethanol != null) {
			int ethanolPower = 40;
			int ethanolBurnTime = Math.round(Constants.ENGINE_CYCLE_DURATION_ETHANOL * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.combustion"));
			BuildcraftFuelRegistry.fuel.addFuel(ethanol, ethanolPower, ethanolBurnTime);
		}
	}

}
