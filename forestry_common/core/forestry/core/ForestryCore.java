/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core;

import java.io.File;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.ImmutableList;

import forestry.api.apiculture.FlowerManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IPlugin;
import forestry.api.fuels.EngineBronzeFuel;
import forestry.api.fuels.EngineCopperFuel;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.fuels.RainSubstrate;
import forestry.core.config.Config;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryItem;
import forestry.core.config.Version;
import forestry.core.gadgets.TileEngine;
import forestry.core.gadgets.TileMachine;
import forestry.core.gadgets.TileMill;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.plugins.NativePlugin;
import forestry.plugins.PluginCore;
import forestry.plugins.PluginManager;

public class ForestryCore {

	public void preInit(File modLocation, Object basemod) {
		ForestryAPI.instance = basemod;
		ForestryAPI.forestryConstants = new ForestryConstants();

		PluginManager.loadPlugins(modLocation);

		// Register event handler
		MinecraftForge.EVENT_BUS.register(new EventHandlerCore());

		Config.load();
		if (!Config.disableVersionCheck)
			Version.versionCheck();

		for (IPlugin plugin : PluginManager.plugins) {
			if (plugin instanceof PluginCore)
				plugin.preInit();
		}

		for (IPlugin plugin : PluginManager.plugins) {
			if (plugin instanceof PluginCore)
				continue;

			if (plugin.isAvailable())
				plugin.preInit();
			else
				Proxies.log.fine("Skipped plugin " + plugin.getClass() + " because preconditions were not met.");
		}

	}

	public void init(Object basemod) {

		// Register Liquids & Containers
		registerLiquidContainers();

		// Register world generator
		GameRegistry.registerWorldGenerator(new WorldGenerator(), 0);

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
		FuelManager.bronzeEngineFuel.put(LiquidHelper.getFluid(Defaults.LIQUID_BIOMASS), new EngineBronzeFuel(LiquidHelper.getFluid(Defaults.LIQUID_BIOMASS),
				Defaults.ENGINE_FUEL_VALUE_BIOMASS, (int) (Defaults.ENGINE_CYCLE_DURATION_BIOMASS * GameMode.getGameMode().getFloatSetting("fuel.biomass.biogas")), 1));
		FuelManager.bronzeEngineFuel.put(LiquidHelper.getFluid(Defaults.LIQUID_WATER), new EngineBronzeFuel(LiquidHelper.getFluid(Defaults.LIQUID_WATER),
				Defaults.ENGINE_FUEL_VALUE_WATER, Defaults.ENGINE_CYCLE_DURATION_WATER, 3));
		FuelManager.bronzeEngineFuel.put(LiquidHelper.getFluid(Defaults.LIQUID_MILK), new EngineBronzeFuel(LiquidHelper.getFluid(Defaults.LIQUID_MILK),
				Defaults.ENGINE_FUEL_VALUE_MILK, Defaults.ENGINE_CYCLE_DURATION_MILK, 3));
		FuelManager.bronzeEngineFuel.put(LiquidHelper.getFluid(Defaults.LIQUID_SEEDOIL), new EngineBronzeFuel(LiquidHelper.getFluid(Defaults.LIQUID_SEEDOIL),
				Defaults.ENGINE_FUEL_VALUE_SEED_OIL, Defaults.ENGINE_CYCLE_DURATION_SEED_OIL, 1));
		FuelManager.bronzeEngineFuel.put(LiquidHelper.getFluid(Defaults.LIQUID_HONEY), new EngineBronzeFuel(LiquidHelper.getFluid(Defaults.LIQUID_HONEY),
				Defaults.ENGINE_FUEL_VALUE_HONEY, Defaults.ENGINE_CYCLE_DURATION_HONEY, 1));
		FuelManager.bronzeEngineFuel.put(LiquidHelper.getFluid(Defaults.LIQUID_JUICE), new EngineBronzeFuel(LiquidHelper.getFluid(Defaults.LIQUID_JUICE),
				Defaults.ENGINE_FUEL_VALUE_JUICE, Defaults.ENGINE_CYCLE_DURATION_JUICE, 1));

		// Set rain substrates
		FuelManager.rainSubstrate.put(ForestryItem.iodineCharge.getItemStack(), new RainSubstrate(ForestryItem.iodineCharge.getItemStack(),
				Defaults.RAINMAKER_RAIN_DURATION_IODINE, 0.01f));
		FuelManager.rainSubstrate.put(ForestryItem.craftingMaterial.getItemStack(1, 4), new RainSubstrate(ForestryItem.craftingMaterial.getItemStack(1, 4), 0.075f));

		// Set additional apiary flowers
		for (int i=0; i<8; i++) {
			FlowerManager.plainFlowers.add(new ItemStack(Blocks.red_flower, 1, i));
		}
		FlowerManager.plainFlowers.add(new ItemStack(Blocks.yellow_flower));

		// Register gui handler
		NetworkRegistry.INSTANCE.registerGuiHandler(basemod, new GuiHandler());

		// Register machines
		GameRegistry.registerTileEntity(TileMill.class, "forestry.Grower");
		GameRegistry.registerTileEntity(TileEngine.class, "forestry.Engine");
		GameRegistry.registerTileEntity(TileMachine.class, "forestry.Machine");


	}

	public void postInit() {

		for (IPlugin plugin : PluginManager.plugins) {
			if (plugin.isAvailable())
				plugin.doInit();
		}

		for (IPlugin plugin : PluginManager.plugins) {
			if (plugin.isAvailable())
				plugin.postInit();
		}

		Proxies.common.registerTickHandlers();

		// Handle IMC messages.
		processIMCMessages(FMLInterModComms.fetchRuntimeMessages(ForestryAPI.instance));
	}

	public void serverStarting(MinecraftServer server) {
		CommandHandler commandManager = (CommandHandler) server.getCommandManager();
		for (IPlugin plugin : PluginManager.plugins) {
			if (plugin.isAvailable() && plugin instanceof NativePlugin) {
				ICommand[] commands = ((NativePlugin) plugin).getConsoleCommands();
				if (commands == null)
					continue;
				for (ICommand command : commands) {
					commandManager.registerCommand(command);
				}
			}
		}
	}

	public void processIMCMessages(ImmutableList<IMCMessage> messages) {
		for (IMCMessage message : messages) {
			for (IPlugin plugin : PluginManager.plugins) {
				if (!(plugin instanceof NativePlugin))
					continue;

				if (((NativePlugin) plugin).processIMCMessage(message))
					break;
			}
		}
	}

	public String getPriorities() {
		return "after:mod_IC2;after:mod_BuildCraftCore;after:mod_BuildCraftEnergy;after:mod_BuildCraftFactory;after:mod_BuildCraftSilicon;after:mod_BuildCraftTransport;after:mod_RedPowerWorld";
	}

	private void registerLiquidContainers() {
		// Add lava and water buckets to the API in case this has not been done yet.
		if (LiquidHelper.isEmptyLiquidData()) {
			LiquidHelper.injectLiquidContainer(Defaults.LIQUID_LAVA, Defaults.BUCKET_VOLUME, new ItemStack(Items.lava_bucket), new ItemStack(Items.bucket));
			LiquidHelper.injectLiquidContainer(Defaults.LIQUID_WATER, Defaults.BUCKET_VOLUME, new ItemStack(Items.water_bucket), new ItemStack(Items.bucket));
		}

		// Glass
		LiquidHelper.getOrCreateLiquid(Defaults.LIQUID_GLASS);

		// Set default lava, water and biofuel buckets
		LiquidHelper.getOrCreateLiquid(Defaults.LIQUID_MILK);
		LiquidHelper.injectLiquidContainer(Defaults.LIQUID_MILK, Defaults.BUCKET_VOLUME, new ItemStack(Items.milk_bucket), new ItemStack(Items.bucket));

		// Lava
		LiquidHelper.injectTinContainer(Defaults.LIQUID_LAVA, Defaults.BUCKET_VOLUME, ForestryItem.canLava.getItemStack(),
				ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_LAVA, Defaults.BUCKET_VOLUME, ForestryItem.refractoryLava.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		// Water
		LiquidHelper.injectLiquidContainer(Defaults.LIQUID_WATER, Defaults.BUCKET_VOLUME, new ItemStack(Items.potionitem, 1, 0),
				new ItemStack(Items.glass_bottle));
		LiquidHelper.injectTinContainer(Defaults.LIQUID_WATER, Defaults.BUCKET_VOLUME, ForestryItem.canWater.getItemStack(),
				ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_WATER, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleWater.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_WATER, Defaults.BUCKET_VOLUME, ForestryItem.refractoryWater.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		LiquidHelper.getOrCreateLiquid(Defaults.LIQUID_BIOMASS);
		LiquidHelper.injectLiquidContainer(Defaults.LIQUID_BIOMASS, Defaults.BUCKET_VOLUME, ForestryItem.bucketBiomass.getItemStack(), new ItemStack(Items.bucket));
		LiquidHelper.injectTinContainer(Defaults.LIQUID_BIOMASS, Defaults.BUCKET_VOLUME, ForestryItem.canBiomass.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_BIOMASS, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleBiomass.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_BIOMASS, Defaults.BUCKET_VOLUME, ForestryItem.refractoryBiomass.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		LiquidHelper.getOrCreateLiquid(Defaults.LIQUID_ETHANOL);
		LiquidHelper.injectLiquidContainer(Defaults.LIQUID_ETHANOL, Defaults.BUCKET_VOLUME, ForestryItem.bucketBiofuel.getItemStack(), new ItemStack(Items.bucket));
		LiquidHelper.injectTinContainer(Defaults.LIQUID_ETHANOL, Defaults.BUCKET_VOLUME, ForestryItem.canBiofuel.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_ETHANOL, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleBiofuel.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_ETHANOL, Defaults.BUCKET_VOLUME, ForestryItem.refractoryBiofuel.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		LiquidHelper.getOrCreateLiquid(Defaults.LIQUID_SEEDOIL);
		LiquidHelper.injectTinContainer(Defaults.LIQUID_SEEDOIL, Defaults.BUCKET_VOLUME, ForestryItem.canSeedOil.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_SEEDOIL, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleSeedOil.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_SEEDOIL, Defaults.BUCKET_VOLUME, ForestryItem.refractorySeedOil.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		LiquidHelper.getOrCreateLiquid(Defaults.LIQUID_HONEY);
		LiquidHelper.injectTinContainer(Defaults.LIQUID_HONEY, Defaults.BUCKET_VOLUME, ForestryItem.canHoney.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_HONEY, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleHoney.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_HONEY, Defaults.BUCKET_VOLUME, ForestryItem.refractoryHoney.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		LiquidHelper.getOrCreateLiquid(Defaults.LIQUID_JUICE);
		LiquidHelper.injectTinContainer(Defaults.LIQUID_JUICE, Defaults.BUCKET_VOLUME, ForestryItem.canJuice.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_JUICE, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleJuice.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_JUICE, Defaults.BUCKET_VOLUME, ForestryItem.refractoryJuice.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		LiquidHelper.getOrCreateLiquid(Defaults.LIQUID_ICE);
		LiquidHelper.injectTinContainer(Defaults.LIQUID_ICE, Defaults.BUCKET_VOLUME, ForestryItem.canIce.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Defaults.LIQUID_ICE, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleIce.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Defaults.LIQUID_ICE, Defaults.BUCKET_VOLUME, ForestryItem.refractoryIce.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

	}
}
