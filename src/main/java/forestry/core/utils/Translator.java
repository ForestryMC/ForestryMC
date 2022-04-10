package forestry.core.utils;

import java.util.function.Supplier;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TranslationTextComponent;

public class Translator {
	private Translator() {}

	public static String translateToLocal(String key) {
		return LanguageMap.getInstance().getOrDefault(key);
	}

	public static boolean canTranslateToLocal(String key) {
		return LanguageMap.getInstance().has(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		return new TranslationTextComponent(key, format).getString();
	}

	public static ITextComponent tryTranslate(String optionalKey, String defaultKey) {
		return tryTranslate(() -> new TranslationTextComponent(optionalKey), () -> new TranslationTextComponent(defaultKey));
	}

	public static ITextComponent tryTranslate(String optionalKey, Supplier<ITextComponent> defaultKey) {
		return tryTranslate(() -> new TranslationTextComponent(optionalKey), defaultKey);
	}

	/**
	 * Tries to translate the optional key component. Returns the default key component if the first can't be translated.
	 *
	 * @return The optional component if it can be translated the other component otherwise.
	 */
	private static ITextComponent tryTranslate(Supplier<TranslationTextComponent> optionalKey, Supplier<ITextComponent> defaultKey) {
		TranslationTextComponent component = optionalKey.get();
		if (canTranslate(component)) {
			return component;
		} else {
			return defaultKey.get();
		}
	}

	public static boolean canTranslate(TranslationTextComponent component) {
		String translatedText = component.getString();
		return !translatedText.startsWith(component.getKey());
	}
}
