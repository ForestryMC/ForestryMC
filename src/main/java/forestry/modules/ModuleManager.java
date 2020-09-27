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
import com.mojang.brigadier.CommandDispatcher;
import forestry.api.core.ForestryAPI;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.api.modules.IModuleContainer;
import forestry.api.modules.IModuleManager;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.config.forge_old.Configuration;
import forestry.core.utils.Log;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager implements IModuleManager {
    private static final String CONFIG_CATEGORY = "modules";
    private static final ModuleManager ourInstance = new ModuleManager();

    public static final List<IPickupHandler> pickupHandlers = Lists.newArrayList();
    public static final List<ISaveEventHandler> saveEventHandlers = Lists.newArrayList();
    public static final List<IResupplyHandler> resupplyHandlers = Lists.newArrayList();

    private static final HashMap<ResourceLocation, IForestryModule> sortedModules = new LinkedHashMap<>();
    private static final Set<IForestryModule> loadedModules = new LinkedHashSet<>();
    private static final Set<IForestryModule> unloadedModules = new LinkedHashSet<>();
    private static final HashMap<String, IModuleContainer> moduleContainers = new HashMap<>();
    public static final Set<IForestryModule> configDisabledModules = new HashSet<>();
    public static CommonModuleHandler moduleHandler;

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
        for (IModuleContainer container : containers) {
            Preconditions.checkNotNull(container);
            moduleContainers.put(container.getID(), container);
        }
    }

    @Override
    public Collection<IModuleContainer> getContainers() {
        return moduleContainers.values();
    }

    public static Set<IForestryModule> getLoadedModules() {
        return ImmutableSet.copyOf(sortedModules.values());
    }

    @Nullable
    private static IForestryModule getModuleCore(List<IForestryModule> forestryModules) {
        for (IForestryModule module : forestryModules) {
            ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
            if (module.isAvailable() && info.coreModule()) {
                return module;
            }
        }

        return null;
    }

    private static void configureModules(Map<String, List<IForestryModule>> modules) {
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        Set<ResourceLocation> toLoad = new HashSet<>();
        Set<IForestryModule> modulesToLoad = new HashSet<>();

        ImmutableList<IForestryModule> allModules = ImmutableList.copyOf(modules.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        for (IModuleContainer container : moduleContainers.values()) {
            String containerID = container.getID();
            List<IForestryModule> containerModules = modules.get(containerID);
            Configuration config = container.getModulesConfig();

            config.load();
            config.addCustomCategoryComment(
                    CONFIG_CATEGORY,
                    "Disabling these modules can greatly change how the mod functions.\n"
                            + "Your mileage may vary, please report any issues."
            );
            IForestryModule coreModule = getModuleCore(containerModules);
            if (coreModule != null) {
                containerModules.remove(coreModule);
                containerModules.add(0, coreModule);
            } else {
                Log.debug("Could not find core module for the module container: {}", containerID);
            }

            Iterator<IForestryModule> iterator = containerModules.iterator();
            while (iterator.hasNext()) {
                IForestryModule module = iterator.next();
                if (!container.isAvailable()) {
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
                    toLoad.remove(new ResourceLocation(moduleId));
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

        for (IModuleContainer container : moduleContainers.values()) {
            Configuration config = container.getModulesConfig();
            if (config.hasChanged()) {
                config.save();
            }
        }

        loadedModules.addAll(sortedModules.values());
        unloadedModules.addAll(allModules);
        unloadedModules.removeAll(sortedModules.values());

        for (IModuleContainer container : moduleContainers.values()) {
            Collection<IForestryModule> loadedModules = sortedModules.values().stream().filter(m -> {
                        ForestryModule info = m.getClass().getAnnotation(ForestryModule.class);
                        return info.containerID().equals(container.getID());
                    }
            ).collect(Collectors.toList());
            Collection<IForestryModule> unloadedModules = ModuleManager.unloadedModules.stream().filter(m -> {
                        ForestryModule info = m.getClass().getAnnotation(ForestryModule.class);
                        return info.containerID().equals(container.getID());
                    }
            ).collect(Collectors.toList());
            container.onConfiguredModules(loadedModules, unloadedModules);
        }

        ForestryAPI.enabledModules = new HashSet<>();
        for (IForestryModule module : sortedModules.values()) {
            ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);
            ForestryAPI.enabledModules.add(new ResourceLocation(info.containerID(), info.moduleID()));
        }

        Locale.setDefault(locale);
    }

    public static void runSetup() {
        Map<String, List<IForestryModule>> forestryModules = ForestryPluginUtil.getForestryModules();

        moduleHandler = DistExecutor.safeRunForDist(() -> ClientModuleHandler::new, () -> CommonModuleHandler::new);
        configureModules(forestryModules);
    }

    public static CommonModuleHandler getModuleHandler() {
        Preconditions.checkNotNull(moduleHandler);
        return moduleHandler;
    }

    public static void serverStarting(MinecraftServer server) {
        CommandDispatcher<CommandSource> dispatcher = server.getCommandManager().getDispatcher();

        loadedModules.stream()
                .map(IForestryModule::register)
                .filter(Objects::nonNull)
                .forEach(dispatcher::register);
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
