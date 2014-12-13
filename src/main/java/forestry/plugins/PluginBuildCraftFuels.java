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

import cpw.mods.fml.common.Optional;

import forestry.core.GameMode;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;

import buildcraft.api.fuels.BuildcraftFuelRegistry;

@Plugin(pluginID = "BC6|Fuels", name = "BuildCraft 6 Fuels", author = "mezz", url = Defaults.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
public class PluginBuildCraftFuels extends ForestryPlugin {

	@Override
	public boolean isAvailable() {
		return Proxies.common.isAPILoaded("buildcraft.api.fuels", "[2.0, 2.1)");
	}

	@Override
	public String getFailMessage() {
		return "Compatible BuildCraftAPI|fuels version not found";
	}

	@Optional.Method(modid = "BuildCraftAPI|fuels")
	@Override
	public void doInit() {
		BuildcraftFuelRegistry.coolant.addCoolant(FluidRegistry.getFluid(Defaults.LIQUID_ICE), 10.0f);

		Fluid ethanol = FluidRegistry.getFluidStack(Defaults.LIQUID_ETHANOL, 1).getFluid();
		int ethanolPower = 40;
		int ethanolBurnTime = Math.round(Defaults.ENGINE_CYCLE_DURATION_ETHANOL * GameMode.getGameMode().getFloatSetting("fuel.ethanol.combustion"));
		BuildcraftFuelRegistry.fuel.addFuel(ethanol, ethanolPower, ethanolBurnTime);

		LiquidHelper.injectWaxContainer(Defaults.LIQUID_OIL, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleOil.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_FUEL, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleFuel.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_OIL, Defaults.BUCKET_VOLUME, ForestryItem.refractoryOil.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_FUEL, Defaults.BUCKET_VOLUME, ForestryItem.refractoryFuel.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());
		LiquidHelper.injectTinContainer(Defaults.LIQUID_OIL, Defaults.BUCKET_VOLUME, ForestryItem.canOil.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectTinContainer(Defaults.LIQUID_FUEL, Defaults.BUCKET_VOLUME, ForestryItem.canFuel.getItemStack(), ForestryItem.canEmpty.getItemStack());
	}

}
