package forestry.core.utils;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class Translator {
	private Translator() {}

	public static String translateToLocal(String key) {
		return Language.getInstance().getOrDefault(key);
	}

	public static boolean canTranslateToLocal(String key) {
		return Language.getInstance().has(key);
	}

	public static String translateToLocalFormatted(String key, Object... format) {
		return Component.translatable(key, format).getString();
	}

	public static Component tryTranslate(String optionalKey, String defaultKey) {
		return tryTranslate(optionalKey, () -> Component.translatable(defaultKey));
	}

	public static Component tryTranslate(String optionalKey, Supplier<Component> defaultKey) {
		TranslatableContents contents = new TranslatableContents(optionalKey);
		boolean translationFailed = contents.visit(s -> Optional.of(optionalKey.equals(s))).orElse(false);

		if (translationFailed) {
			return defaultKey.get();
		} else {
			return MutableComponent.create(contents);
		}
	}
}
