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

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

import forestry.Forestry;
import forestry.api.core.ForestryAPI;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.utils.Log;
import forestry.plugins.compat.PluginAgriCraft;
import forestry.plugins.compat.PluginBiomesOPlenty;
import forestry.plugins.compat.PluginBuildCraftFuels;
import forestry.plugins.compat.PluginBuildCraftRecipes;
import forestry.plugins.compat.PluginBuildCraftStatements;
import forestry.plugins.compat.PluginBuildCraftTransport;
import forestry.plugins.compat.PluginChisel;
import forestry.plugins.compat.PluginEnderIO;
import forestry.plugins.compat.PluginErebus;
import forestry.plugins.compat.PluginExtraUtilities;
import forestry.plugins.compat.PluginGrowthCraft;
import forestry.plugins.compat.PluginHarvestCraft;
import forestry.plugins.compat.PluginIC2;
import forestry.plugins.compat.PluginImmersiveEngineering;
import forestry.plugins.compat.PluginMagicalCrops;
import forestry.plugins.compat.PluginMineFactoryReloaded;
import forestry.plugins.compat.PluginNatura;
import forestry.plugins.compat.PluginPlantMegaPack;
import forestry.plugins.compat.PluginRotaryCraft;
import forestry.plugins.compat.PluginWitchery;

public class PluginManager {

	private static final String MODULE_CONFIG_FILE_NAME = "modules.cfg";
	private static final String CATEGORY_MODULES = "modules";

	public static final ArrayList<IPickupHandler> pickupHandlers = Lists.newArrayList();
	public static final ArrayList<ISaveEventHandler> saveEventHandlers = Lists.newArrayList();
	public static final ArrayList<IResupplyHandler> resupplyHandlers = Lists.newArrayList();

	private static final Set<Module> loadedModules = EnumSet.noneOf(Module.class);
	private static final Set<Module> unloadedModules = EnumSet.allOf(Module.class);
	private static Stage stage = Stage.SETUP;

	public enum Stage {
		SETUP, // setup API to make it functional, register basic blocks and items. GameMode Configs are not yet accessible
		SETUP_DISABLED, // setup fallback API to avoid crashes
		PRE_INIT, // register handlers, triggers, definitions, backpacks, crates, and anything that depends on basic items
		INIT, // anything that depends on PreInit stages, recipe registration
		POST_INIT, // stubborn mod integration, dungeon loot, and finalization of things that take input from mods
		FINISHED
	}

	public enum Module {

		CORE(new PluginCore(), false),
		FLUIDS(new PluginFluids(), false),

		APICULTURE(new PluginApiculture()),
		ARBORICULTURE(new PluginArboriculture()),
		ENERGY(new PluginEnergy()),
		FACTORY(new PluginFactory()),
		FARMING(new PluginFarming()),
		FOOD(new PluginFood()),
		LEPIDOPTEROLOGY(new PluginLepidopterology()),
		MAIL(new PluginMail()),
		STORAGE(new PluginStorage()),

		BUILDCRAFT_FUELS(new PluginBuildCraftFuels()),
		BUILDCRAFT_RECIPES(new PluginBuildCraftRecipes()),
		BUILDCRAFT_STATEMENTS(new PluginBuildCraftStatements()),
		BUILDCRAFT_TRANSPORT(new PluginBuildCraftTransport()),
		
		AGRICRAFT(new PluginAgriCraft()),
		BIOMESOPLENTY(new PluginBiomesOPlenty()),
		CHISEL(new PluginChisel()),
		ENDERIO(new PluginEnderIO()),
		EREBUS(new PluginErebus()),
		EXTRAUTILITIES(new PluginExtraUtilities()),
		GROWTHCRAFT(new PluginGrowthCraft()),
		HARVESTCRAFT(new PluginHarvestCraft()),
		IMMERSIVEENGINEERING(new PluginImmersiveEngineering()),
		INDUSTRIALCRAFT(new PluginIC2()),
		MAGICALCROPS(new PluginMagicalCrops()),
		MINEFACTORYRELOADED(new PluginMineFactoryReloaded()),
		NATURA(new PluginNatura()),
		PLANTMEGAPACK(new PluginPlantMegaPack()),
		ROTARYCRAFT(new PluginRotaryCraft()),
		WITCHERY(new PluginWitchery());

		static {
			ForestryAPI.enabledPlugins = new HashSet<>();
		}

		private final ForestryPlugin instance;
		private final boolean canBeDisabled;

		Module(ForestryPlugin plugin) {
			this(plugin, true);
		}

		Module(ForestryPlugin plugin, boolean canBeDisabled) {
			this.instance = plugin;
			this.canBeDisabled = canBeDisabled;
		}

		public ForestryPlugin instance() {
			return instance;
		}

		public boolean isEnabled() {
			return ForestryAPI.enabledPlugins.contains(toString());
		}

		public boolean canBeDisabled() {
			return canBeDisabled;
		}

		public String configName() {
			return toString().toLowerCase(Locale.ENGLISH).replace('_', '.');
		}

	}

	public static final EnumSet<Module> configDisabledModules = EnumSet.noneOf(Module.class);

	public static Stage getStage() {
		return stage;
	}

	public static EnumSet<Module> getLoadedModules() {
		return EnumSet.copyOf(loadedModules);
	}

	private static void registerHandlers(ForestryPlugin plugin) {
		Log.fine("Registering Handlers for Plugin: {0}", plugin);

		plugin.getPacketRegistry().registerPackets();

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
			GameRegistry.registerFuelHandler((fuelHandler));
		}
	}

	private static void configureModules() {
		Locale locale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);

		Configuration config = new Configuration(new File(Forestry.instance.getConfigFolder(), MODULE_CONFIG_FILE_NAME));

		config.load();
		config.addCustomCategoryComment(CATEGORY_MODULES, "Disabling these modules can greatly change how the mod functions.\n"
				+ "Your mileage may vary, please report any issues.");

		Set<Module> toLoad = EnumSet.allOf(Module.class);
		Iterator<Module> it = toLoad.iterator();
		while (it.hasNext()) {
			Module m = it.next();
			if (!m.canBeDisabled()) {
				continue;
			}
			if (!isEnabled(config, m)) {
				it.remove();
				Log.info("Module disabled: {0}", m);
				continue;
			}
			ForestryPlugin plugin = m.instance;
			if (!plugin.isAvailable()) {
				it.remove();
				Log.info("Module {0} failed to load: {1}", plugin, plugin.getFailMessage());
				continue;
			}
		}

		boolean changed;
		do {
			changed = false;
			it = toLoad.iterator();
			while (it.hasNext()) {
				Module m = it.next();
				Set<Module> deps = m.instance().getDependancies();
				if (!toLoad.containsAll(deps)) {
					it.remove();
					changed = true;
					Log.warning("Module {0} is missing dependencies: {1}", m, deps);
					continue;
				}
			}
		} while (changed);

		unloadedModules.removeAll(toLoad);
		loadedModules.addAll(toLoad);

		for (Module m : loadedModules) {
			ForestryAPI.enabledPlugins.add(m.toString());
		}

		if (config.hasChanged()) {
			config.save();
		}

		Locale.setDefault(locale);
	}

	public static void runSetup() {
		stage = Stage.SETUP;
		configureModules();

		for (Module m : loadedModules) {
			ForestryPlugin plugin = m.instance;
			Log.fine("Setup Start: {0}", plugin);
			plugin.setupAPI();
			plugin.registerItemsAndBlocks();
			Log.fine("Setup Complete: {0}", plugin);
		}

		stage = Stage.SETUP_DISABLED;
		for (Module m : unloadedModules) {
			ForestryPlugin plugin = m.instance;
			Log.fine("Disabled-Setup Start: {0}", plugin);
			plugin.disabledSetupAPI();
			Log.fine("Disabled-Setup Complete: {0}", plugin);
		}
	}

	public static void runPreInit() {
		stage = Stage.PRE_INIT;
		for (Module m : loadedModules) {
			ForestryPlugin plugin = m.instance;
			Log.fine("Pre-Init Start: {0}", plugin);
			registerHandlers(plugin);
			plugin.preInit();
			if (Module.BUILDCRAFT_STATEMENTS.isEnabled()) {
				plugin.registerTriggers();
			}
			if (Module.STORAGE.isEnabled()) {
				plugin.registerBackpackItems();
				plugin.registerCrates();
			}
			Log.fine("Pre-Init Complete: {0}", plugin);
		}
	}

	public static void runInit() {
		stage = Stage.INIT;
		for (Module m : loadedModules) {
			ForestryPlugin plugin = m.instance;
			Log.fine("Init Start: {0}", plugin);
			plugin.doInit();
			plugin.registerRecipes();
			Log.fine("Init Complete: {0}", plugin);
		}
	}

	public static void runPostInit() {
		stage = Stage.POST_INIT;
		for (Module m : loadedModules) {
			ForestryPlugin plugin = m.instance;
			Log.fine("Post-Init Start: {0}", plugin);
			plugin.postInit();
			Log.fine("Post-Init Complete: {0}", plugin);
		}

		stage = Stage.FINISHED;
	}

	public static void serverStarting(MinecraftServer server) {
		CommandHandler commandManager = (CommandHandler) server.getCommandManager();

		for (Module m : loadedModules) {
			ForestryPlugin plugin = m.instance;
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
			for (Module m : loadedModules) {
				ForestryPlugin plugin = m.instance;
				if (plugin.processIMCMessage(message)) {
					break;
				}
			}
		}
	}

	public static void populateChunk(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		for (Module m : loadedModules) {
			ForestryPlugin plugin = m.instance;
			plugin.populateChunk(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
		}
	}

	public static void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		for (Module m : loadedModules) {
			ForestryPlugin plugin = m.instance;
			plugin.populateChunkRetroGen(world, rand, chunkX, chunkZ);
		}
	}

	private static boolean isEnabled(Configuration config, Module m) {
		Plugin info = m.instance().getClass().getAnnotation(Plugin.class);

		String comment = StatCollector.translateToLocal(info.unlocalizedDescription());
		Property prop = config.get(CATEGORY_MODULES, m.configName(), true, comment);
		boolean enabled = prop.getBoolean();

		if (!enabled) {
			configDisabledModules.add(m);
		}

		return enabled;
	}
}
