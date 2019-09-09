package genetics.utils;

import net.minecraft.nbt.CompoundNBT;

public class NBTUtils {

	private NBTUtils() {
	}

	public static String getString(CompoundNBT compound, String key, String fallback) {
		if (compound.contains(key)) {
			return compound.getString(key);
		}
		return fallback;
	}

	public static String getString(CompoundNBT compound, String key) {
		if (compound.contains(key)) {
			return compound.getString(key);
		}
		throw new IllegalStateException("Missing " + key + ", expected to find a string");
	}

	public static boolean getBoolean(CompoundNBT compound, String key, boolean fallback) {
		if (compound.contains(key)) {
			return compound.getBoolean(key);
		}
		return fallback;
	}

	public static int getInt(CompoundNBT compound, String key, int fallback) {
		if (compound.contains(key)) {
			return compound.getInt(key);
		}
		return fallback;
	}
}
