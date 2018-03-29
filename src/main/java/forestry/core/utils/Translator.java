package forestry.core.utils;

import java.util.IllegalFormatException;

import net.minecraft.client.resources.I18n;

public class Translator {
	private Translator() {

	}

	public static String translateToLocal(String key) {
		if (I18n.hasKey(key)) {
			return I18n.format(key);
		} else {
			return net.minecraft.util.text.translation.I18n.translateToFallback(key);    //no equivalent from what I can see

		}
	}

	public static boolean canTranslateToLocal(String key) {
		return I18n.hasKey(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		String s = translateToLocal(key);
		try {
			return String.format(s, format);
		} catch (IllegalFormatException e) {
			String errorMessage = "Format error: " + s;
			Log.error(errorMessage, e);
			return errorMessage;
		}
	}
}
