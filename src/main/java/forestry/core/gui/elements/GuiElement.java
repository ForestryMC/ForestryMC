package forestry.core.gui.elements;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiState;

public class GuiElement extends Gui implements IGuiElement {
	/* Attributes - Final */
	//Element Position
	protected int xPos;
	protected int yPos;
	//Tooltip of the element
	protected final List<String> tooltip = new ArrayList<>();
	/* Attributes - State*/
	//Size of this element
	protected int width;
	protected int height;
	protected GuiElementAlignment align = GuiElementAlignment.TOP_LEFT;
	//The element container that contains this element
	@Nullable
	protected IGuiElement parent;
	@Nullable
	private IGuiState state;

	public GuiElement(int width, int height) {
		this(0, 0, width, height);
	}

	public GuiElement(int xPos, int yPos, int width, int height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getX() {
		int x = 0;
		if (parent != null && parent.getWidth() > width) {
			x = (int) ((parent.getWidth() - width) * align.getXOffset());
		}
		return xPos + x;
	}

	@Override
	public int getY() {
		int y = 0;
		if (parent != null && parent.getHeight() > height) {
			y = (int) ((parent.getHeight() - height) * align.getYOffset());
		}
		return yPos + y;
	}

	public final int getAbsoluteX() {
		return parent == null ? getX() : getX() + parent.getAbsoluteX();
	}

	public final int getAbsoluteY() {
		return parent == null ? getY() : getY() + parent.getAbsoluteY();
	}

	@Override
	public void draw(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(getX(), getY(), 0.0F);

		drawElement(mouseX, mouseY);

		GlStateManager.popMatrix();
	}

	public void drawElement(int mouseX, int mouseY) {
		//
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public boolean keyTyped(char typedChar, int keyCode) {
		return false;
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
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setXPosition(int xPos) {
		this.xPos = xPos;
	}

	@Override
	public void setYPosition(int yPos) {
		this.yPos = yPos;
	}

	@Override
	public void setLocation(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	@Override
	public void setBounds(int xPos, int yPos, int width, int height) {
		setLocation(xPos, yPos);
		setSize(width, height);
	}

	@Override
	public void setAlign(GuiElementAlignment align) {
		this.align = align;
	}

	@Override
	public GuiElementAlignment getAlign() {
		return align;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		int x = getX();
		int y = getY();
		return mouseX >= x && mouseX <= getWidth() + x && mouseY >= y && mouseY <= getHeight() + y;
	}

	public final boolean isMouseOver() {
		IGuiState guiState = getGuiState();
		if (guiState == null) {
			return false;
		}
		int mouseX;
		int mouseY;
		if (parent != null) {
			mouseX = guiState.getRelativeMouseX(parent);
			mouseY = guiState.getRelativeMouseY(parent);
		} else {
			mouseX = guiState.getMouseX();
			mouseY = guiState.getMouseY();
		}
		return isMouseOver(mouseX, mouseY);
	}

	@Nullable
	@Override
	public IGuiElement getParent() {
		return parent;
	}

	@Override
	public void setParent(@Nullable IGuiElement parent) {
		this.parent = parent;
	}

	@Override
	public List<String> getTooltip(int mouseX, int mouseY) {
		return tooltip;
	}

	@Override
	public IGuiElement addTooltip(String line) {
		tooltip.add(line);
		return this;
	}

	@Override
	public IGuiElement addTooltip(Collection<String> lines) {
		tooltip.addAll(lines);
		return this;
	}

	@Override
	public void clearTooltip() {
		tooltip.clear();
	}

	@Override
	public List<String> getTooltip() {
		return tooltip;
	}

	@Nullable
	@Override
	public IGuiState getGuiState() {
		if (state == null && parent != null) {
			state = parent.getGuiState();
		}
		return state;
	}

	public void setGuiState(@Nullable IGuiState state) {
		this.state = state;
	}
}
