package forestry.core.gui.widgets;

import net.minecraft.client.renderer.GlStateManager;

import forestry.core.gui.Drawable;

public class WidgetSlider extends Widget {
	private final Drawable texture;

	private int xOffset;
	private int yOffset;

	public WidgetSlider(WidgetManager manager, int xPos, int yPos, Drawable texture) {
		super(manager, xPos, yPos);
		this.texture = texture;
		this.width = texture.uWidth;
		this.height = texture.vHeight;
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public int getXOffset() {
		return xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}

	@Override
	public void draw(int startX, int startY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		texture.draw(startX + xPos + xOffset, startY + yPos + yOffset);
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		int xPos = this.xPos + xOffset;
		int yPos = this.yPos + yOffset;
		return mouseX >= xPos && mouseX <= xPos + this.width && mouseY >= yPos && mouseY <= yPos + this.height;
	}
}
