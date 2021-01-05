package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.FabricatorElement;
import forestry.core.gui.elements.lib.IElementGroup;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.IGuiElementFactory;

@OnlyIn(Dist.CLIENT)
public class FabricatorContent extends BookContent<CraftingData> {

	@Override
	public Class<? extends CraftingData> getDataClass() {
		return CraftingData.class;
	}

	@Override
	public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
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
