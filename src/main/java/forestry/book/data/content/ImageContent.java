package forestry.book.data.content;

import forestry.api.book.BookContent;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.DrawableElement;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IElementGroup;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.IGuiElementFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * A book content that displays a image.
 */
@OnlyIn(Dist.CLIENT)
public class ImageContent extends BookContent<Drawable> {
    //The size fo the gui element.
    private int width;
    private int height;
    //Centers the element between the end of the page and the last element
    private boolean center;

    @Override
    public Class<? extends Drawable> getDataClass() {
        return Drawable.class;
    }

    @Override
    public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
        if (data == null) {
            return false;
        }
        if (center) {
            page.pane(page.getWidth(), pageHeight - page.getHeight())
                    .add(new DrawableElement(0, 0, width, height, data)).setAlign(GuiElementAlignment.MIDDLE_CENTER);
        } else {
            page.add(new DrawableElement(0, 0, width, height, data)).setAlign(GuiElementAlignment.TOP_CENTER);
        }
        return true;
    }
}
