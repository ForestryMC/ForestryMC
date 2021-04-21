package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.book.data.IndexData;
import forestry.book.gui.elements.IndexElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ElementGroup;

@OnlyIn(Dist.CLIENT)
public class IndexContent extends BookContent<IndexData> {
	@Nullable
	@Override
	public Class<? extends IndexData> getDataClass() {
		return IndexData.class;
	}

	@Override
	public boolean addElements(ElementGroup page, GuiElementFactory factory, @Nullable BookContent<?> previous, @Nullable GuiElement previousElement, int pageHeight) {
		if (data == null || data.entries.length <= 0) {
			return false;
		}
		page.add(new IndexElement(0, 0, data.entries));
		return true;
	}
}
