package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.FabricatorElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ElementGroup;

@OnlyIn(Dist.CLIENT)
public class FabricatorContent extends BookContent<CraftingData> {

	@Override
	public Class<? extends CraftingData> getDataClass() {
		return CraftingData.class;
	}

	@Override
	public boolean addElements(ElementGroup page, GuiElementFactory factory, @Nullable BookContent<?> previous, @Nullable GuiElement previousElement, int pageHeight) {
		if (data == null || (data.stack.isEmpty() && data.stacks.length == 0)) {
			return false;
		}
		if (!data.stack.isEmpty()) {
			page.add(new FabricatorElement(0, 0, data.stack));
		} else {
			page.add(new FabricatorElement(0, 0, data.stacks));
		}
		return true;
	}
}
