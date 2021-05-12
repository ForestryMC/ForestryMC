package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.book.data.TextData;
import forestry.book.gui.elements.TextDataElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ContainerElement;

/**
 * A book content that displays one or more texts.
 */
@OnlyIn(Dist.CLIENT)
public class TextContent extends BookContent<TextData> {
	@Override
	public Class<? extends TextData> getDataClass() {
		return TextData.class;
	}

	@Override
	public boolean addElements(ContainerElement page, GuiElementFactory factory, @Nullable BookContent<?> previous, @Nullable GuiElement previousElement, int pageHeight) {
		if (data == null) {
			return false;
		}
		TextDataElement element;
		if (previousElement instanceof TextDataElement) {
			element = (TextDataElement) previousElement;
		} else {
			element = new TextDataElement(0, 0, page.getWidth(), 0);
			page.add(element);
		}
		element.addData(data);
		return true;
	}
}
