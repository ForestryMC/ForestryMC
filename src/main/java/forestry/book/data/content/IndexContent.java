package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.book.data.IndexData;
import forestry.book.gui.elements.IndexElement;

@OnlyIn(Dist.CLIENT)
public class IndexContent extends BookContent<IndexData> {
	@Nullable
	@Override
	public Class<? extends IndexData> getDataClass() {
		return IndexData.class;
	}

	@Override
	public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
		if (data == null || data.entries.length <= 0) {
			return false;
		}
		page.add(new IndexElement(0, 0, data.entries));
		return true;
	}
}
