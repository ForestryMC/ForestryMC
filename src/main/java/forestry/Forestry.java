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
package forestry;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.File;

import net.minecraft.item.Item;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.climate.ClimateManager;
import forestry.api.core.ForestryAPI;
import forestry.api.core.ForestryEvent;
import forestry.core.EventHandlerCore;
import forestry.core.advancements.AdvancementManager;
import forestry.core.climate.ClimateFactory;
import forestry.core.climate.ClimateRoot;
import forestry.core.climate.ClimateStateHelper;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.GameMode;
import forestry.core.errors.EnumErrorCode;
import forestry.core.errors.ErrorStateRegistry;
import forestry.core.gui.GuiHandler;
import forestry.core.multiblock.MultiblockEventHandler;
import forestry.core.network.PacketHandler;
import forestry.core.proxy.Proxies;
import forestry.core.utils.MigrationHelper;
import forestry.core.worldgen.WorldGenerator;
import forestry.modules.ForestryModules;
import forestry.modules.ModuleManager;
import forestry.plugins.ForestryCompatPlugins;
import forestry.plugins.PluginBuildCraftFuels;
import forestry.plugins.PluginIC2;
import forestry.plugins.PluginNatura;
import forestry.plugins.PluginTechReborn;

/**
 * Forestry Minecraft Mod
 *
 * @author SirSengir
 */
@Mod(
	modid = Constants.MOD_ID,
	name = Constants.MOD_NAME,
	version = Constants.VERSION,
	guiFactory = "forestry.core.config.ForestryGuiConfigFactory",
	acceptedMinecraftVersions = "[1.12.2,1.13.0)",
	dependencies = "required-after:forge@[14.23.4.2749,);"
		+ "after:jei@[4.12.0.0,);"
		+ "after:" + PluginIC2.MOD_ID + ";"
		+ "after:" + PluginNatura.MOD_ID + ";"
		+ "after:toughasnails;"
		+ "after:" + PluginTechReborn.MOD_ID + ";"
		+ "after:" + PluginBuildCraftFuels.MOD_ID + ";"
		+ "before:binniecore@[2.5.1.184,)")
public class Forestry {

	@SuppressWarnings("NullableProblems")
	@Mod.Instance(Constants.MOD_ID)
	public static Forestry instance;
	@Nullable
	private File configFolder;

	public Forestry() {
		ForestryAPI.instance = this;
		ForestryAPI.forestryConstants = new Constants();
		ForestryAPI.errorStateRegistry = new ErrorStateRegistry();
		ClimateManager.climateRoot = ForestryAPI.climateManager = ClimateRoot.getInstance();
		ClimateManager.climateFactory = ClimateFactory.INSTANCE;
		ClimateManager.stateHelper = ClimateStateHelper.INSTANCE;
		EnumErrorCode.init();
		FluidRegistry.enableUniversalBucket();
		MinecraftForge.EVENT_BUS.register(this);
		ModuleManager moduleManager = ModuleManager.getInstance();
		ForestryAPI.moduleManager = moduleManager;
		moduleManager.registerContainers(new ForestryModules(), new ForestryCompatPlugins());
	}

	@Nullable
	private static PacketHandler packetHandler;

	public static PacketHandler getPacketHandler() {
		Preconditions.checkNotNull(packetHandler);
		return packetHandler;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		packetHandler = new PacketHandler();

		// Register event handler
		EventHandlerCore eventHandlerCore = new EventHandlerCore();
		MinecraftForge.EVENT_BUS.register(eventHandlerCore);
		MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
		MinecraftForge.EVENT_BUS.register(Config.class);
		Proxies.common.registerEventHandlers();

		configFolder = new File(event.getModConfigurationDirectory(), Constants.MOD_ID);
		Config.load(event.getSide());

		MinecraftForge.EVENT_BUS.post(new ForestryEvent.PreInit(this, event));

		ModuleManager.runSetup(event);
		ModuleManager.getInternalHandler().runSetup();

		String gameMode = Config.gameMode;
		Preconditions.checkNotNull(gameMode);
		ForestryAPI.activeMode = new GameMode(gameMode);

		MigrationHelper.registerFixable();

		ModuleManager.getInternalHandler().runPreInit(event.getSide());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void registerItems(RegistryEvent.Register<Item> event) {
		ModuleManager.getInternalHandler().runRegisterBackpacksAndCrates();
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		Proxies.render.registerModels();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Register gui handler
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		ModuleManager.getInternalHandler().runInit();

		Proxies.render.registerItemAndBlockColors();
		AdvancementManager.registerTriggers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ModuleManager.getInternalHandler().runPostInit();

		// Register world generator
		WorldGenerator worldGenerator = new WorldGenerator();
		GameRegistry.registerWorldGenerator(worldGenerator, 0);

		// Register tick handlers
		Proxies.common.registerTickHandlers(worldGenerator);

		// Handle IMC messages.
		ModuleManager.getInternalHandler().processIMCMessages(FMLInterModComms.fetchRuntimeMessages(ForestryAPI.instance));
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		ModuleManager.serverStarting(event.getServer());
	}

	@Nullable
	public File getConfigFolder() {
		return configFolder;
	}

	@EventHandler
	public void processIMCMessages(IMCEvent event) {
		ModuleManager.getInternalHandler().processIMCMessages(event.getMessages());
	}
}
