package forestry.core.utils;

import java.util.IllegalFormatException;

import net.minecraft.client.resources.I18n;

public class Translator {
	private Translator() {

	}

	public static String translateToLocal(String key) {
		return I18n.format(key);
	}

	public static boolean canTranslateToLocal(String key) {
		return I18n.hasKey(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		try {
			return I18n.format(key, format);
		} catch (IllegalFormatException e) {
			String errorMessage = "Format error: " + I18n.format(key);
			Log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
