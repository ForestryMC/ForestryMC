package forestry.book.data.content;

import forestry.api.book.BookContent;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.CarpenterElement;
import forestry.core.gui.elements.lib.IElementGroup;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.IGuiElementFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

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
    public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
        if (data == null || (data.stack.isEmpty() && data.stacks.length == 0)) {
            return false;
        }
        if (!data.stack.isEmpty()) {
            page.add(new CarpenterElement(0, 0, data.stack));
        } else {
            page.add(new CarpenterElement(0, 0, data.stacks));
        }
        return true;
    }
}
