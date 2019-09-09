package forestry.core.utils;

import net.minecraft.client.resources.I18n;

//TODO - sides issues
public class Translator {
	private Translator() {

	}

	public static String translateToLocal(String key) {
		return translateToLocalFormatted(key);
	}

	public static boolean canTranslateToLocal(String key) {
		return I18n.hasKey(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		return I18n.format(key, format);
	}
}
