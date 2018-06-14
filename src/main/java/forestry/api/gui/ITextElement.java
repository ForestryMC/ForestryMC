package forestry.api.gui;

import java.util.Collection;
import java.util.Map;

import forestry.api.gui.style.ITextStyle;

/**
 * A element that displays or contains one ore more lines of text.
 */
public interface ITextElement extends IGuiElement {

	/**
	 * @return The text this element displays.
	 */
	Collection<String> getLines();

	ITextElement setText(String text);

	/**
	 * @return The raw text this element displays without any formations and their style.
	 */
	Map<ITextStyle, String> getRawLines();
}
