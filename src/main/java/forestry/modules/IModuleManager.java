package forestry.modules;

import java.util.Collection;

import net.minecraft.util.ResourceLocation;

public interface IModuleManager {

	default boolean isModuleEnabled(String containerID, String moduleID){
		return isModuleEnabled(new ResourceLocation(containerID, moduleID));
	}

	boolean isModuleEnabled(ResourceLocation id);

	void registerContainers(IModuleContainer... container);

	Collection<IModuleContainer> getContainers();
}
