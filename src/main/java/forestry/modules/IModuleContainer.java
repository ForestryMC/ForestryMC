package forestry.modules;

import net.minecraftforge.common.config.Configuration;

public interface IModuleContainer {
	/**
	 * @return Unique identifier for the module container, no spaces!
	 */
	String getID();

	boolean isAvailable();

	Configuration getModulesConfig();

	/**
	 * @return true if the module is enabled in the config file of this container.
	 */
	boolean isModuleEnabled(IForestryModule module);
}
