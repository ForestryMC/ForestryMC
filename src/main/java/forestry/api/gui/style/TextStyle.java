package forestry.api.gui.style;

import java.util.function.IntSupplier;

public class TextStyle implements ITextStyle {
	private IntSupplier color = () -> 0xFFFFFF;
	private boolean bold = false;
	private boolean italic = false;
	private boolean underlined = false;
	private boolean strikethrough = false;
	private boolean obfuscated = false;
	private boolean shadow = false;
	private boolean unicode = false;

	public TextStyle() {
	}

	public TextStyle(IntSupplier color, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean shadow, boolean unicode) {
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.shadow = shadow;
		this.unicode = unicode;
	}

	public TextStyle(int color, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean shadow, boolean unicode) {
		this.color = () -> color;
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.shadow = shadow;
		this.unicode = unicode;
	}

	public TextStyle setColor(int color) {
		this.color = () -> color;
		return this;
	}

	public void setColor(IntSupplier color) {
		this.color = color;
	}

	public int getColor() {
		return color.getAsInt();
	}

	public TextStyle setBold(boolean bold) {
		this.bold = bold;
		return this;
	}

	public boolean isBold() {
		return bold;
	}

	public TextStyle setObfuscated(boolean obfuscated) {
		this.obfuscated = obfuscated;
		return this;
	}

	public boolean isObfuscated() {
		return obfuscated;
	}

	public TextStyle setStrikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
		return this;
	}

	public boolean isStrikethrough() {
		return strikethrough;
	}

	public TextStyle setUnderlined(boolean underlined) {
		this.underlined = underlined;
		return this;
	}

	public boolean isUnderlined() {
		return underlined;
	}

	public TextStyle setItalic(boolean italic) {
		this.italic = italic;
		return this;
	}

	public boolean isItalic() {
		return italic;
	}

	public TextStyle setShadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}

	public boolean isShadow() {
		return shadow;
	}

	public boolean isUnicode() {
		return unicode;
	}

	public TextStyle setUnicode(boolean unicode) {
		this.unicode = unicode;
		return this;
	}
}
