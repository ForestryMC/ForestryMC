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

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.api.fuels.IronEngineCoolant;
import buildcraft.api.fuels.IronEngineFuel;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.recipes.BuildcraftRecipes;
import buildcraft.api.transport.IPipeTile;

import forestry.api.core.IPlugin;
import forestry.api.core.PluginInfo;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.gadgets.TileForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;

@PluginInfo(pluginID = "BC6", name = "BuildCraft 6", author = "SirSengir", url = Defaults.URL, description = "Compatibility plugin for BuildCraft 6.")
public class PluginBuildCraft implements IPlugin, ITriggerProvider {

	public static PluginBuildCraft instance;
	public static Configuration config;
	// Ignore Buildcraft?
	public static boolean ignore;
	public static Item wrench;
	public static Item stoneGear;
	public static Item pipeWaterproof;
	public static final String validVersionRange = "[6.0.0,6.1.0)";

	public PluginBuildCraft() {
		if (PluginBuildCraft.instance == null)
			PluginBuildCraft.instance = this;
	}

	/**
	 * @return true if BuildCraftCore is installed.
	 */
	@Override
	public boolean isAvailable() {
		return (Proxies.common.isModLoaded("BuildCraft|Core", validVersionRange) && Proxies.common.isModLoaded("BuildCraft|Transport", validVersionRange));
	}

	@Override
	public void doInit() {
		config = Config.config;

		Property buildcraftignore = config.get("buildcraft.ignore", Config.CATEGORY_COMMON, false);
		buildcraftignore.Comment = "set to true to ignore buildcraft";
		PluginBuildCraft.ignore = Boolean.parseBoolean(buildcraftignore.Value);

		IronEngineCoolant.addCoolant(LiquidHelper.getLiquid(Defaults.LIQUID_ICE, 1).getFluid(), 10.0f);

		addIronEngineFuel(LiquidHelper.getLiquid(Defaults.LIQUID_ETHANOL, 1).getFluid(), 4,
				Defaults.ENGINE_CYCLE_DURATION_ETHANOL * GameMode.getGameMode().getFloatSetting("fuel.ethanol.combustion"));

		// Add recipe for ethanol
		addRefineryRecipe(LiquidHelper.getLiquid(Defaults.LIQUID_BIOMASS, 4), null, LiquidHelper.getLiquid(Defaults.LIQUID_ETHANOL, 1), 10, 1);

		// Add custom trigger handler
		ActionManager.registerTriggerProvider(this);

		initStoneGear();
		initWaterproof();
		initLiquids();
	}

	@Override
	public void postInit() {
	}

	private void initLiquids() {
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_OIL, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleOil.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_FUEL, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleFuel.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_OIL, Defaults.BUCKET_VOLUME, ForestryItem.refractoryOil.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_FUEL, Defaults.BUCKET_VOLUME, ForestryItem.refractoryFuel.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());
		LiquidHelper.injectTinContainer(Defaults.LIQUID_OIL, Defaults.BUCKET_VOLUME, ForestryItem.canOil.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectTinContainer(Defaults.LIQUID_FUEL, Defaults.BUCKET_VOLUME, ForestryItem.canFuel.getItemStack(), ForestryItem.canEmpty.getItemStack());

	}

	private void addIronEngineFuel(Fluid fuel, float powerPerCycle, float totalBurningTime) {
		IronEngineFuel.addFuel(fuel, (int) powerPerCycle, (int) totalBurningTime);
	}

	private void addRefineryRecipe(FluidStack ingredient1, FluidStack ingredient2, FluidStack result, int energy, int delay) {
		BuildcraftRecipes.refinery.addRecipe(ingredient1, ingredient2, result, energy, delay);

	}

	private void initStoneGear() {
		try {
			stoneGear = (Item) Class.forName("buildcraft.BuildCraftCore").getField("stoneGearItem").get(null);
		} catch (Exception ex) {
			Proxies.log.fine("No BuildCraft stone gear found.");
			return;
		}
	}

	private void initWaterproof() {
		try {
			pipeWaterproof = (Item) Class.forName("buildcraft.BuildCraftTransport").getField("pipeWaterproof").get(null);
		} catch (Exception ex) {
			Proxies.log.fine("No BuildCraft pipe waterproof found.");
			return;
		}

		Proxies.common.addRecipe(new ItemStack(pipeWaterproof), "#", '#', ForestryItem.beeswax);
	}

	public double invokeUseEnergyMethod(PowerHandler workProvider, float min, float max, boolean doUse) {
		return workProvider.useEnergy(min, max, doUse);
	}

	public void invokeReceiveEnergyMethod(PowerHandler.Type type, PowerReceiver receiver, double extractedEnergy, ForgeDirection from) {
		receiver.receiveEnergy(type, extractedEnergy, from);
	}

	// / ITRIGGERPROVIDER
	@Override
	public LinkedList<ITrigger> getPipeTriggers(IPipeTile pipe) {
		return null;
	}

	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
		if (tile instanceof TileForestry)
			return ((TileForestry) tile).getCustomTriggers();

		return null;
	}

	@Override
	public void preInit() {
	}
}
