/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.modules;

import java.util.Collection;

import net.minecraft.util.ResourceLocation;

/**
 * The module manager of Forestry.
 */
public interface IModuleManager {

	default boolean isModuleEnabled(String containerID, String moduleID){
		return isModuleEnabled(new ResourceLocation(containerID, moduleID));
	}

	boolean isModuleEnabled(ResourceLocation id);

	void registerContainers(IModuleContainer... container);

	Collection<IModuleContainer> getContainers();
}
