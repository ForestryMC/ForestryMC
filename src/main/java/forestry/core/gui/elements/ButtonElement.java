package forestry.core.gui.elements;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import forestry.core.gui.Drawable;
import forestry.core.gui.buttons.StandardButtonTextureSets;
import forestry.core.utils.SoundUtil;

public class ButtonElement extends GuiElement {
	/* Attributes - Final */
	private final Consumer<ButtonElement> onClicked;
	private final Drawable[] textures;

	/* Attributes - State */
	private boolean enabled = true;

	public ButtonElement(Builder builder) {
		super(builder);
		Preconditions.checkNotNull(builder.onClicked, "No action was defined for this button.");
		Preconditions.checkArgument(builder.textures[0] != null && builder.textures[1] != null && builder.textures[2] != null, "The button is missing some textures.");
		this.onClicked = builder.onClicked;
		this.textures = builder.textures;
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (!enabled) {
			return false;
		}
		onPressed();
		SoundUtil.playButtonClick();
		return true;
	}

	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		// RenderSystem.enableAlphaTest();
		boolean mouseOver = isMouseOver();
		int hoverState = getTextureIndex(mouseOver);
		Drawable drawable = textures[hoverState];
		drawable.draw(transform, 0, 0);
		// RenderSystem.disableAlphaTest();
	}

	@Override
	public boolean canMouseOver() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected int getTextureIndex(boolean mouseOver) {
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

	public static class Builder extends ElementBuilder<Builder, ButtonElement> {
		private final Drawable[] textures = new Drawable[3];
		@Nullable
		private Consumer<ButtonElement> onClicked;

		public Builder() {
		}

		public Builder action(@Nullable Consumer<ButtonElement> onClicked) {
			this.onClicked = onClicked;
			return this;
		}

		public Builder textures(StandardButtonTextureSets textureSets) {
			size(textureSets.getWidth(), textureSets.getHeight());
			for (int i = 0; i < 3; i++) {
				textures[i] = new Drawable(textureSets.getTexture(), textureSets.getX(), textureSets.getY() + textureSets.getHeight() * i, textureSets.getWidth(), textureSets.getHeight());
			}
			return this;
		}

		public Builder textures(Drawable drawable) {
			size(drawable.uWidth, drawable.vHeight);
			for (int i = 0; i < 3; i++) {
				textures[i] = new Drawable(drawable.textureLocation, drawable.u, drawable.v + drawable.vHeight * i, drawable.uWidth, drawable.vHeight);
			}
			return this;
		}

		public Builder textures(Drawable disabledDrawable, Drawable enabledDrawable) {
			return textures(disabledDrawable, enabledDrawable, enabledDrawable);
		}

		public Builder textures(Drawable disabledDrawable, Drawable enabledDrawable, Drawable mouseOverDrawable) {
			size(disabledDrawable.uWidth, disabledDrawable.vHeight);
			textures[0] = disabledDrawable;
			textures[1] = enabledDrawable;
			textures[2] = mouseOverDrawable;
			return this;
		}

		@Override
		public ButtonElement create() {
			return new ButtonElement(this);
		}
	}

}
