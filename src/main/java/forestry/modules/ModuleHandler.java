package forestry.modules;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

import net.minecraftforge.fml.common.FMLCommonHandler;

import forestry.api.modules.IForestryModule;
import forestry.api.modules.IModuleHandler;
import forestry.core.config.Constants;
import forestry.core.utils.Log;

public class ModuleHandler implements IModuleHandler {

	private final Set<IForestryModule> modules;
	private final ModuleManager manager;

	public ModuleHandler(Collection<IForestryModule> modules, ModuleManager manager) {
		this.modules = ImmutableSet.copyOf(modules);
		this.manager = manager;
	}

	public void runSetup() {
		for (IForestryModule module : modules) {
			Log.debug("Setup API Start: {}", module);
			module.setupAPI();
			Log.debug("Setup API Complete: {}", module);
		}

		for (IForestryModule module : modules) {
			Log.debug("Disabled-Setup Start: {}", module);
			module.disabledSetupAPI();
			Log.debug("Disabled-Setup Complete: {}", module);
		}

		for (IForestryModule module : modules) {
			Log.debug("Register Items and Blocks Start: {}", module);
			module.registerItemsAndBlocks();
			Log.debug("Register Items and Blocks Complete: {}", module);
		}
	}

	public void runPreInit() {
		for (IForestryModule module : modules) {
			Log.debug("Pre-Init Start: {}", module);
			if(module instanceof BlankForestryModule) {
				BlankForestryModule moduleInternal = (BlankForestryModule) module;
				ModuleManager.registerHandlers(moduleInternal, FMLCommonHandler.instance().getEffectiveSide());
			}
			module.preInit();
			if (manager.isModuleEnabled(Constants.MOD_ID, ForestryModuleUids.BUILDCRAFT_STATEMENTS)) {
				module.registerTriggers();
			}
			Log.debug("Pre-Init Complete: {}", module);
		}
	}

	public void runInit() {
		for (IForestryModule module : modules) {
			Log.debug("Init Start: {}", module);
			module.doInit();
			module.registerRecipes();
			Log.debug("Init Complete: {}", module);
		}
	}

	public void runPostInit() {
		for (IForestryModule module : modules) {
			Log.debug("Post-Init Start: {}", module);
			module.postInit();
			Log.debug("Post-Init Complete: {}", module);
		}
	}

	@Override
	public Set<IForestryModule> getModules() {
		return modules;
	}
}
