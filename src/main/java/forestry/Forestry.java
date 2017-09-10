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

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ForestryEvent;
import forestry.core.EventHandlerCore;
import forestry.core.climate.ClimateStates;
import forestry.core.config.Config;
import forestry.core.config.Constants;
import forestry.core.config.GameMode;
import forestry.core.errors.EnumErrorCode;
import forestry.core.errors.ErrorStateRegistry;
import forestry.core.gui.GuiHandler;
import forestry.core.multiblock.MultiblockEventHandler;
import forestry.core.network.PacketHandler;
import forestry.core.proxy.Proxies;
import forestry.core.worldgen.WorldGenerator;
import forestry.plugins.PluginManager;
import forestry.plugins.compat.PluginIC2;
import forestry.plugins.compat.PluginNatura;

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
		acceptedMinecraftVersions = "[1.12, 1.12.1]",
		dependencies = "required-after:forge@[14.21.1.2395,);"
				+ "after:jei@[4.7.8.91,);"
				+ "after:" + PluginIC2.modId + ";"
				+ "after:" + PluginNatura.modId + ";"
				+ "after:toughasnails;")
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
		ForestryAPI.states = ClimateStates.INSTANCE;
		EnumErrorCode.init();
		FluidRegistry.enableUniversalBucket();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nullable
	private static PacketHandler packetHandler;

	public static PacketHandler getPacketHandler() {
		Preconditions.checkState(packetHandler != null);
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

		PluginManager.runSetup(event);

		String gameMode = Config.gameMode;
		Preconditions.checkState(gameMode != null);
		ForestryAPI.activeMode = new GameMode(gameMode);

		PluginManager.runPreInit(event.getSide());

	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		PluginManager.runRegisterBackpacksAndCrates();
		Proxies.render.registerModels();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Register gui handler
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		PluginManager.runInit();

		Proxies.render.registerItemAndBlockColors();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PluginManager.runPostInit();

		// Register world generator
		WorldGenerator worldGenerator = new WorldGenerator();
		GameRegistry.registerWorldGenerator(worldGenerator, 0);

		// Register tick handlers
		Proxies.common.registerTickHandlers(worldGenerator);

		// Handle IMC messages.
		PluginManager.processIMCMessages(FMLInterModComms.fetchRuntimeMessages(ForestryAPI.instance));
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		PluginManager.serverStarting(event.getServer());
	}

	@Nullable
	public File getConfigFolder() {
		return configFolder;
	}

	@EventHandler
	public void processIMCMessages(IMCEvent event) {
		PluginManager.processIMCMessages(event.getMessages());
	}
}
