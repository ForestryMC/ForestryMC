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

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.recipes.BuildcraftRecipeRegistry;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.ITriggerExternal;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.api.statements.ITriggerProvider;
import buildcraft.api.statements.StatementManager;
import forestry.core.GameMode;
import forestry.core.config.Config;
import forestry.core.config.Configuration;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.config.Property;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

@Plugin(pluginID = "BC6.1", name = "BuildCraft 6.1", author = "SirSengir", url = Defaults.URL, unlocalizedDescription = "for.plugin.buildcraft6.description")
public class PluginBuildCraft extends ForestryPlugin implements ITriggerProvider {

	public static PluginBuildCraft instance;
	public static Configuration config;
	// Ignore Buildcraft?
	public static boolean ignore;
	public static Item wrench;
	public static Item stoneGear;
	public static Item pipeWaterproof;
	public static final String validVersionRange = "[6.1.0,6.2.0)";

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
	public String getFailMessage() {
		return "Compatible Buildcraft version not found";
	}

	@Override
	public void doInit() {
		config = Config.config;

		Property buildcraftignore = config.get("buildcraft.ignore", Config.CATEGORY_COMMON, false);
		buildcraftignore.Comment = "set to true to ignore buildcraft";
		PluginBuildCraft.ignore = Boolean.parseBoolean(buildcraftignore.Value);
		BuildcraftFuelRegistry.coolant.addCoolant(LiquidHelper.getFluid(Defaults.LIQUID_ICE), 10.0f);

		addIronEngineFuel(LiquidHelper.getLiquid(Defaults.LIQUID_ETHANOL, 1).getFluid(), 40,
				Defaults.ENGINE_CYCLE_DURATION_ETHANOL * GameMode.getGameMode().getFloatSetting("fuel.ethanol.combustion"));

		// Add recipe for ethanol
		addRefineryRecipe("forestry:BiomassToEthanol", LiquidHelper.getLiquid(Defaults.LIQUID_BIOMASS, 4), LiquidHelper.getLiquid(Defaults.LIQUID_ETHANOL, 1), 100, 1);

		// Add custom trigger handler
		StatementManager.registerTriggerProvider(this);

		initStoneGear();
		initWaterproof();
		initLiquids();
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
		BuildcraftFuelRegistry.fuel.addFuel(fuel, (int) powerPerCycle, (int) totalBurningTime);
	}

	private void addRefineryRecipe(String id, FluidStack ingredient1, FluidStack result, int energy, int delay) {
		BuildcraftRecipeRegistry.refinery.addRecipe(id, ingredient1, result, energy, delay);
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

	// / ITRIGGERPROVIDER
	@Override
	public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
		TileEntity tile = container.getTile();
		if (tile instanceof ITriggerProvider)
			return ((ITriggerProvider) tile).getInternalTriggers(container);

		return null;
	}

	@Override
	public Collection<ITriggerExternal> getExternalTriggers(ForgeDirection side, TileEntity tile) {
		if (tile instanceof ITriggerProvider)
			return ((ITriggerProvider) tile).getExternalTriggers(side, tile);

		return null;
	}
}
