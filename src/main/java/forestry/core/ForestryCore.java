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
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.api.apiculture.FlowerManager;
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
import forestry.core.config.Version;
import forestry.core.fluids.Fluids;
import forestry.core.gadgets.TileEngine;
import forestry.core.gadgets.TileMachine;
import forestry.core.gadgets.TileMill;
import forestry.core.proxy.Proxies;
import forestry.core.utils.LiquidHelper;
import forestry.plugins.PluginManager;
import java.io.File;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

public class ForestryCore {

	public void preInit(File modLocation, Object basemod) {
		ForestryAPI.instance = basemod;
		ForestryAPI.forestryConstants = new ForestryConstants();

		// Register event handler
		MinecraftForge.EVENT_BUS.register(new EventHandlerCore());

		Config.load();
		if (!Config.disableVersionCheck)
			Version.versionCheck();
		
		EnumErrorCode.init();

		PluginManager.runPreInit();
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

		FuelManager.bronzeEngineFuel.put(Fluids.BIOMASS.get(), new EngineBronzeFuel(Fluids.BIOMASS.get(),
				Defaults.ENGINE_FUEL_VALUE_BIOMASS, (int) (Defaults.ENGINE_CYCLE_DURATION_BIOMASS * GameMode.getGameMode().getFloatSetting("fuel.biomass.biogas")), 1));
		FuelManager.bronzeEngineFuel.put(Fluids.WATER.get(), new EngineBronzeFuel(Fluids.WATER.get(),
				Defaults.ENGINE_FUEL_VALUE_WATER, Defaults.ENGINE_CYCLE_DURATION_WATER, 3));
		FuelManager.bronzeEngineFuel.put(Fluids.MILK.get(), new EngineBronzeFuel(Fluids.MILK.get(),
				Defaults.ENGINE_FUEL_VALUE_MILK, Defaults.ENGINE_CYCLE_DURATION_MILK, 3));
		FuelManager.bronzeEngineFuel.put(Fluids.SEEDOIL.get(), new EngineBronzeFuel(Fluids.SEEDOIL.get(),
				Defaults.ENGINE_FUEL_VALUE_SEED_OIL, Defaults.ENGINE_CYCLE_DURATION_SEED_OIL, 1));
		FuelManager.bronzeEngineFuel.put(Fluids.HONEY.get(), new EngineBronzeFuel(Fluids.HONEY.get(),
				Defaults.ENGINE_FUEL_VALUE_HONEY, Defaults.ENGINE_CYCLE_DURATION_HONEY, 1));
		FuelManager.bronzeEngineFuel.put(Fluids.JUICE.get(), new EngineBronzeFuel(Fluids.JUICE.get(),
				Defaults.ENGINE_FUEL_VALUE_JUICE, Defaults.ENGINE_CYCLE_DURATION_JUICE, 1));

		// Set rain substrates
		FuelManager.rainSubstrate.put(ForestryItem.iodineCharge.getItemStack(), new RainSubstrate(ForestryItem.iodineCharge.getItemStack(),
				Defaults.RAINMAKER_RAIN_DURATION_IODINE, 0.01f));
		FuelManager.rainSubstrate.put(ForestryItem.craftingMaterial.getItemStack(1, 4), new RainSubstrate(ForestryItem.craftingMaterial.getItemStack(1, 4), 0.075f));

		// Set additional apiary flowers
		for (int i = 0; i < 9; i++)
			FlowerManager.plainFlowers.add(new ItemStack(Blocks.red_flower, 1, i));
		FlowerManager.plainFlowers.add(new ItemStack(Blocks.yellow_flower));

		// Register gui handler
		NetworkRegistry.INSTANCE.registerGuiHandler(basemod, new GuiHandler());

		// Register machines
		GameRegistry.registerTileEntity(TileMill.class, "forestry.Grower");
		GameRegistry.registerTileEntity(TileEngine.class, "forestry.Engine");
		GameRegistry.registerTileEntity(TileMachine.class, "forestry.Machine");

		PluginManager.runInit();
	}

	public void postInit() {

		PluginManager.runPostInit();

		Proxies.common.registerTickHandlers();

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

	private void registerLiquidContainers() {
		// Add lava and water buckets to the API in case this has not been done yet.
		if (LiquidHelper.isEmptyLiquidData()) {
			LiquidHelper.injectLiquidContainer(Fluids.LAVA, Defaults.BUCKET_VOLUME, new ItemStack(Items.lava_bucket), new ItemStack(Items.bucket));
			LiquidHelper.injectLiquidContainer(Fluids.WATER, Defaults.BUCKET_VOLUME, new ItemStack(Items.water_bucket), new ItemStack(Items.bucket));
		}

		// Glass
		Fluids.GLASS.register();

		// Set default lava, water and biofuel buckets
		Fluids.MILK.register();
		LiquidHelper.injectLiquidContainer(Fluids.MILK, Defaults.BUCKET_VOLUME, new ItemStack(Items.milk_bucket), new ItemStack(Items.bucket));

		// Lava
		LiquidHelper.injectTinContainer(Fluids.LAVA, Defaults.BUCKET_VOLUME, ForestryItem.canLava.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectRefractoryContainer(Fluids.LAVA, Defaults.BUCKET_VOLUME, ForestryItem.refractoryLava.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		// Water
		LiquidHelper.injectLiquidContainer(Fluids.WATER, Defaults.BUCKET_VOLUME, new ItemStack(Items.potionitem, 1, 0), new ItemStack(Items.glass_bottle));
		LiquidHelper.injectTinContainer(Fluids.WATER, Defaults.BUCKET_VOLUME, ForestryItem.canWater.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Fluids.WATER, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleWater.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Fluids.WATER, Defaults.BUCKET_VOLUME, ForestryItem.refractoryWater.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		Fluids.BIOMASS.register();
		LiquidHelper.injectLiquidContainer(Fluids.BIOMASS, Defaults.BUCKET_VOLUME, ForestryItem.bucketBiomass.getItemStack(), new ItemStack(Items.bucket));
		LiquidHelper.injectTinContainer(Fluids.BIOMASS, Defaults.BUCKET_VOLUME, ForestryItem.canBiomass.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Fluids.BIOMASS, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleBiomass.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Fluids.BIOMASS, Defaults.BUCKET_VOLUME, ForestryItem.refractoryBiomass.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		Fluids.BIOETHANOL.register();
		LiquidHelper.injectLiquidContainer(Fluids.BIOETHANOL, Defaults.BUCKET_VOLUME, ForestryItem.bucketBiofuel.getItemStack(), new ItemStack(Items.bucket));
		LiquidHelper.injectTinContainer(Fluids.BIOETHANOL, Defaults.BUCKET_VOLUME, ForestryItem.canBiofuel.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Fluids.BIOETHANOL, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleBiofuel.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Fluids.BIOETHANOL, Defaults.BUCKET_VOLUME, ForestryItem.refractoryBiofuel.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		Fluids.SEEDOIL.register();
		LiquidHelper.injectTinContainer(Fluids.SEEDOIL, Defaults.BUCKET_VOLUME, ForestryItem.canSeedOil.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Fluids.SEEDOIL, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleSeedOil.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Fluids.SEEDOIL, Defaults.BUCKET_VOLUME, ForestryItem.refractorySeedOil.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		Fluids.HONEY.register();
		LiquidHelper.injectTinContainer(Fluids.HONEY, Defaults.BUCKET_VOLUME, ForestryItem.canHoney.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Fluids.HONEY, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleHoney.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Fluids.HONEY, Defaults.BUCKET_VOLUME, ForestryItem.refractoryHoney.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		Fluids.JUICE.register();
		LiquidHelper.injectTinContainer(Fluids.JUICE, Defaults.BUCKET_VOLUME, ForestryItem.canJuice.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Fluids.JUICE, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleJuice.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Fluids.JUICE, Defaults.BUCKET_VOLUME, ForestryItem.refractoryJuice.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

		Fluids.ICE.register();
		LiquidHelper.injectTinContainer(Fluids.ICE, Defaults.BUCKET_VOLUME, ForestryItem.canIce.getItemStack(), ForestryItem.canEmpty.getItemStack());
		LiquidHelper.injectWaxContainer(Fluids.ICE, Defaults.BUCKET_VOLUME, ForestryItem.waxCapsuleIce.getItemStack(), ForestryItem.waxCapsule.getItemStack());
		LiquidHelper.injectRefractoryContainer(Fluids.ICE, Defaults.BUCKET_VOLUME, ForestryItem.refractoryIce.getItemStack(), ForestryItem.refractoryEmpty.getItemStack());

	}
}
