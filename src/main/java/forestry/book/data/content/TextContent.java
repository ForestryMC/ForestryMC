package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.book.data.TextData;
import forestry.book.gui.elements.TextDataElement;

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
	public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
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
