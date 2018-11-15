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

import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.ICoolant;
import buildcraft.api.fuels.ICoolantManager;
import buildcraft.api.mj.MjAPI;

@ForestryModule(containerID = ForestryCompatPlugins.ID, moduleID = ForestryModuleUids.BUILDCRAFT_FUELS, name = "BuildCraft 6 Fuels", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.module.buildcraft6.description")
public class PluginBuildCraftFuels extends BlankForestryModule {

	public static final String MOD_ID = "buildcraftenergy";

	@Override
	public boolean isAvailable() {
		return ModUtil.isModLoaded(Constants.BCLIB_MOD_ID, "[7.99.17,8.0)");
	}

	@Override
	public String getFailMessage() {
		return "Compatible BuildCraftAPI|fuels version not found";
	}

	@Override
	public void doInit() {
		ICoolantManager coolantManager = BuildcraftFuelRegistry.coolant;
		if (coolantManager != null) {
			FluidStack water = new FluidStack(FluidRegistry.WATER, 1);
			ICoolant waterCoolant = coolantManager.getCoolant(water);
			Fluid ice = Fluids.ICE.getFluid();
			if (waterCoolant != null && ice != null) {
				float waterCooling = waterCoolant.getDegreesCoolingPerMB(water, 100);

				coolantManager.addCoolant(ice, Constants.ICE_COOLING_MULTIPLIER * waterCooling);
			}
		}
		Fluid ethanol = Fluids.BIO_ETHANOL.getFluid();
		if (ethanol != null) {
			long ethanolPower = 4 * MjAPI.MJ;
			int ethanolBurnTime = Math.round(Constants.ENGINE_CYCLE_DURATION_ETHANOL * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.combustion"));
			BuildcraftFuelRegistry.fuel.addFuel(ethanol, ethanolPower, ethanolBurnTime);
		}
	}

}
