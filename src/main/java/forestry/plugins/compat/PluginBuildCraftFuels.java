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
package forestry.plugins.compat;

import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.Optional;

import forestry.api.core.ForestryAPI;
import forestry.core.config.Constants;
import forestry.core.fluids.Fluids;
import forestry.core.utils.ModUtil;
import forestry.plugins.ForestryPlugin;
import forestry.plugins.Plugin;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.ICoolant;
import buildcraft.api.fuels.ICoolantManager;

@Plugin(pluginID = "BC6|Fuels", name = "BuildCraft 6 Fuels", author = "mezz", url = Constants.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
public class PluginBuildCraftFuels extends ForestryPlugin {

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
		ICoolant waterCoolant = coolantManager.getCoolant(Fluids.WATER.getFluid());
		float waterCooling = waterCoolant.getDegreesCoolingPerMB(100);

		coolantManager.addCoolant(Fluids.ICE.getFluid(), Constants.ICE_COOLING_MULTIPLIER * waterCooling);

		Fluid ethanol = Fluids.ETHANOL.getFluid();
		if (ethanol != null) {
			int ethanolPower = 40;
			int ethanolBurnTime = Math.round(Constants.ENGINE_CYCLE_DURATION_ETHANOL * ForestryAPI.activeMode.getFloatSetting("fuel.ethanol.combustion"));
			BuildcraftFuelRegistry.fuel.addFuel(ethanol, ethanolPower, ethanolBurnTime);
		}
	}

}
