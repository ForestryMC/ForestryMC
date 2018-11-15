package forestry.modules;

import java.io.File;
import java.util.Collection;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import forestry.Forestry;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.IForestryModule;
import forestry.api.modules.IModuleContainer;
import forestry.core.config.Constants;

public class ForestryModules implements IModuleContainer {
	private static final String MODULE_CONFIG_FILE_NAME = "modules.cfg";
	private static final String CONFIG_CATEGORY = "modules";
	private static Configuration config;

	@Override
	public String getID() {
		return Constants.MOD_ID;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public Configuration getModulesConfig() {
		return getModulesConfiguration();
	}

	public static final Configuration getModulesConfiguration() {
		if (config == null) {
			config = new Configuration(new File(Forestry.instance.getConfigFolder(), MODULE_CONFIG_FILE_NAME));
		}
		return config;
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
