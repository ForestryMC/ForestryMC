package forestry.core.utils;

import net.minecraft.util.text.TextFormatting;

import forestry.api.gui.style.ITextStyle;

public class GuiElementUtil {
	private GuiElementUtil() {
	}

	public static String getFormattedString(ITextStyle style, String rawText) {
		StringBuilder modifiers = new StringBuilder();
		if (style.isBold()) {
			modifiers.append(TextFormatting.BOLD);
		}
		if (style.isItalic()) {
			modifiers.append(TextFormatting.ITALIC);
		}
		if (style.isUnderlined()) {
			modifiers.append(TextFormatting.UNDERLINE);
		}
		if (style.isStrikethrough()) {
			modifiers.append(TextFormatting.STRIKETHROUGH);
		}
		if (style.isObfuscated()) {
			modifiers.append(TextFormatting.OBFUSCATED);
		}
		modifiers.append(rawText);
		return modifiers.toString();
	}

}
