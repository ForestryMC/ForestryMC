package forestry.api.gui;

import forestry.api.gui.style.ITextStyle;

/**
 * A label element that displays and contains one single line of text
 */
public interface ILabelElement extends ITextElement {
	/**
	 * @return The style of the text.
	 */
	ITextStyle getStyle();

	ILabelElement setStyle(ITextStyle style);

	/**
	 * @return The current text of this element with its {@link net.minecraft.util.text.TextFormatting}s.
	 */
	String getText();

	ILabelElement setText(String text);

	/**
	 * The current text of this element without its {@link net.minecraft.util.text.TextFormatting}s.
	 */
	String getRawText();
}
