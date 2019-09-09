package forestry.api.gui;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.gui.style.ITextStyle;

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

	/**
	 * Adds a single line of text.
	 */
	ILabelElement label(String text);

	ILabelElement label(String text, ITextStyle style);

	ILabelElement label(String text, GuiElementAlignment align);

	ILabelElement label(String text, GuiElementAlignment align, ITextStyle textStyle);

	ILabelElement label(String text, int width, int height, GuiElementAlignment align, ITextStyle textStyle);

	ILabelElement label(String text, int x, int y, int width, int height, GuiElementAlignment align, ITextStyle textStyle);

	/**
	 * Adds a text element that splits the text with wordwrap.
	 */
	ITextElement splitText(String text, int width);

	ITextElement splitText(String text, int width, ITextStyle textStyle);

	ITextElement splitText(String text, int width, GuiElementAlignment align, ITextStyle textStyle);

	ITextElement splitText(String text, int x, int y, int width, GuiElementAlignment align, ITextStyle textStyle);

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
