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
package forestry.core;

import com.google.common.collect.ImmutableList;

import java.io.File;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.EngineCopperFuel;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.fuels.RainSubstrate;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;
import forestry.core.multiblock.MultiblockEventHandler;
import forestry.core.proxy.Proxies;
import forestry.plugins.PluginManager;

public class ForestryCore {

	public void preInit(File modLocation, Object basemod) {
		ForestryAPI.instance = basemod;
		ForestryAPI.forestryConstants = new ForestryConstants();
		ForestryAPI.errorStateRegistry = new ErrorStateRegistry();

		// Register event handler
		MinecraftForge.EVENT_BUS.register(new EventHandlerCore());
		MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());

		Config.load();

		EnumErrorCode.init();

		PluginManager.runSetup();
		PluginManager.runPreInit();
	}

	public void init(Object basemod) {
		// Set fuels and resources for the fermenter
		FuelManager.fermenterFuel.put(ForestryItem.fertilizerCompound.getItemStack(), new FermenterFuel(ForestryItem.fertilizerCompound.getItemStack(),
				GameMode.getGameMode().getIntegerSetting("fermenter.value.fertilizer"), GameMode.getGameMode().getIntegerSetting("fermenter.cycles.fertilizer")));
		FuelManager.fermenterFuel.put(ForestryItem.fertilizerBio.getItemStack(), new FermenterFuel(ForestryItem.fertilizerBio.getItemStack(), GameMode
				.getGameMode().getIntegerSetting("fermenter.value.compost"), GameMode.getGameMode().getIntegerSetting("fermenter.cycles.compost")));
		FuelManager.fermenterFuel.put(ForestryItem.mulch.getItemStack(), new FermenterFuel(ForestryItem.mulch.getItemStack(), GameMode.getGameMode()
				.getIntegerSetting("fermenter.value.compost"), GameMode.getGameMode().getIntegerSetting("fermenter.cycles.compost")));

		// Add moistener resources
		FuelManager.moistenerResource.put(new ItemStack(Items.wheat), new MoistenerFuel(new ItemStack(Items.wheat), ForestryItem.mouldyWheat.getItemStack(), 0,
				300));
		FuelManager.moistenerResource.put(ForestryItem.mouldyWheat.getItemStack(), new MoistenerFuel(ForestryItem.mouldyWheat.getItemStack(), ForestryItem.decayingWheat.getItemStack(), 1, 600));
		FuelManager.moistenerResource.put(ForestryItem.decayingWheat.getItemStack(), new MoistenerFuel(ForestryItem.decayingWheat.getItemStack(),
				ForestryItem.mulch.getItemStack(), 2, 900));

		// Set fuels for our own engines
		FuelManager.copperEngineFuel.put(ForestryItem.peat.getItemStack(), new EngineCopperFuel(ForestryItem.peat.getItemStack(),
				Defaults.ENGINE_COPPER_FUEL_VALUE_PEAT, Defaults.ENGINE_COPPER_CYCLE_DURATION_PEAT));
		FuelManager.copperEngineFuel.put(ForestryItem.bituminousPeat.getItemStack(), new EngineCopperFuel(ForestryItem.bituminousPeat.getItemStack(),
				Defaults.ENGINE_COPPER_FUEL_VALUE_BITUMINOUS_PEAT, Defaults.ENGINE_COPPER_CYCLE_DURATION_BITUMINOUS_PEAT));

		FuelManager.bronzeEngineFuel.put(Fluids.BIOMASS.getFluid(), new EngineBronzeFuel(Fluids.BIOMASS.getFluid(),
				Defaults.ENGINE_FUEL_VALUE_BIOMASS, (int) (Defaults.ENGINE_CYCLE_DURATION_BIOMASS * GameMode.getGameMode().getFloatSetting("fuel.biomass.biogas")), 1));
		FuelManager.bronzeEngineFuel.put(Fluids.WATER.getFluid(), new EngineBronzeFuel(Fluids.WATER.getFluid(),
				Defaults.ENGINE_FUEL_VALUE_WATER, Defaults.ENGINE_CYCLE_DURATION_WATER, 3));
		FuelManager.bronzeEngineFuel.put(Fluids.MILK.getFluid(), new EngineBronzeFuel(Fluids.MILK.getFluid(),
				Defaults.ENGINE_FUEL_VALUE_MILK, Defaults.ENGINE_CYCLE_DURATION_MILK, 3));
		FuelManager.bronzeEngineFuel.put(Fluids.SEEDOIL.getFluid(), new EngineBronzeFuel(Fluids.SEEDOIL.getFluid(),
				Defaults.ENGINE_FUEL_VALUE_SEED_OIL, Defaults.ENGINE_CYCLE_DURATION_SEED_OIL, 1));
		FuelManager.bronzeEngineFuel.put(Fluids.HONEY.getFluid(), new EngineBronzeFuel(Fluids.HONEY.getFluid(),
				Defaults.ENGINE_FUEL_VALUE_HONEY, Defaults.ENGINE_CYCLE_DURATION_HONEY, 1));
		FuelManager.bronzeEngineFuel.put(Fluids.JUICE.getFluid(), new EngineBronzeFuel(Fluids.JUICE.getFluid(),
				Defaults.ENGINE_FUEL_VALUE_JUICE, Defaults.ENGINE_CYCLE_DURATION_JUICE, 1));

		// Set rain substrates
		FuelManager.rainSubstrate.put(ForestryItem.iodineCharge.getItemStack(), new RainSubstrate(ForestryItem.iodineCharge.getItemStack(),
				Defaults.RAINMAKER_RAIN_DURATION_IODINE, 0.01f));
		FuelManager.rainSubstrate.put(ForestryItem.craftingMaterial.getItemStack(1, 4), new RainSubstrate(ForestryItem.craftingMaterial.getItemStack(1, 4), 0.075f));

		// Register gui handler
		NetworkRegistry.INSTANCE.registerGuiHandler(basemod, new GuiHandler());

		PluginManager.runInit();
	}

	public void postInit() {
		PluginManager.runPostInit();

		// Register world generator
		WorldGenerator worldGenerator = new WorldGenerator();
		GameRegistry.registerWorldGenerator(worldGenerator, 0);

		// Register tick handlers
		Proxies.common.registerTickHandlers(worldGenerator);

		// Handle IMC messages.
		processIMCMessages(FMLInterModComms.fetchRuntimeMessages(ForestryAPI.instance));
	}

	public void serverStarting(MinecraftServer server) {
		PluginManager.serverStarting(server);
	}

	public void processIMCMessages(ImmutableList<IMCMessage> messages) {
		PluginManager.processIMCMessages(messages);
	}

	public String getPriorities() {
		return "after:mod_IC2;after:mod_BuildCraftCore;after:mod_BuildCraftEnergy;after:mod_BuildCraftFactory;after:mod_BuildCraftSilicon;after:mod_BuildCraftTransport;after:mod_RedPowerWorld";
	}
}
