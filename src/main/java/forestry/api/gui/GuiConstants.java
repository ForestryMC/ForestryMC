package forestry.api.gui;

import forestry.api.gui.style.ITextStyle;
import forestry.api.gui.style.ImmutableTextStyle;
import forestry.api.gui.style.TextStyleBuilder;

public class GuiConstants {
	public static final ITextStyle DEFAULT_STYLE = new ImmutableTextStyle();
	public static final ITextStyle BLACK_STYLE = new TextStyleBuilder().color(0x000000).build();
	public static final ITextStyle UNICODE_STYLE = new TextStyleBuilder().unicode(true).build();
	public static final ITextStyle UNDERLINED_STYLE = new TextStyleBuilder().underlined(true).build();
	public static final ITextStyle BOLD_BLACK_STYLE = new TextStyleBuilder(BLACK_STYLE).bold(true).build();
	public static final ITextStyle ITALIC_BLACK_STYLE = new TextStyleBuilder(BLACK_STYLE).italic(true).build();
}
