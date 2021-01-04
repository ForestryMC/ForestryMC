package forestry.api.book;

import forestry.core.gui.elements.lib.IElementGroup;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.IGuiElementFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
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
