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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import forestry.Forestry;
import forestry.core.interfaces.IOreDictionaryHandler;
import forestry.core.interfaces.IPacketHandler;
import forestry.core.interfaces.IPickupHandler;
import forestry.core.interfaces.IResupplyHandler;
import forestry.core.interfaces.ISaveEventHandler;
import forestry.core.proxy.Proxies;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class PluginManager {

	public static final String MODULE_CONFIG_FILE_NAME = "modules.cfg";
	public static final String CATEGORY_MODULES = "modules";

	public static ArrayList<IGuiHandler> guiHandlers = Lists.newArrayList();
	public static ArrayList<IPacketHandler> packetHandlers = Lists.newArrayList();
	public static ArrayList<IPickupHandler> pickupHandlers = Lists.newArrayList();
	public static ArrayList<ISaveEventHandler> saveEventHandlers = Lists.newArrayList();
	public static ArrayList<IResupplyHandler> resupplyHandlers = Lists.newArrayList();
	public static ArrayList<IOreDictionaryHandler> dictionaryHandlers = Lists.newArrayList();

	private static ArrayList<PluginModule> loadedModules;
	private static ArrayList<PluginModule> unloadedModules;
	private static Stage stage = Stage.SETUP;

	public enum Stage {

		SETUP, PRE_INIT, INIT, POST_INIT, INIT_DISABLED, FINISHED;
	}

	private static HashMap<Class<? extends ForestryPlugin>, PluginModule> pluginModules = new HashMap<Class<? extends ForestryPlugin>, PluginModule>();

	static {
		ArrayList<Class<? extends ForestryPlugin>> pluginClasses = new ArrayList<Class<? extends ForestryPlugin>>();
		pluginClasses.add(PluginCore.class);
		pluginClasses.add(PluginApiculture.class);
		pluginClasses.add(PluginArboriculture.class);
		pluginClasses.add(PluginEnergy.class);
		pluginClasses.add(PluginFactory.class);
		pluginClasses.add(PluginFarming.class);
		pluginClasses.add(PluginFood.class);
		pluginClasses.add(PluginLepidopterology.class);
		pluginClasses.add(PluginMail.class);
		pluginClasses.add(PluginStorage.class);
		pluginClasses.add(PluginBuildCraft.class);
		pluginClasses.add(PluginPropolisPipe.class);
		pluginClasses.add(PluginEE.class);
		pluginClasses.add(PluginFarmCraftory.class);
		pluginClasses.add(PluginIC2.class);
		pluginClasses.add(PluginNatura.class);

		unloadedModules = new ArrayList<PluginModule>();
		loadedModules = new ArrayList<PluginModule>();

		for (Class<? extends ForestryPlugin>pluginClass : pluginClasses) {
			PluginModule pluginModule = new PluginModule(pluginClass);
			pluginModules.put(pluginClass, pluginModule);
			unloadedModules.add(pluginModule);
		}
	}

	public static Stage getStage() {
		return stage;
	}

	public static PluginModule getPluginModule(Class<? extends ForestryPlugin> pluginClass) {
		return pluginModules.get(pluginClass);
	}

	public static List<PluginModule> getLoadedModules() {
		return new ArrayList<PluginModule>(loadedModules);
	}

	public static boolean isModuleLoaded(Class<? extends ForestryPlugin> pluginClass) {
		PluginModule pluginModule = pluginModules.get(pluginClass);
		return loadedModules.contains(pluginModule);
	}

	public static void addPlugin(ForestryPlugin plugin) {

	}

	public static void runPreInit() {
		stage = Stage.SETUP;
		Locale locale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);

		Configuration config = new Configuration(new File(Forestry.instance.getConfigFolder(), MODULE_CONFIG_FILE_NAME));

		config.load();
		config.addCustomCategoryComment(CATEGORY_MODULES, "Disabling these modules can greatly change how the mod functions.\n"
				+ "Your milage may vary, please report any issues.");

		ArrayList<PluginModule> toLoad = new ArrayList<PluginModule>(unloadedModules);
		Iterator<PluginModule> it = toLoad.iterator();
		while (it.hasNext()) {
			PluginModule m = it.next();
			if (m == getPluginModule(PluginCore.class))
				continue;
			if (!isEnabled(config, m)) {
				it.remove();
				Proxies.log.info("Module disabled: {0}", m);
				continue;
			}
			if (m.instance() == null) {
				it.remove();
				Proxies.log.info("Module {0} failed to load.", m);
				continue;
			}
		}
		boolean changed;
		do {
			changed = false;
			it = toLoad.iterator();
			while (it.hasNext()) {
				PluginModule m = it.next();

				Set<Class<? extends ForestryPlugin>> dependenciesClasses = m.instance().getDependencies();
				Set<PluginModule> dependencies = new HashSet<PluginModule>();
				for (Class<? extends ForestryPlugin> depClass : dependenciesClasses)
					dependencies.add(getPluginModule(depClass));

				if (!toLoad.containsAll(dependencies)) {
					it.remove();
					changed = true;
					Proxies.log.warning("Module {0} is missing dependencies: {1}", m, dependencies);
					continue;
				}
			}
		} while (changed);

		unloadedModules.removeAll(toLoad);
		loadedModules.addAll(toLoad);

		if (config.hasChanged())
			config.save();

		Locale.setDefault(locale);

		stage = Stage.PRE_INIT;
		for (PluginModule m : loadedModules) {
			ForestryPlugin plugin = m.instance();
			loadPlugin(plugin);
			Proxies.log.fine("Pre-Init Start: {0}", plugin);
			plugin.preInit();
			plugin.registerItems();
			Proxies.log.fine("Pre-Init Complete: {0}", plugin);
		}
	}

	private static void loadPlugin(ForestryPlugin plugin) {
		Proxies.log.fine("Loading Plugin: {0}", plugin);

		IGuiHandler guiHandler = plugin.getGuiHandler();
		if (guiHandler != null)
			guiHandlers.add(guiHandler);

		IPacketHandler packetHandler = plugin.getPacketHandler();
		if (packetHandler != null)
			packetHandlers.add(packetHandler);

		IPickupHandler pickupHandler = plugin.getPickupHandler();
		if (pickupHandler != null)
			pickupHandlers.add(pickupHandler);

		ISaveEventHandler saveHandler = plugin.getSaveEventHandler();
		if (saveHandler != null)
			saveEventHandlers.add(saveHandler);

		IResupplyHandler resupplyHandler = plugin.getResupplyHandler();
		if (resupplyHandler != null)
			resupplyHandlers.add(resupplyHandler);

		IOreDictionaryHandler dictionaryHandler = plugin.getDictionaryHandler();
		if (dictionaryHandler != null)
			dictionaryHandlers.add(dictionaryHandler);

		IFuelHandler fuelHandler = plugin.getFuelHandler();
		if (fuelHandler != null)
			GameRegistry.registerFuelHandler((fuelHandler));
	}

	public static void runInit() {
		stage = Stage.INIT;
		for (PluginModule m : loadedModules) {
			ForestryPlugin plugin = m.instance();
			Proxies.log.fine("Init Start: {0}", plugin);
			plugin.registerBackpackItems();
			plugin.registerCrates();
			plugin.doInit();
			Proxies.log.fine("Init Complete: {0}", plugin);
		}
	}

	public static void runPostInit() {
		stage = Stage.POST_INIT;
		for (PluginModule m : loadedModules) {
			ForestryPlugin plugin = m.instance();
			Proxies.log.fine("Post-Init Start: {0}", plugin);
			plugin.registerRecipes();
			plugin.postInit();
			Proxies.log.fine("Post-Init Complete: {0}", plugin);
		}

		stage = Stage.INIT_DISABLED;
		for (PluginModule m : unloadedModules) {
			ForestryPlugin plugin = m.instance();
			Proxies.log.fine("Disabled-Init Start: {0}", m);
			if (plugin != null)
				plugin.disabledInit();
			Proxies.log.fine("Disabled-Init Complete: {0}", m);
		}
		stage = Stage.FINISHED;
	}

	public static void serverStarting(MinecraftServer server) {
		CommandHandler commandManager = (CommandHandler) server.getCommandManager();

		for (PluginModule m : loadedModules) {
			ForestryPlugin plugin = m.instance();
			ICommand[] commands = plugin.getConsoleCommands();
			if (commands == null)
				continue;
			for (ICommand command : commands)
				commandManager.registerCommand(command);
		}
	}

	public static void processIMCMessages(ImmutableList<FMLInterModComms.IMCMessage> messages) {
		for (FMLInterModComms.IMCMessage message : messages)
			for (PluginModule m : loadedModules) {
				ForestryPlugin plugin = m.instance();
				if (plugin.processIMCMessage(message))
					break;
			}
	}

	public static void generateSurface(World world, Random rand, int chunkX, int chunkZ) {
		for (PluginModule m : loadedModules) {
			ForestryPlugin plugin = m.instance();
			plugin.generateSurface(world, rand, chunkX, chunkZ);
		}
	}

	private static boolean isEnabled(Configuration config, PluginModule m) {
		boolean defaultValue = true;
		Property prop = config.get(CATEGORY_MODULES, m.toString().toLowerCase(Locale.ENGLISH).replace('_', '.'), defaultValue);
		return prop.getBoolean(true);
	}
}
