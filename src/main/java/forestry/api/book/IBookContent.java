package forestry.api.book;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;

@SideOnly(Side.CLIENT)
public interface IBookContent {
	/**
	 * Called after the deserialization.
	 */
	default void onDeserialization() {
	}

	/**
	 * Adds the content to the page by adding a {@link IGuiElement} or at the content to the previous element.
	 *
	 * @param page            The gui element that represents a book page
	 * @param previous        The content of the previous element.
	 * @param previousElement The element that was previously added to the page.
	 * @param pageHeight      The max height of the current page.
	 * @return True if you added an element.
	 */
	boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight);
}
