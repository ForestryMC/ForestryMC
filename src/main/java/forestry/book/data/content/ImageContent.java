package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.DrawableElement;

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
