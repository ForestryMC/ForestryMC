package forestry.book.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

import forestry.api.gui.GuiElementAlignment;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.ButtonElement;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.elements.layouts.PaneLayout;

public abstract class SelectionElement extends PaneLayout {
	private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas.png");
	private static final Drawable CRAFTING_COUNT = new Drawable(BOOK_TEXTURE, 104, 181, 34, 14);
	private static final Drawable RIGHT_BUTTON = new Drawable(BOOK_TEXTURE, 138, 181, 10, 9);
	private static final Drawable LEFT_BUTTON = new Drawable(BOOK_TEXTURE, 148, 181, 10, 9);

	protected int index;

	@Nullable
	protected final ButtonElement leftButton;
	@Nullable
	protected final ButtonElement rightButton;
	@Nullable
	protected final ElementGroup text;

	public SelectionElement(int xPos, int yPos, int width, int height, boolean addSelection) {
		super(xPos, yPos, width, height);
		if (addSelection) {
			drawable(0, 0, CRAFTING_COUNT).setAlign(GuiElementAlignment.BOTTOM_CENTER);
			text = panel(width, height);
			leftButton = add(new ButtonElement(-27, -2, LEFT_BUTTON, e -> updateIndex(index - 1)));
			leftButton.setAlign(GuiElementAlignment.BOTTOM_CENTER);

			rightButton = add(new ButtonElement(27, -2, RIGHT_BUTTON, e -> updateIndex(index + 1)));
			rightButton.setAlign(GuiElementAlignment.BOTTOM_CENTER);
		} else {
			text = null;
			leftButton = null;
			rightButton = null;
		}
	}

	protected abstract void updateIndex(int index);
}
