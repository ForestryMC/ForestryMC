/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.modules;

import java.util.Collection;

import net.minecraft.resources.ResourceLocation;

/**
 * The module manager of Forestry.
 */
public interface IModuleManager {

	void registerContainers(IModuleContainer... container);

	Collection<IModuleContainer> getContainers();
}
