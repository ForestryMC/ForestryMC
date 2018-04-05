/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.gui;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiElement {
	/* Position and Size*/
	/**
	 * @return the x position of this element relative to the position of the top widget.
	 */
	int getX();

	/**
	 * @return the y position of this element relative to the position of the top widget.
	 */
	int getY();

	void setAlign(GuiElementAlignment align);

	GuiElementAlignment getAlign();

	int getAbsoluteX();

	int getAbsoluteY();

	/**
	 * @return the size of the element on the x-axis.
	 */
	int getWidth();

	/**
	 * @return the size of the element on the y-axis.
	 */
	int getHeight();

	void setSize(int width, int height);

	void setLocation(int xPos, int yPos);

	void setBounds(int xPos, int yPos, int width, int height);

	/**
	 * Sets the x-axis offset to the original x-axis position.
	 */
	void setXPosition(int xPos);

	/**
	 * Sets the y-axis offset to the original y-axis position.
	 */
	void setYPosition(int yPos);

	/* Parent */
	/**
	 * @return the element that contains this element and handles all events.
	 */
	@Nullable
	IGuiElement getParent();

	void setParent(@Nullable IGuiElement parent);

	/* Rendering */
	/**
	 * Draws the element and his children.
	 *
	 * @param mouseX The x position of the mouse relative to the parent of the element.
	 * @param mouseY The y position of the mouse relative to the parent of the element.
	 */
	void draw(int mouseX, int mouseY);

	/**
	 * Draws the element itself at the current position.
	 */
	void drawElement(int mouseX, int mouseY);

	/* Events */
	/**
	 * @param mouseX The x position of the mouse relative to the parent of the element.
	 * @param mouseY The y position of the mouse relative to the parent of the element.
	 * @param mouseButton The pressed mouse button.
	 */
	void mouseClicked(int mouseX, int mouseY, int mouseButton);

	/**
	 * @param mouseX The x position of the mouse relative to the parent of the element.
	 * @param mouseY The y position of the mouse relative to the parent of the element.
	 * @param mouseButton The pressed mouse button.
	 */
	void mouseReleased(int mouseX, int mouseY, int mouseButton);

	void mouseClickMove(int mouseX, int mouseY, int mouseButton);

	boolean keyTyped(char typedChar, int keyCode);

	/**
	 * @param mouseX The x position of the mouse relative to the parent of the element.
	 * @param mouseY The y position of the mouse relative to the parent of the element.
	 * @return True if the mouse is currently over the element.
	 */
	boolean isMouseOver(int mouseX, int mouseY);

	/**
	 * @return True if the mouse is currently over the element.
	 */
	boolean isMouseOver();

	/* Tooltip */
	IGuiElement addTooltip(String line);

	IGuiElement addTooltip(Collection<String> lines);

	void clearTooltip();

	/**
	 * Returns the tooltip that this element provides at the given mouse position.
	 *
	 * @param mouseX The x position of the mouse relative to the parent of the element.
	 * @param mouseY The y position of the mouse relative to the parent of the element.
	 */
	List<String> getTooltip(int mouseX, int mouseY);

	List<String> getTooltip();

	@Nullable
	IGuiState getGuiState();
}
