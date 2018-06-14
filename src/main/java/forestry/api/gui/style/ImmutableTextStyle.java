package forestry.api.gui.style;

import java.util.function.IntSupplier;

public final class ImmutableTextStyle implements ITextStyle {
	private final IntSupplier color;
	private final boolean bold;
	private final boolean italic;
	private final boolean underlined;
	private final boolean strikethrough;
	private final boolean obfuscated;
	private final boolean shadow;
	private final boolean unicode;

	public ImmutableTextStyle() {
		this(() -> 0xFFFFFF, false, false, false, false, false, false, false);
	}

	public ImmutableTextStyle(int color, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean shadow, boolean unicode) {
		this(() -> color, bold, italic, underlined, strikethrough, obfuscated, shadow, unicode);
	}

	public ImmutableTextStyle(IntSupplier color, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean shadow, boolean unicode) {
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.shadow = shadow;
		this.unicode = unicode;
	}

	public int getColor() {
		return color.getAsInt();
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isObfuscated() {
		return obfuscated;
	}

	public boolean isStrikethrough() {
		return strikethrough;
	}

	public boolean isUnderlined() {
		return underlined;
	}

	public boolean isItalic() {
		return italic;
	}

	public boolean isShadow() {
		return shadow;
	}

	public boolean isUnicode() {
		return unicode;
	}
}
