package forestry.book.gui.elements;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.gui.GuiConstants;
import forestry.api.gui.GuiElementAlignment;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.ButtonElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.elements.layouts.PaneLayout;

@OnlyIn(Dist.CLIENT)
public abstract class SelectionElement<R> extends PaneLayout {
	private static final Drawable CRAFTING_COUNT = new Drawable(GuiForesterBook.TEXTURE, 104, 181, 34, 14);
	private static final Drawable RIGHT_BUTTON = new Drawable(GuiForesterBook.TEXTURE, 138, 181, 10, 9);
	private static final Drawable LEFT_BUTTON = new Drawable(GuiForesterBook.TEXTURE, 148, 181, 10, 9);

	private int index = -1;

	@Nullable
	protected final ButtonElement leftButton;
	@Nullable
	protected final ButtonElement rightButton;
	@Nullable
	protected final ElementGroup text;
	protected final ElementGroup selectedElement;
	protected final R[] recipes;

	protected SelectionElement(int xPos, int yPos, int width, int height, R[] recipes) {
		this(xPos, yPos, width, height, recipes, 0);
	}

	protected SelectionElement(int xPos, int yPos, int width, int height, R[] recipes, int yOffset) {
		super(xPos, yPos, width, height + (recipes.length > 1 ? 16 : 0));
		this.recipes = recipes;
		if (recipes.length > 1) {
			drawable(0, 0, CRAFTING_COUNT).setAlign(GuiElementAlignment.BOTTOM_CENTER);
			text = pane(width, this.height);
			leftButton = add(new ButtonElement(-27, -2, LEFT_BUTTON, e -> setIndex(index - 1)));
			leftButton.setAlign(GuiElementAlignment.BOTTOM_CENTER);

			rightButton = add(new ButtonElement(27, -2, RIGHT_BUTTON, e -> setIndex(index + 1)));
			rightButton.setAlign(GuiElementAlignment.BOTTOM_CENTER);
		} else {
			text = null;
			leftButton = null;
			rightButton = null;
		}
		selectedElement = GuiElementFactory.INSTANCE.createPane(0, 2, width, this.height);
	}

	protected final void setIndex(int index) {
		if (index == this.index || index >= recipes.length || index < 0) {
			return;
		}
		this.index = index;
		selectedElement.clear();
		onIndexUpdate(index, recipes[index]);
		if (text != null) {
			text.clear();
			text.label((index + 1) + "/" + recipes.length, GuiElementAlignment.BOTTOM_CENTER, GuiConstants.BLACK_STYLE).setYPosition(2);
		}
		if (leftButton != null) {
			leftButton.setEnabled(index > 0);
		}
		if (rightButton != null) {
			rightButton.setEnabled(index < recipes.length - 1);
		}
	}

	protected abstract void onIndexUpdate(int index, R recipe);
}
