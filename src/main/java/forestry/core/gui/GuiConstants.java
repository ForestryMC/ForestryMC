package forestry.core.gui;

import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Style;

public class GuiConstants {
	public static final Style DEFAULT_STYLE = Style.EMPTY;
	public static final Style BLACK_STYLE = DEFAULT_STYLE.withColor(TextColor.fromRgb(0x000000));
	public static final Style GRAY_STYLE = DEFAULT_STYLE.withColor(TextColor.fromRgb(0x666666));
	public static final Style UNDERLINED_STYLE = DEFAULT_STYLE.withUnderlined(true);
	public static final Style BOLD_BLACK_STYLE = BLACK_STYLE.withBold(true);
	public static final Style ITALIC_BLACK_STYLE = BLACK_STYLE.withItalic(true);
}
