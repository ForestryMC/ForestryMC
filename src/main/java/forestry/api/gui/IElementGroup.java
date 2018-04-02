package forestry.api.gui;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;

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

	IGuiElement item(int xPos, int yPos, ItemStack itemStack);

	default IGuiElement item(ItemStack itemStack) {
		return item(0, 0, itemStack);
	}

	/**
	 * Adds a text element with the default color,the align {@link GuiElementAlignment#TOP_LEFT} and the height 12.
	 */
	IGuiElement text(String text);

	/**
	 * Adds a text element with the align {@link GuiElementAlignment#TOP_LEFT} and the height 12.
	 */
	IGuiElement text(String text, int color);

	/**
	 * Adds a text element with the default color and the height 12.
	 */
	IGuiElement text(String text, GuiElementAlignment align);

	IGuiElement text(String text, GuiElementAlignment align, int color, boolean unicode);

	/**
	 * Adds a text element with the height 12.
	 */
	IGuiElement text(String text, GuiElementAlignment align, int color);

	IGuiElement text(int x, String text, GuiElementAlignment align, int color);

	/**
	 * Adds a text element.
	 */
	IGuiElement text(int x, int height, String text, GuiElementAlignment align, int color);

	IGuiElement text(int x, int y, int width, int height, String text);

	/**
	 * Adds a text element.
	 */
	IGuiElement text(int x, int height, String text, GuiElementAlignment align, int color, boolean unicode);

	default IElementLayout vertical(int width) {
		return vertical(0, 0, width);
	}

	IElementLayout vertical(int xPos, int yPos, int width);

	IElementLayout horizontal(int xPos, int yPos, int height);

	default IElementLayout horizontal(int height) {
		return horizontal(0, 0, height);
	}

	IElementGroup panel(int xPos, int yPos, int width, int height);

	default IElementGroup panel(int width, int height) {
		return panel(0, 0, width, height);
	}

	IElementLayoutHelper layoutHelper(IElementLayoutHelper.LayoutFactory layoutFactory, int width, int height);
}
