package forestry.modules;

import javax.annotation.Nullable;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.storage.BackpackManager;
import forestry.api.storage.IBackpackInterface;
import forestry.api.storage.ICrateRegistry;
import forestry.api.storage.StorageManager;
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

	public static void addItemToBackpack(String backpackUid, @Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return;
		}
		IBackpackInterface backpackInterface = BackpackManager.backpackInterface;
		if (backpackInterface == null) {
			return;
		}
		backpackInterface.addItemToForestryBackpack(backpackUid, stack);
	}

	public static void registerCrate(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return;
		}
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		if (crateRegistry == null) {
			return;
		}
		crateRegistry.registerCrate(stack);
	}

	public static void registerCrate(@Nullable Item item) {
		if (item == null) {
			return;
		}
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		if (crateRegistry == null) {
			return;
		}
		crateRegistry.registerCrate(item);
	}

	public static void registerCrate(@Nullable Block block) {
		if (block == null) {
			return;
		}
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		if (crateRegistry == null) {
			return;
		}
		crateRegistry.registerCrate(block);
	}

	public static void registerCrate(@Nullable String oreDict) {
		if (oreDict == null || oreDict.isEmpty()) {
			return;
		}
		ICrateRegistry crateRegistry = StorageManager.crateRegistry;
		if (crateRegistry == null) {
			return;
		}
		crateRegistry.registerCrate(oreDict);
	}
}
