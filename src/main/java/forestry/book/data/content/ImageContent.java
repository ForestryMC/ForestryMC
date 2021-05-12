package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.Alignment;
import forestry.core.gui.elements.DrawableElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ContainerElement;

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
	public boolean addElements(ContainerElement page, GuiElementFactory factory, @Nullable BookContent<?> previous, @Nullable GuiElement previousElement, int pageHeight) {
		if (data == null) {
			return false;
		}
		if (center) {
			page.pane(page.getWidth(), pageHeight - page.getHeight())
					.add(new DrawableElement(width, height, data)).setAlign(Alignment.MIDDLE_CENTER);
		} else {
			page.add(new DrawableElement(width, height, data)).setAlign(Alignment.TOP_CENTER);
		}
		return true;
	}
}
