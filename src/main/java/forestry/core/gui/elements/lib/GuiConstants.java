package forestry.core.gui.elements.lib;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

public class GuiConstants {
    public static final Style DEFAULT_STYLE = Style.EMPTY;
    public static final Style BLACK_STYLE = DEFAULT_STYLE.setColor(Color.func_240743_a_(0x000000));
    public static final Style GRAY_STYLE = DEFAULT_STYLE.setColor(Color.func_240743_a_(0x666666));
    public static final Style UNDERLINED_STYLE = DEFAULT_STYLE.setUnderlined(true);
    public static final Style BOLD_BLACK_STYLE = BLACK_STYLE.setBold(true);
    public static final Style ITALIC_BLACK_STYLE = BLACK_STYLE.setItalic(true);
}
