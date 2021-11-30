package forestry.modules;

import java.util.stream.Stream;

import forestry.core.config.Constants;

public final class ModuleHelper {

	private ModuleHelper() {
	}

	public static boolean isEnabled(String moduleID) {
		return isModuleEnabled(Constants.MOD_ID, moduleID);
	}

	public static boolean allEnabled(String... moduleIDs) {
		return Stream.of(moduleIDs).allMatch(ModuleHelper::isEnabled);
	}

	public static boolean anyEnabled(String... moduleIDs) {
		return Stream.of(moduleIDs).anyMatch(ModuleHelper::isEnabled);
	}

	public static boolean isModuleEnabled(String containerID, String moduleID) {
		ModuleManager manager = ModuleManager.getInstance();
		return manager.isModuleEnabled(containerID, moduleID);
	}
}
