package forestry.book.data.content;

import javax.annotation.Nullable;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;

public class MutationContent extends BookContent {
	public String species = "";

	@Nullable
	@Override
	public Class getDataClass() {
		return null;
	}

	@Override
	public boolean addElements(IElementGroup group, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement) {
		return false;
	}
}
