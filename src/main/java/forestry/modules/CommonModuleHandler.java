package forestry.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraftforge.fml.InterModComms;

import forestry.api.modules.IForestryModule;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.ItemGroupForestry;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.Log;
import forestry.modules.features.FeatureType;
import forestry.modules.features.IModFeature;
import forestry.modules.features.ModFeatureRegistry;
//import forestry.plugins.ForestryCompatPlugins;

//TODO - most of this needs tearing up and replacing
public class CommonModuleHandler {

	//TODO use toposort for sorting dependancies?
	public enum Stage {
		SETUP, // setup API to make it functional. GameMode Configs are not yet accessible
		SETUP_DISABLED, // setup fallback API to avoid crashes
		REGISTER, // register basic blocks and items
		PRE_INIT, // register handlers, definitions, and anything that depends on basic items
		BACKPACKS_CRATES, // backpacks, crates
		INIT, // anything that depends on PreInit stages, recipe registration
		POST_INIT, // stubborn mod integration, dungeon loot, and finalization of things that take input from mods
		FINISHED
	}

	protected final ModFeatureRegistry registry;
	protected final Set<BlankForestryModule> modules = new LinkedHashSet<>();
	protected final Set<IForestryModule> disabledModules = new LinkedHashSet<>();
	protected Stage stage = Stage.SETUP;

	public CommonModuleHandler() {
		this.registry = ModFeatureRegistry.get(Constants.MOD_ID);
	}

	public void addModules(Collection<IForestryModule> modules, Collection<IForestryModule> disabledModules) {
		if (stage != Stage.SETUP) {
			throw new RuntimeException("Tried to register Modules outside of SETUP");
		}
		for (IForestryModule module : modules) {
			if (!(module instanceof BlankForestryModule)) {
				continue;
			}
			this.modules.add((BlankForestryModule) module);
		}
		this.disabledModules.addAll(disabledModules);
	}

	public Stage getStage() {
		return stage;
	}

	public void runSetup() {
		stage = Stage.SETUP;
		for (IForestryModule module : modules) {
			Log.debug("Setup API Start: {}", module);
			module.setupAPI();
			Log.debug("Setup API Complete: {}", module);
		}
		stage = Stage.SETUP_DISABLED;
		for (IForestryModule module : disabledModules) {
			Log.debug("Disabled-Setup Start: {}", module);
			module.disabledSetupAPI();
			Log.debug("Disabled-Setup Complete: {}", module);
		}
		stage = Stage.REGISTER;

	}

	public void createFeatures() {
		ItemGroupForestry.create();
		ForestryPluginUtil.loadFeatureProviders();
	}

	public void createObjects(BiPredicate<FeatureType, String> filter) {
		registry.createObjects(filter);
	}

	public Collection<IModFeature> getFeatures(FeatureType type) {
		return registry.getFeatures(type);
	}

	public Collection<IModFeature> getFeatures(Predicate<FeatureType> filter) {
		return registry.getFeatures(filter);
	}

	public <T extends IForgeRegistryEntry<T>> void registerObjects(RegistryEvent.Register<T> event) {
		registry.onRegister(event);
		registerObjects();
	}

	private void registerObjects() {
		for (IForestryModule module : modules) {
			module.registerObjects();
		}
	}

	public void registerGuiFactories() {
		for (IForestryModule module : modules) {
			module.registerGuiFactories();
		}
	}

	public void runPreInit() {
		stage = Stage.PRE_INIT;
		for (BlankForestryModule module : modules) {
			Log.debug("Pre-Init Start: {}", module);
			registerHandlers(module);
			module.preInit();
			Log.debug("Pre-Init Complete: {}", module);
		}
	}

	private void registerHandlers(BlankForestryModule module) {
		Log.debug("Registering Handlers for Module: {}", module);

		IPacketRegistry packetRegistry = module.getPacketRegistry();
		if (packetRegistry != null) {
			registerPackages(packetRegistry);
		}

		IPickupHandler pickupHandler = module.getPickupHandler();
		if (pickupHandler != null) {
			ModuleManager.pickupHandlers.add(pickupHandler);
		}

		ISaveEventHandler saveHandler = module.getSaveEventHandler();
		if (saveHandler != null) {
			ModuleManager.saveEventHandlers.add(saveHandler);
		}

		IResupplyHandler resupplyHandler = module.getResupplyHandler();
		if (resupplyHandler != null) {
			ModuleManager.resupplyHandlers.add(resupplyHandler);
		}
	}

	protected void registerPackages(IPacketRegistry packetRegistry) {
		packetRegistry.registerPacketsServer();
	}

	public void runInit() {
		stage = Stage.INIT;
		for (IForestryModule module : modules) {
			Log.debug("Init Start: {}", module);
			module.doInit();
			module.registerRecipes();
			Log.debug("Init Complete: {}", module);
		}
	}

	public void runClientInit() {

	}

	public void runPostInit() {
		stage = Stage.POST_INIT;
		for (IForestryModule module : modules) {
			Log.debug("Post-Init Start: {}", module);
			module.postInit();
			Log.debug("Post-Init Complete: {}", module);
		}
		stage = Stage.FINISHED;
	}

	public void runRegisterBackpacksAndCrates() {
		stage = Stage.BACKPACKS_CRATES;
	}

	public void runBookInit() {
		for (IForestryModule module : modules) {
			Log.debug("Book Entry Registration Start: {}", module);
			//odule.registerBookEntries(ForesterBook.INSTANCE);
			Log.debug("Book Entry Registration  Complete: {}", module);
		}
	}

	public void processIMCMessages(Stream<InterModComms.IMCMessage> messages) {
		messages.forEach(m -> {
			for (BlankForestryModule module : modules) {
				if (module.processIMCMessage(m)) {
					break;
				}
			}
		});
	}
}
