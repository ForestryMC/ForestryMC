package forestry.book.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.api.book.IBookEntry;
import forestry.api.book.IBookPageFactory;
import forestry.api.gui.IGuiElement;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.VerticalLayout;

@OnlyIn(Dist.CLIENT)
public class JsonPageFactory implements IBookPageFactory {

	public static final String NAME = "json";
	public static final JsonPageFactory INSTANCE = new JsonPageFactory();

	private JsonPageFactory() {
	}

	@Override
	public Collection<IGuiElement> load(IBookEntry entry, int leftPageHeight, int rightPageHeight, int pageWidth) {
		List<IGuiElement> pages = new ArrayList<>();
		BookContent previous = null;
		IGuiElement previousElement = null;

		for (BookContent[] contentArray : entry.getContent()) {
			VerticalLayout page = new VerticalLayout(108);
			pages.add(page);
			for (BookContent content : contentArray) {
				if (content.addElements(page, GuiElementFactory.INSTANCE, previous, previousElement, GuiForesterBook.PAGE_HEIGHT - (pages.size() % 2 == 1 ? 13 : 0))) {
					previous = content;
					previousElement = page.getLastElement();
					page.layout();
				} else {
					previous = null;
					previousElement = null;
				}
			}
			previous = null;
			previousElement = null;
		}
		return pages;
	}
}
