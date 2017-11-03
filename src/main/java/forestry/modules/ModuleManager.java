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
package forestry.modules;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;

import net.minecraftforge.common.config.Configuration;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.api.modules.IModuleContainer;
import forestry.api.modules.IModuleManager;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.Log;

public class ModuleManager implements IModuleManager {

	private static final String CONFIG_CATEGORY = "modules";
	private static ModuleManager ourInstance = new ModuleManager();

	public static final ArrayList<IPickupHandler> pickupHandlers = Lists.newArrayList();
	public static final ArrayList<ISaveEventHandler> saveEventHandlers = Lists.newArrayList();
	public static final ArrayList<IResupplyHandler> resupplyHandlers = Lists.newArrayList();

	private static final HashMap<ResourceLocation, IForestryModule> sortedModules = new LinkedHashMap<>();
	private static final Set<IForestryModule> loadedModules = new LinkedHashSet<>();
	private static final Set<BlankForestryModule> internalModules = new LinkedHashSet<>();
	private static final Set<IForestryModule> unloadedModules = new LinkedHashSet<>();
	private static final HashMap<String, IModuleContainer> moduleContainers = new HashMap<>();
	public static final Set<IForestryModule> configDisabledModules = new HashSet<>();
	private static Stage stage = Stage.SETUP;

	public enum Stage {
		SETUP, // setup API to make it functional. GameMode Configs are not yet accessible
		SETUP_DISABLED, // setup fallback API to avoid crashes
		REGISTER, // register basic blocks and items
		PRE_INIT, // register handlers, triggers, definitions, and anything that depends on basic items
		BACKPACKS_CRATES, // backpacks, crates
		INIT, // anything that depends on PreInit stages, recipe registration
		POST_INIT, // stubborn mod integration, dungeon loot, and finalization of things that take input from mods
		FINISHED
	}

	private ModuleManager() {
	}

	public static ModuleManager getInstance() {
		return ourInstance;
	}

	@Override
	public boolean isModuleEnabled(ResourceLocation id) {
		return sortedModules.get(id) != null;
	}

	@Override
	public void registerContainers(IModuleContainer... containers) {
		for(IModuleContainer container : containers) {
			Preconditions.checkNotNull(container);
			moduleContainers.put(container.getID(), container);
		}
	}

	@Override
	public Collection<IModuleContainer> getContainers() {
		return moduleContainers.values();
	}

	public static Stage getStage() {
		return stage;
	}

	public static Set<IForestryModule> getLoadedModules() {
		return ImmutableSet.copyOf(sortedModules.values());
	}

	private static void registerHandlers(BlankForestryModule module, Side side) {
		Log.debug("Registering Handlers for Module: {}", module);

		IPacketRegistry packetRegistry = module.getPacketRegistry();
		if (packetRegistry != null) {
			packetRegistry.registerPacketsServer();
			if (side == Side.CLIENT) {
				packetRegistry.registerPacketsClient();
			}
		}

		IPickupHandler pickupHandler = module.getPickupHandler();
		if (pickupHandler != null) {
			pickupHandlers.add(pickupHandler);
		}

		ISaveEventHandler saveHandler = module.getSaveEventHandler();
		if (saveHandler != null) {
			saveEventHandlers.add(saveHandler);
		}

		IResupplyHandler resupplyHandler = module.getResupplyHandler();
		if (resupplyHandler != null) {
			resupplyHandlers.add(resupplyHandler);
		}
	}

	private static IForestryModule getModuleCore(List<IForestryModule> forestryModules) {
		for (IForestryModule module : forestryModules) {
			ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
			if (module.isAvailable() && info.coreModule()) {
				return module;
			}
		}
		return null;
	}

	private static IForestryModule getCoreModule(List<IForestryModule> modules, String containerID) {
		for (IForestryModule module : modules) {
			ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
			if (info.coreModule()) {
				return module;
			}
		}
		throw new IllegalStateException("Could not find core module for the container " + containerID);
	}

	private static void configureModules(Map<String, List<IForestryModule>> modules) {
		Locale locale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);

		Set<ResourceLocation> toLoad = new HashSet<>();
		Set<IForestryModule> modulesToLoad = new HashSet<>();

		ImmutableList<IForestryModule> allModules = ImmutableList.copyOf(modules.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList()));

		for(IModuleContainer container : moduleContainers.values()){
			String containerID = container.getID();
			List<IForestryModule> containerModules = modules.get(containerID);
			Configuration config = container.getModulesConfig();

			config.load();
			config.addCustomCategoryComment(CONFIG_CATEGORY, "Disabling these modules can greatly change how the mod functions.\n"
				+ "Your mileage may vary, please report any issues.");
			IForestryModule coreModule = getModuleCore(containerModules);
			if(coreModule != null) {
				containerModules.remove(coreModule);
				containerModules.add(0, coreModule);
			}else{
				Log.debug("Could not find core module for the module container: {}", containerID);
			}

			Iterator<IForestryModule> iterator = containerModules.iterator();
			while (iterator.hasNext()) {
				IForestryModule module = iterator.next();
				if(!container.isAvailable()){
					iterator.remove();
					Log.info("Module disabled: {}", module);
					continue;
				}
				if (module.canBeDisabled()) {
					if (!container.isModuleEnabled(module)) {
						configDisabledModules.add(module);
						iterator.remove();
						Log.info("Module disabled: {}", module);
						continue;
					}
					if (!module.isAvailable()) {
						iterator.remove();
						Log.info("Module {} failed to load: {}", module, module.getFailMessage());
						continue;
					}
				}
				ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
				toLoad.add(new ResourceLocation(containerID, info.moduleID()));
				modulesToLoad.add(module);
			}
		}

		//Check Dependencies
		Iterator<IForestryModule> iterator;
		boolean changed;
		do {
			changed = false;
			iterator = modulesToLoad.iterator();
			while (iterator.hasNext()) {
				IForestryModule module = iterator.next();
				Set<ResourceLocation> dependencies = module.getDependencyUids();
				if (!toLoad.containsAll(dependencies)) {
					iterator.remove();
					changed = true;
					ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
					String moduleId = info.moduleID();
					toLoad.remove(moduleId);
					Log.warning("Module {} is missing dependencies: {}", moduleId, dependencies);
				}
			}
		} while (changed);

		//Sort Modules
		do {
			changed = false;
			iterator = modulesToLoad.iterator();
			while (iterator.hasNext()) {
				IForestryModule module = iterator.next();
				if (sortedModules.keySet().containsAll(module.getDependencyUids())) {
					iterator.remove();
					ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
					sortedModules.put(new ResourceLocation(info.containerID(), info.moduleID()), module);
					changed = true;
					break;
				}
			}
		} while (changed);

		for(IModuleContainer container : moduleContainers.values()){
			Configuration config = container.getModulesConfig();
			if(config.hasChanged()){
				config.save();
			}
		}

		loadedModules.addAll(sortedModules.values());
		sortedModules.values().stream().filter((m)->m instanceof BlankForestryModule).forEach((IForestryModule m)->internalModules.add((BlankForestryModule) m));
		unloadedModules.addAll(allModules);
		unloadedModules.removeAll(sortedModules.values());

		ForestryAPI.enabledModules = new HashSet<>();
		for (IForestryModule module : sortedModules.values()) {
			ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
			ForestryAPI.enabledModules.add(new ResourceLocation(info.containerID(), info.moduleID()));
		}

		Locale.setDefault(locale);
	}

	public static void runSetup(FMLPreInitializationEvent event) {

		ASMDataTable asmDataTable = event.getAsmData();
		Map<String, List<IForestryModule>> forestryModules = ForestryPluginUtil.getForestryModules(asmDataTable);

		stage = Stage.SETUP;
		configureModules(forestryModules);

		for (IForestryModule module : loadedModules) {
			Log.debug("Setup API Start: {}", module);
			module.setupAPI();
			Log.debug("Setup API Complete: {}", module);
		}

		stage = Stage.SETUP_DISABLED;
		for (IForestryModule module : unloadedModules) {
			Log.debug("Disabled-Setup Start: {}", module);
			module.disabledSetupAPI();
			Log.debug("Disabled-Setup Complete: {}", module);
		}

		stage = Stage.REGISTER;
		for (IForestryModule module : loadedModules) {
			Log.debug("Register Items and Blocks Start: {}", module);
			module.registerItemsAndBlocks();
			Log.debug("Register Items and Blocks Complete: {}", module);
		}
	}

	public static void runPreInit(Side side) {
		stage = Stage.PRE_INIT;
		for (IForestryModule module : loadedModules) {
			Log.debug("Pre-Init Start: {}", module);
			if(module instanceof BlankForestryModule) {
				BlankForestryModule moduleInternal = (BlankForestryModule) module;
				registerHandlers(moduleInternal, side);
			}
			module.preInit();
			if (getInstance().isModuleEnabled(Constants.MOD_ID, ForestryModuleUids.BUILDCRAFT_STATEMENTS)) {
				module.registerTriggers();
			}
			Log.debug("Pre-Init Complete: {}", module);
		}
	}

	public static void runRegisterBackpacksAndCrates() {
		stage = Stage.BACKPACKS_CRATES;
		for (IForestryModule module : loadedModules) {
			if (getInstance().isModuleEnabled(Constants.MOD_ID, ForestryModuleUids.CRATE)) {
				Log.debug("BackpacksAndCrates Start: {}", module);
				module.registerBackpackItems();
				module.registerCrates();
				Log.debug("BackpacksAndCrates Complete: {}", module);
			}
		}
	}

	public static void runInit() {
		stage = Stage.INIT;
		for (IForestryModule module : loadedModules) {
			Log.debug("Init Start: {}", module);
			module.doInit();
			module.registerRecipes();
			Log.debug("Init Complete: {}", module);
		}
	}

	public static void runPostInit() {
		stage = Stage.POST_INIT;
		for (IForestryModule module : loadedModules) {
			Log.debug("Post-Init Start: {}", module);
			module.postInit();
			Log.debug("Post-Init Complete: {}", module);
		}

		stage = Stage.FINISHED;
	}

	public static void serverStarting(MinecraftServer server) {
		CommandHandler commandManager = (CommandHandler) server.getCommandManager();

		for (IForestryModule module : loadedModules) {
			ICommand[] commands = module.getConsoleCommands();
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
			for (BlankForestryModule module : internalModules) {
				if (module.processIMCMessage(message)) {
					break;
				}
			}
		}
	}

	public static void populateChunk(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		for (BlankForestryModule module : internalModules) {
			module.populateChunk(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
		}
	}

	public static void decorateBiome(World world, Random rand, BlockPos pos) {
		for (BlankForestryModule module : internalModules) {
			module.decorateBiome(world, rand, pos);
		}
	}

	public static void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		for (BlankForestryModule module : internalModules) {
			module.populateChunkRetroGen(world, rand, chunkX, chunkZ);
		}
	}


	public static List<ItemStack> getHiddenItems() {
		List<ItemStack> hiddenItems = new ArrayList<>();
		for (BlankForestryModule module : internalModules) {
			module.getHiddenItems(hiddenItems);
		}
		return hiddenItems;
	}


	public static Set<String> getLootPoolNames() {
		Set<String> lootPoolNames = new HashSet<>();
		for (IForestryModule module : loadedModules) {
			module.addLootPoolNames(lootPoolNames);
		}
		return lootPoolNames;
	}

	public static Set<String> getLootTableFiles() {
		Set<String> lootTableNames = new HashSet<>();
		for (IForestryModule module : loadedModules) {
			ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
			String lootTableFolder = info.lootTable();
			if (!lootTableFolder.isEmpty()) {
				lootTableNames.add(lootTableFolder);
			}
		}
		return lootTableNames;
	}
}
