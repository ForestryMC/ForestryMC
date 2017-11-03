package forestry.plugins;

import java.util.Collection;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.api.modules.IModuleContainer;
import forestry.modules.ForestryModules;
import forestry.modules.ForestryPluginUtil;
import forestry.modules.ModuleManager;

public class ForestryCompatPlugins implements IModuleContainer {
	private static final String MODULE_CONFIG_FILE_NAME = "modules.cfg";
	private static final String CONFIG_CATEGORY = "modules.plugins";
	public static final String ID = "forestry_compat";
	private Configuration config;

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public Configuration getModulesConfig() {
		return ForestryModules.getModulesConfiguration();
	}

	@Override
	public boolean isModuleEnabled(IForestryModule module) {
		ForestryModule info = module.getClass().getAnnotation(ForestryModule.class);

		String comment = ForestryPluginUtil.getComment(module);
		Property prop = getModulesConfig().get(CONFIG_CATEGORY, info.moduleID(), true, comment);
		return prop.getBoolean();
	}

	@Override
	public void onConfiguredModules(Collection<IForestryModule> activeModules, Collection<IForestryModule> unloadedModules) {
		ModuleManager.getInternalHandler().addModules(activeModules, unloadedModules);
	}
}
