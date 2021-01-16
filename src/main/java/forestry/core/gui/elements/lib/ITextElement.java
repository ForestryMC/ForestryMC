package forestry.core.gui.elements.lib;

import java.util.Collection;

import net.minecraft.util.text.ITextComponent;

/**
 * A element that displays or contains one ore more lines of text.
 */
public interface ITextElement extends IGuiElement {

	/**
	 * @return The text this element displays.
	 */
	Collection<ITextComponent> getLines();

	ITextElement setText(ITextComponent text);
}
