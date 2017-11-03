/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.modules;

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
