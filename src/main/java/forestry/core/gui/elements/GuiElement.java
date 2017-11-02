package forestry.core.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Gui;

import forestry.api.core.IGuiElement;

public abstract class GuiElement extends Gui implements IGuiElement {
	protected final int xPos;
	protected final int yPos;
	protected int xOffset = 0;
	protected int yOffset = 0;
	protected int width;
	protected int height;

	public GuiElement(int xPos, int yPos, int width, int height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getX() {
		return xPos + xOffset;
	}

	@Override
	public int getY() {
		return yPos + yOffset;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	@Override
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		int xPos = getX();
		int yPos = getY();
		return mouseX >= xPos && mouseX <= xPos + getWidth() && mouseY >= yPos && mouseY <= yPos + getHeight();
	}

	@Nullable
	@Override
	public IGuiElement getParent() {
		return null;
	}

	@Override
	public void setParent(@Nullable IGuiElement parent) {

	}
}
