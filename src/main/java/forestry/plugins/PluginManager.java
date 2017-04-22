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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import forestry.Forestry;
import forestry.api.core.ForestryAPI;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.PluginCore;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class PluginManager {

	private static final String PLUGIN_CONFIG_FILE_NAME = "plugins.cfg";
	private static final String CATEGORY_PLUGINS = "plugins";

	public static final ArrayList<IPickupHandler> pickupHandlers = Lists.newArrayList();
	public static final ArrayList<ISaveEventHandler> saveEventHandlers = Lists.newArrayList();
	public static final ArrayList<IResupplyHandler> resupplyHandlers = Lists.newArrayList();

	private static final Set<IForestryPlugin> loadedPlugins = new LinkedHashSet<>();
	private static final Set<IForestryPlugin> unloadedPlugins = new LinkedHashSet<>();
	private static Stage stage = Stage.SETUP;

	public enum Stage {
		SETUP, // setup API to make it functional. GameMode Configs are not yet accessible
		SETUP_DISABLED, // setup fallback API to avoid crashes
		REGISTER, // register basic blocks and items
		PRE_INIT, // register handlers, triggers, definitions, backpacks, crates, and anything that depends on basic items
		INIT, // anything that depends on PreInit stages, recipe registration
		POST_INIT, // stubborn mod integration, dungeon loot, and finalization of things that take input from mods
		FINISHED
	}

	public static final Set<IForestryPlugin> configDisabledPlugins = new HashSet<>();

	public static Stage getStage() {
		return stage;
	}

	public static Set<IForestryPlugin> getLoadedPlugins() {
		return ImmutableSet.copyOf(loadedPlugins);
	}

	private static void registerHandlers(IForestryPlugin plugin, Side side) {
		Log.debug("Registering Handlers for Plugin: {}", plugin);

		IPacketRegistry packetRegistry = plugin.getPacketRegistry();
		if (packetRegistry != null) {
			packetRegistry.registerPacketsServer();
			if (side == Side.CLIENT) {
				packetRegistry.registerPacketsClient();
			}
		}

		IPickupHandler pickupHandler = plugin.getPickupHandler();
		if (pickupHandler != null) {
			pickupHandlers.add(pickupHandler);
		}

		ISaveEventHandler saveHandler = plugin.getSaveEventHandler();
		if (saveHandler != null) {
			saveEventHandlers.add(saveHandler);
		}

		IResupplyHandler resupplyHandler = plugin.getResupplyHandler();
		if (resupplyHandler != null) {
			resupplyHandlers.add(resupplyHandler);
		}

		IFuelHandler fuelHandler = plugin.getFuelHandler();
		if (fuelHandler != null) {
			GameRegistry.registerFuelHandler(fuelHandler);
		}
	}

	private static IForestryPlugin getPluginCore(List<IForestryPlugin> forestryPlugins) {
		for (IForestryPlugin plugin : forestryPlugins) {
			if (plugin instanceof PluginCore) {
				return plugin;
			}
		}
		throw new IllegalStateException("Could not find core plugin");
	}

	private static void configurePlugins(List<IForestryPlugin> forestryPlugins) {
		Locale locale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);

		Configuration config = new Configuration(new File(Forestry.instance.getConfigFolder(), PLUGIN_CONFIG_FILE_NAME));

		config.load();
		config.addCustomCategoryComment(CATEGORY_PLUGINS, "Disabling these plugins can greatly change how the mod functions.\n"
				+ "Your mileage may vary, please report any issues.");

		IForestryPlugin corePlugin = getPluginCore(forestryPlugins);
		forestryPlugins.remove(corePlugin);
		forestryPlugins.add(0, corePlugin);

		Set<String> toLoad = new HashSet<>();

		ImmutableList<IForestryPlugin> allForestryPlugins = ImmutableList.copyOf(forestryPlugins);

		Iterator<IForestryPlugin> iterator = forestryPlugins.iterator();
		while (iterator.hasNext()) {
			IForestryPlugin plugin = iterator.next();
			if (plugin.canBeDisabled()) {
				if (!isEnabled(config, plugin)) {
					iterator.remove();
					Log.info("Plugin disabled: {}", plugin);
					continue;
				}
				if (!plugin.isAvailable()) {
					iterator.remove();
					Log.info("Plugin {} failed to load: {}", plugin, plugin.getFailMessage());
					continue;
				}
			}
			ForestryPlugin info = plugin.getClass().getAnnotation(ForestryPlugin.class);
			toLoad.add(info.pluginID());
		}

		boolean changed;
		do {
			changed = false;
			iterator = forestryPlugins.iterator();
			while (iterator.hasNext()) {
				IForestryPlugin plugin = iterator.next();
				Set<String> dependencies = plugin.getDependencyUids();
				if (!toLoad.containsAll(dependencies)) {
					iterator.remove();
					changed = true;
					ForestryPlugin info = plugin.getClass().getAnnotation(ForestryPlugin.class);
					String pluginId = info.pluginID();
					toLoad.remove(pluginId);
					Log.warning("Plugin {} is missing dependencies: {}", pluginId, dependencies);
				}
			}
		} while (changed);

		loadedPlugins.addAll(forestryPlugins);
		unloadedPlugins.addAll(allForestryPlugins);
		unloadedPlugins.removeAll(loadedPlugins);

		ForestryAPI.enabledPlugins = new HashSet<>();
		for (IForestryPlugin plugin : loadedPlugins) {
			ForestryPlugin info = plugin.getClass().getAnnotation(ForestryPlugin.class);
			ForestryAPI.enabledPlugins.add(info.pluginID());
		}

		if (config.hasChanged()) {
			config.save();
		}

		Locale.setDefault(locale);
	}

	public static void runSetup(FMLPreInitializationEvent event) {

		ASMDataTable asmDataTable = event.getAsmData();
		List<IForestryPlugin> forestryPlugins = ForestryPluginUtil.getForestryPlugins(asmDataTable);

		stage = Stage.SETUP;
		configurePlugins(forestryPlugins);

		for (IForestryPlugin plugin : loadedPlugins) {
			Log.debug("Setup API Start: {}", plugin);
			plugin.setupAPI();
			Log.debug("Setup API Complete: {}", plugin);
		}

		stage = Stage.SETUP_DISABLED;
		for (IForestryPlugin plugin : unloadedPlugins) {
			Log.debug("Disabled-Setup Start: {}", plugin);
			plugin.disabledSetupAPI();
			Log.debug("Disabled-Setup Complete: {}", plugin);
		}

		stage = Stage.REGISTER;
		for (IForestryPlugin plugin : loadedPlugins) {
			Log.debug("Register Items and Blocks Start: {}", plugin);
			plugin.registerItemsAndBlocks();
			Log.debug("Register Items and Blocks Complete: {}", plugin);
		}
	}

	public static void runPreInit(Side side) {
		stage = Stage.PRE_INIT;
		for (IForestryPlugin plugin : loadedPlugins) {
			Log.debug("Pre-Init Start: {}", plugin);
			registerHandlers(plugin, side);
			plugin.preInit();
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.BUILDCRAFT_STATEMENTS)) {
				plugin.registerTriggers();
			}
			if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.STORAGE)) {
				plugin.registerBackpackItems();
				plugin.registerCrates();
			}
			Log.debug("Pre-Init Complete: {}", plugin);
		}
	}

	public static void runInit() {
		stage = Stage.INIT;
		for (IForestryPlugin plugin : loadedPlugins) {
			Log.debug("Init Start: {}", plugin);
			plugin.doInit();
			plugin.registerRecipes();
			Log.debug("Init Complete: {}", plugin);
		}
	}

	public static void runPostInit() {
		stage = Stage.POST_INIT;
		for (IForestryPlugin plugin : loadedPlugins) {
			Log.debug("Post-Init Start: {}", plugin);
			plugin.postInit();
			Log.debug("Post-Init Complete: {}", plugin);
		}

		stage = Stage.FINISHED;
	}

	public static void serverStarting(MinecraftServer server) {
		CommandHandler commandManager = (CommandHandler) server.getCommandManager();

		for (IForestryPlugin plugin : loadedPlugins) {
			ICommand[] commands = plugin.getConsoleCommands();
			if (commands == null) {
				continue;
			}
			for (ICommand command : commands) {
				commandManager.registerCommand(command);
			}
		}
	}

	public static void processIMCMessages(ImmutableList<FMLInterModComms.IMCMessage> messages) {
		for (FMLInterModComms.IMCMessage message : messages) {
			for (IForestryPlugin plugin : loadedPlugins) {
				if (plugin.processIMCMessage(message)) {
					break;
				}
			}
		}
	}

	public static void populateChunk(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		for (IForestryPlugin plugin : loadedPlugins) {
			plugin.populateChunk(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
		}
	}

	public static void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		for (IForestryPlugin plugin : loadedPlugins) {
			plugin.populateChunkRetroGen(world, rand, chunkX, chunkZ);
		}
	}


	public static List<ItemStack> getHiddenItems() {
		List<ItemStack> hiddenItems = new ArrayList<>();
		for (IForestryPlugin plugin : loadedPlugins) {
			plugin.getHiddenItems(hiddenItems);
		}
		return hiddenItems;
	}


	public static Set<String> getLootPoolNames() {
		Set<String> lootPoolNames = new HashSet<>();
		for (IForestryPlugin plugin : loadedPlugins) {
			plugin.addLootPoolNames(lootPoolNames);
		}
		return lootPoolNames;
	}

	private static boolean isEnabled(Configuration config, IForestryPlugin plugin) {
		ForestryPlugin info = plugin.getClass().getAnnotation(ForestryPlugin.class);

		String comment = Translator.translateToLocal(info.unlocalizedDescription());
		Property prop = config.get(CATEGORY_PLUGINS, info.pluginID(), true, comment);
		boolean enabled = prop.getBoolean();

		if (!enabled) {
			configDisabledPlugins.add(plugin);
		}

		return enabled;
	}
}
