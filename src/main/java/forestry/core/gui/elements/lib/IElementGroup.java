package forestry.core.gui.elements.lib;

import forestry.core.gui.elements.LabelElement;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface IElementGroup extends IGuiElement {
    /**
     * Adds a element to this layout.
     */
    <E extends IGuiElement> E add(E element);

    /**
     * Removes a element from this layout.
     */
    <E extends IGuiElement> E remove(E element);

    default IElementGroup add(IGuiElement... elements) {
        for (IGuiElement element : elements) {
            add(element);
        }
        return this;
    }

    default IElementGroup remove(IGuiElement... elements) {
        for (IGuiElement element : elements) {
            remove(element);
        }
        return this;
    }

    default IElementGroup add(Collection<IGuiElement> elements) {
        elements.forEach(this::add);
        return this;
    }

    default IElementGroup remove(Collection<IGuiElement> elements) {
        elements.forEach(this::remove);
        return this;
    }

    void clear();

    @Nullable
    IGuiElement getLastElement();

    /**
     * @return All elements that this layout contains.
     */
    List<IGuiElement> getElements();

    IItemElement item(int xPos, int yPos, ItemStack itemStack);

    default IItemElement item(ItemStack itemStack) {
        return item(0, 0, itemStack);
    }

    Style defaultStyle();

    ILabelElement label(ITextComponent component);

    ILabelElement translated(String key, Object... args);

    LabelElement.Builder labelLine(IFormattableTextComponent component);

    LabelElement.Builder translatedLine(String key, Object... args);

    LabelElement.Builder labelLine(String text);

    /**
     * Adds a single line of text.
     */
    ILabelElement label(ITextComponent text, Style style);

    ILabelElement label(ITextComponent text, GuiElementAlignment align);

    ILabelElement label(ITextComponent text, GuiElementAlignment align, Style textStyle);

    ILabelElement label(ITextComponent text, int width, int height, GuiElementAlignment align, Style textStyle);

    ILabelElement label(
            ITextComponent text,
            int x,
            int y,
            int width,
            int height,
            GuiElementAlignment align,
            Style textStyle
    );

    /**
     * Adds a text element that splits the text with wordwrap.
     */
    ITextElement splitText(IFormattableTextComponent text, int width);

    ITextElement splitText(IFormattableTextComponent text, int width, Style textStyle);

    ITextElement splitText(IFormattableTextComponent text, int width, GuiElementAlignment align, Style textStyle);

    ITextElement splitText(
            IFormattableTextComponent text,
            int x,
            int y,
            int width,
            GuiElementAlignment align,
            Style textStyle
    );

    default IElementLayout vertical(int width) {
        return vertical(0, 0, width);
    }

    IElementLayout vertical(int xPos, int yPos, int width);

    IElementLayout horizontal(int xPos, int yPos, int height);

    default IElementLayout horizontal(int height) {
        return horizontal(0, 0, height);
    }

    IElementGroup pane(int xPos, int yPos, int width, int height);

    default IElementGroup pane(int width, int height) {
        return pane(0, 0, width, height);
    }

    IElementLayoutHelper layoutHelper(IElementLayoutHelper.LayoutFactory layoutFactory, int width, int height);
}
