package forestry.core.gui.elements.layouts;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.*;
import forestry.core.gui.elements.lib.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ElementGroup extends GuiElement implements IElementGroup {
    protected final List<IGuiElement> elements = new ArrayList<>();

    public ElementGroup(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
    }

    public <E extends IGuiElement> E add(E element) {
        elements.add(element);
        element.setParent(this);
        element.onCreation();
        return element;
    }

    public <E extends IGuiElement> E remove(E element) {
        elements.remove(element);
        element.onDeletion();
        return element;
    }

    public void clear() {
        for (IGuiElement element : new ArrayList<>(elements)) {
            remove(element);
        }
    }

    @Override
    public List<IGuiElement> getElements() {
        return elements;
    }

    @Override
    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        int mX = mouseX - getX();
        int mY = mouseY - getY();
        elements.forEach(element -> element.draw(transform, mY, mX));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateClient() {
        if (!isVisible()) {
            return;
        }
        onUpdateClient();
        for (IGuiElement widget : getElements()) {
            widget.updateClient();
        }
    }

    @Nullable
    @Override
    public IGuiElement getLastElement() {
        return elements.isEmpty() ? null : elements.get(elements.size() - 1);
    }

    public DrawableElement drawable(Drawable drawable) {
        return add(new DrawableElement(drawable));
    }

    public DrawableElement drawable(int x, int y, Drawable drawable) {
        return add(new DrawableElement(x, y, drawable));
    }

    @Override
    public IItemElement item(int xPos, int yPos, ItemStack itemStack) {
        IItemElement element = new ItemElement(xPos, yPos, itemStack);
        add(element);
        return element;
    }

    @Override
    public Style defaultStyle() {
        return GuiConstants.DEFAULT_STYLE;
    }

    @Override
    public ILabelElement translated(String key, Object... args) {
        return label(new TranslationTextComponent(key, args));
    }

    public LabelElement.Builder labelLine(IFormattableTextComponent component) {
        return new LabelElement.Builder(this::add, component);
    }

    public LabelElement.Builder translatedLine(String key, Object... args) {
        return labelLine(new TranslationTextComponent(key, args));
    }

    @Override
    public LabelElement.Builder labelLine(String text) {
        return labelLine(new StringTextComponent(text));
    }

    @Override
    public ILabelElement label(ITextComponent text) {
        return label(text, defaultStyle());
    }

    @Override
    public ILabelElement label(ITextComponent text, Style style) {
        return label(text, GuiElementAlignment.TOP_LEFT, style);
    }

    @Override
    public ILabelElement label(ITextComponent text, GuiElementAlignment align) {
        return label(text, align, defaultStyle());
    }

    @Override
    public ILabelElement label(ITextComponent text, GuiElementAlignment align, Style textStyle) {
        return label(text, -1, 12, align, textStyle);
    }

    @Override
    public ILabelElement label(ITextComponent text, int width, int height, GuiElementAlignment align, Style textStyle) {
        return label(text, 0, 0, width, height < 0 ? 12 : height, align, textStyle);
    }

    @Override
    public ILabelElement label(
            ITextComponent text,
            int x,
            int y,
            int width,
            int height,
            GuiElementAlignment align,
            Style textStyle
    ) {
        return add(
                (ILabelElement) new LabelElement(x, y, width, height, text, true).setStyle(textStyle).setAlign(align)
        );
    }

    @Override
    public ITextElement splitText(IFormattableTextComponent text, int width) {
        return splitText(text, width, defaultStyle());
    }

    @Override
    public ITextElement splitText(IFormattableTextComponent text, int width, Style textStyle) {
        return splitText(text, width, GuiElementAlignment.TOP_LEFT, textStyle);
    }

    @Override
    public ITextElement splitText(
            IFormattableTextComponent text,
            int width,
            GuiElementAlignment align,
            Style textStyle
    ) {
        return splitText(text, 0, 0, width, align, textStyle);
    }

    @Override
    public ITextElement splitText(
            IFormattableTextComponent text,
            int x,
            int y,
            int width,
            GuiElementAlignment align,
            Style textStyle
    ) {
        return (ITextElement) add(new SplitTextElement(x, y, width, text, textStyle)).setAlign(align);
    }

    @Override
    public AbstractElementLayout vertical(int xPos, int yPos, int width) {
        return add(new VerticalLayout(xPos, yPos, width));
    }

    @Override
    public AbstractElementLayout vertical(int width) {
        return add(new VerticalLayout(0, 0, width));
    }

    @Override
    public AbstractElementLayout horizontal(int xPos, int yPos, int height) {
        return add(new HorizontalLayout(xPos, yPos, height));
    }

    @Override
    public AbstractElementLayout horizontal(int height) {
        return add(new HorizontalLayout(0, 0, height));
    }

    @Override
    public ElementGroup pane(int xPos, int yPos, int width, int height) {
        return add(new PaneLayout(xPos, yPos, width, height));
    }

    @Override
    public ElementGroup pane(int width, int height) {
        return add(new PaneLayout(0, 0, width, height));
    }

    @Override
    public ElementLayoutHelper layoutHelper(IElementLayoutHelper.LayoutFactory layoutFactory, int width, int height) {
        return new ElementLayoutHelper(layoutFactory, width, height, this);
    }
}
