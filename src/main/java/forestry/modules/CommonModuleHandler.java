package forestry.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraftforge.fml.InterModComms;

import forestry.api.modules.IForestryModule;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.Log;
import forestry.modules.features.ModFeatureRegistry;
//import forestry.plugins.ForestryCompatPlugins;

//TODO - most of this needs tearing up and replacing
public class CommonModuleHandler {

	//TODO use toposort for sorting dependancies?
	public enum Stage {
		SETUP, // setup API to make it functional. GameMode Configs are not yet accessible
		SETUP_DISABLED, // setup fallback API to avoid crashes
		REGISTER, // register basic blocks and items
		PRE_INIT, // register handlers, triggers, definitions, and anything that depends on basic items
		BACKPACKS_CRATES, // backpacks, crates
		INIT, // anything that depends on PreInit stages, recipe registration
		CLIENT_SETUP, // setup anything that only exists on the client side
		POST_INIT, // stubborn mod integration, dungeon loot, and finalization of things that take input from mods
		FINISHED
	}

	protected final ModFeatureRegistry registry;
	protected final Set<BlankForestryModule> modules = new LinkedHashSet<>();
	protected final Set<IForestryModule> disabledModules = new LinkedHashSet<>();
	protected final ModuleManager moduleManager;
	private Stage stage = Stage.SETUP;

	public CommonModuleHandler(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
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

	public void registerBlocks() {
		for (IForestryModule module : modules) {
			module.registerBlocks();
		}
	}

	public void registerItems() {
		for (IForestryModule module : modules) {
			module.registerItems();
		}
	}

	public <T extends IForgeRegistryEntry<T>> void onObjectRegistration(RegistryEvent.Register<T> event) {
		if (Block.class.isAssignableFrom(event.getRegistry().getRegistrySuperType())) {
			for (IForestryModule module : modules) {
				module.registerFeatures();
			}
			registry.createObjects();
		}
		registry.onRegister(event);
	}

	//TODO generics or something on these registry events
	//TODO possible to do this for modules too?
	public void registerTileEntities() {
		for (IForestryModule module : modules) {
			module.registerTiles();
		}
	}

	public void registerEntityTypes(IForgeRegistry<EntityType<?>> registry) {
		for (IForestryModule module : modules) {
			module.registerEntityTypes(registry);
		}
	}

	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		for (IForestryModule module : modules) {
			module.registerContainerTypes(registry);
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
			//TODO - compat
			if (false) {//moduleManager.isModuleEnabled(ForestryCompatPlugins.ID, ForestryModuleUids.BUILDCRAFT_STATEMENTS)) {
				module.registerTriggers();
			}
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

	@OnlyIn(Dist.CLIENT)
	public void runClientSetup() {
		stage = Stage.CLIENT_SETUP;
		for (IForestryModule module : modules) {
			Log.debug("Init Start: {}", module);
			module.clientSetup();
			Log.debug("Init Complete: {}", module);
		}
		registry.clientSetup();
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
		for (BlankForestryModule module : modules) {
			if (moduleManager.isModuleEnabled(Constants.MOD_ID, ForestryModuleUids.BACKPACKS)) {
				Log.debug("Backpacks Start: {}", module);
				module.registerBackpackItems();
				Log.debug("Backpacks Complete: {}", module);
			}

			if (moduleManager.isModuleEnabled(Constants.MOD_ID, ForestryModuleUids.CRATE)) {
				Log.debug("Crates Start: {}", module);
				module.registerCrates();
				Log.debug("Crates Complete: {}", module);
			}
		}
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

	//TODO - worldgen
	//	public void populateChunk(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
	//		for (BlankForestryModule module : modules) {
	//			module.populateChunk(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
	//		}
	//	}

	public void decorateBiome(World world, Random rand, BlockPos pos) {
		for (BlankForestryModule module : modules) {
			module.decorateBiome(world, rand, pos);
		}
	}

	public void populateChunkRetroGen(World world, Random rand, int chunkX, int chunkZ) {
		for (BlankForestryModule module : modules) {
			module.populateChunkRetroGen(world, rand, chunkX, chunkZ);
		}
	}

	public List<ItemStack> getHiddenItems() {
		List<ItemStack> hiddenItems = new ArrayList<>();
		for (BlankForestryModule module : modules) {
			module.getHiddenItems(hiddenItems);
		}
		return hiddenItems;
	}
}
