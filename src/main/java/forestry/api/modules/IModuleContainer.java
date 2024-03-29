/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.modules;

import java.util.Collection;

public interface IModuleContainer {
	/**
	 * @return Unique identifier for the module container, no spaces!
	 */
	String getID();

	/**
	 * Called after configured the modules.
	 */
	default void onConfiguredModules(Collection<IForestryModule> activeModules, Collection<IForestryModule> unloadedModules) {
	}
}
