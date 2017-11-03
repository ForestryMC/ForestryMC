package forestry.api.modules;

import java.util.Collection;

public interface IModuleHandler {

	void runSetup();

	void runPreInit();

	void runInit();

	void runPostInit();

	Collection<IForestryModule> getModules();
}
