package forestry.api.gui.style;

public interface ITextStyle {

	int getColor();

	default boolean isBold() {
		return false;
	}

	default boolean isObfuscated() {
		return false;
	}

	default boolean isStrikethrough() {
		return false;
	}

	default boolean isUnderlined() {
		return false;
	}

	default boolean isItalic() {
		return false;
	}

	default boolean isShadow() {
		return false;
	}

	default boolean isUnicode() {
		return false;
	}
}
