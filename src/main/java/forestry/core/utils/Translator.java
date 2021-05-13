package forestry.core.utils;

import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TranslationTextComponent;

//TODO - sides issues
@Deprecated
public class Translator {
	private Translator() {

	}

	public static String translateToLocal(String key) {
		return LanguageMap.getInstance().getOrDefault(key);
	}

	public static boolean canTranslateToLocal(String key) {
		return LanguageMap.getInstance().has(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		return new TranslationTextComponent(key, format).getString();
	}
}
