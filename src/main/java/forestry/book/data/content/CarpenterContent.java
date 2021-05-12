package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.CarpenterElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ContainerElement;

/**
 * A book content that displays a carpenter recipe.
 */
@OnlyIn(Dist.CLIENT)
public class CarpenterContent extends BookContent<CraftingData> {

	@Nullable
	@Override
	public Class<? extends CraftingData> getDataClass() {
		return CraftingData.class;
	}

	@Override
	public boolean addElements(ContainerElement page, GuiElementFactory factory, @Nullable BookContent<?> previous, @Nullable GuiElement previousElement, int pageHeight) {
		if (data == null || (data.stack.isEmpty() && data.stacks.length == 0)) {
			return false;
		}
		if (!data.stack.isEmpty()) {
			page.add(new CarpenterElement(data.stack));
		} else {
			page.add(new CarpenterElement(data.stacks));
		}
		return true;
	}
}
