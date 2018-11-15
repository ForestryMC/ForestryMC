package forestry.core.gui.elements;

import java.util.function.Consumer;

import net.minecraft.client.renderer.GlStateManager;

import forestry.api.gui.events.GuiEvent;
import forestry.core.gui.Drawable;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.utils.SoundUtil;

public class ButtonElement extends GuiElement {
	/* Attributes - Final */
	private final Consumer<ButtonElement> onClicked;
	private final Drawable[] textures = new Drawable[3];

	/* Attributes - State */
	private boolean enabled = true;

	public ButtonElement(int xPos, int yPos, int width, int height, Drawable disabledDrawable, Drawable enabledDrawable, Consumer<ButtonElement> onClicked) {
		this(xPos, yPos, width, height, disabledDrawable, enabledDrawable, enabledDrawable, onClicked);
	}

	public ButtonElement(int xPos, int yPos, int width, int height, Drawable disabledDrawable, Drawable enabledDrawable, Drawable mouseOverDrawable, Consumer<ButtonElement> onClicked) {
		super(xPos, yPos, width, height);
		this.onClicked = onClicked;
		textures[0] = disabledDrawable;
		textures[1] = enabledDrawable;
		textures[2] = mouseOverDrawable;
		addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			if (!enabled) {
				return;
			}
			onPressed();
			SoundUtil.playButtonClick();
		});
	}

	public ButtonElement(int xPos, int yPos, Drawable drawable, Consumer<ButtonElement> onClicked) {
		super(xPos, yPos, drawable.uWidth, drawable.vHeight);
		this.onClicked = onClicked;
		for (int i = 0; i < 3; i++) {
			textures[i] = new Drawable(drawable.textureLocation, drawable.u, drawable.v + drawable.vHeight * i, drawable.uWidth, drawable.vHeight);
		}
		addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			if (!enabled) {
				return;
			}
			onPressed();
			SoundUtil.playButtonClick();
		});
	}

	public ButtonElement(int xPos, int yPos, StandardButtonTextureSets textureSets, Consumer<ButtonElement> onClicked) {
		super(xPos, yPos, textureSets.getWidth(), textureSets.getHeight());
		this.onClicked = onClicked;
		for (int i = 0; i < 3; i++) {
			textures[i] = new Drawable(textureSets.getTexture(), textureSets.getX(), textureSets.getY() + textureSets.getHeight() * i, textureSets.getWidth(), textureSets.getHeight());
		}
		addSelfEventHandler(GuiEvent.DownEvent.class, event -> {
			if (!enabled) {
				return;
			}
			onPressed();
			SoundUtil.playButtonClick();
		});
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		GlStateManager.enableAlpha();
		boolean mouseOver = isMouseOver();
		int hoverState = getHoverState(mouseOver);
		Drawable drawable = textures[hoverState];
		drawable.draw(0, 0);
		GlStateManager.disableAlpha();
	}

	@Override
	public boolean canMouseOver() {
		return true;
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

	public void onPressed() {
		onClicked.accept(this);
	}

}
