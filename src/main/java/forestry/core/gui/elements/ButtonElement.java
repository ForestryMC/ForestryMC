package forestry.core.gui.elements;

import java.util.function.Consumer;

import forestry.core.gui.Drawable;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.utils.SoundUtil;

public class ButtonElement extends GuiElement {
	/* Attributes - Final */
	private final Consumer<ButtonElement> onClicked;
	private final Drawable[] textures = new Drawable[3];

	/* Attributes - State */
	private boolean enabled = true;

	public ButtonElement(int xPos, int yPos, Drawable drawable, Consumer<ButtonElement> onClicked) {
		super(xPos, yPos, drawable.uWidth, drawable.vHeight);
		this.onClicked = onClicked;
		for (int i = 0; i < 3; i++) {
			textures[i] = new Drawable(drawable.textureLocation, drawable.u, drawable.v + drawable.vHeight * i, drawable.uWidth, drawable.vHeight);
		}
	}

	public ButtonElement(int xPos, int yPos, StandardButtonTextureSets textureSets, Consumer<ButtonElement> onClicked) {
		super(xPos, yPos, textureSets.getWidth(), textureSets.getHeight());
		this.onClicked = onClicked;
		for (int i = 0; i < 3; i++) {
			textures[i] = new Drawable(textureSets.getTexture(), textureSets.getX(), textureSets.getY() + textureSets.getHeight() * i, textureSets.getWidth(), textureSets.getHeight());
		}
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		boolean mouseOver = isMouseOver(mouseX, mouseY);
		int hoverState = getHoverState(mouseOver);
		Drawable drawable = textures[hoverState];
		drawable.draw(0, 0);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected int getHoverState(boolean mouseOver) {
		int i = 1;

		if (!this.enabled) {
			i = 0;
		} else if (mouseOver) {
			i = 2;
		}

		return i;
	}


	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseEvent) {
		if (!enabled) {
			return;
		}
		onPressed();
		SoundUtil.playButtonClick();
	}

	public void onPressed() {
		onClicked.accept(this);
	}

}
