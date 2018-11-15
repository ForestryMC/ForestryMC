package forestry.modules;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.Side;

import forestry.api.modules.IForestryModule;
import forestry.core.IPickupHandler;
import forestry.core.IResupplyHandler;
import forestry.core.ISaveEventHandler;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.utils.Log;
import forestry.plugins.ForestryCompatPlugins;

public class InternalModuleHandler {

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

	protected final Set<BlankForestryModule> modules = new LinkedHashSet();
	protected final Set<IForestryModule> disabledModules = new LinkedHashSet();
	protected final ModuleManager moduleManager;
	private Stage stage = Stage.SETUP;

	public InternalModuleHandler(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
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
		for (IForestryModule module : modules) {
			Log.debug("Register Items and Blocks Start: {}", module);
			module.registerItemsAndBlocks();
			Log.debug("Register Items and Blocks Complete: {}", module);
		}
	}

	public void runPreInit(Side side) {
		stage = Stage.PRE_INIT;
		for (BlankForestryModule module : modules) {
			Log.debug("Pre-Init Start: {}", module);
			registerHandlers(module, side);
			module.preInit();
			if (moduleManager.isModuleEnabled(ForestryCompatPlugins.ID, ForestryModuleUids.BUILDCRAFT_STATEMENTS)) {
				module.registerTriggers();
			}
			Log.debug("Pre-Init Complete: {}", module);
		}
	}

	private void registerHandlers(BlankForestryModule module, Side side) {
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

	public void runInit() {
		stage = Stage.INIT;
		for (IForestryModule module : modules) {
			Log.debug("Init Start: {}", module);
			module.doInit();
			module.registerRecipes();
			Log.debug("Init Complete: {}", module);
		}
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

	public void processIMCMessages(ImmutableList<FMLInterModComms.IMCMessage> messages) {
		for (FMLInterModComms.IMCMessage message : messages) {
			for (BlankForestryModule module : modules) {
				if (module.processIMCMessage(message)) {
					break;
				}
			}
		}
	}

	public void populateChunk(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated) {
		for (BlankForestryModule module : modules) {
			module.populateChunk(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated);
		}
	}

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
