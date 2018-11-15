/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.gui;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiScreen;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.events.GuiElementEvent;
import forestry.api.gui.events.GuiEventDestination;
import forestry.api.gui.events.GuiEventHandler;
import forestry.api.gui.events.GuiEventOrigin;

/**
 * A <em>gui element</em> is an object having a graphical representation that can be displayed on the screen and that
 * can interact with the user.
 * <p>
 * The interface the most gui elements of forestry implement. You must place the element a containment hierarchy whose
 * root is a top-level {@link IWindowElement}.
 */
@SideOnly(Side.CLIENT)
public interface IGuiElement extends ICroppable {
	/* Position and Size*/

	/**
	 * @return the x position of this element relative to the position of its parent.
	 */
	int getX();

	/**
	 * @return the y position of this element relative to the position of its parent.
	 */
	int getY();

	/**
	 * Sets the alignment of this element.
	 */
	IGuiElement setAlign(GuiElementAlignment align);

	/**
	 * The alignment of the {@link IGuiElement} defines the position of the element relative to the position of its
	 * parent.
	 */
	GuiElementAlignment getAlign();

	/**
	 * The root of the containment hierarchy that this element is part of.
	 */
	IWindowElement getWindow();

	/**
	 * @return the x position of this element relative to the gui.
	 */
	int getAbsoluteX();

	/**
	 * @return the y position of this element relative to the gui.
	 */
	int getAbsoluteY();

	/**
	 * @return the size of the element on the x-axis.
	 */
	int getWidth();

	void setWidth(int width);

	/**
	 * @return the size of the element on the y-axis.
	 */
	int getHeight();

	void setHeight(int height);

	/**
	 * Sets the dimensions of this element.
	 *
	 * @param width  the size of the element on the x-axis.
	 * @param height the size of the element on the y-axis.
	 */
	IGuiElement setSize(int width, int height);

	/**
	 * Sets the position of this element.
	 *
	 * @param xPos the x position of this element relative to the position of its parent.
	 * @param yPos the y position of this element relative to the position of its parent.
	 */
	IGuiElement setLocation(int xPos, int yPos);

	IGuiElement setOffset(int xOffset, int yOffset);

	/**
	 * Sets the dimensions and position of this element.
	 *
	 * @param xPos   the x position of this element relative to the position of its parent.
	 * @param yPos   the y position of this element relative to the position of its parent.
	 * @param width  the size of the element on the x-axis.
	 * @param height the size of the element on the y-axis.
	 */
	IGuiElement setBounds(int xPos, int yPos, int width, int height);

	/**
	 * Sets the x position of this element relative to the position of its parent.
	 */
	void setXPosition(int xPos);

	/**
	 * Sets the y position of this element relative to the position of its parent.
	 */
	void setYPosition(int yPos);

	/* Parent */

	/**
	 * The position of this element is relative to the position of its parent.
	 *
	 * @return the parent element of this element.
	 */
	@Nullable
	IGuiElement getParent();

	/**
	 * Sets the parent of this element.
	 */
	IGuiElement setParent(@Nullable IGuiElement parent);

	/* Creation & Deletion */

	/**
	 * Called at {@link IElementGroup#add(IGuiElement)} after the element was added to the group and
	 * {@link #setParent(IGuiElement)} was called at the element.
	 * <p>
	 * Can be used to add other element to the element if the element is an {@link IElementGroup}.
	 */
	void onCreation();

	/**
	 * Called at {@link IElementGroup#remove(IGuiElement...)} after the element was removed from the group.
	 */
	void onDeletion();

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
	 *
	 * @param mouseX The x position of the mouse relative to the parent of the element.
	 * @param mouseY The y position of the mouse relative to the parent of the element.
	 */
	void drawElement(int mouseX, int mouseY);

	/* Mouse Over */

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

	default boolean canMouseOver() {
		return hasTooltip();
	}

	/* Updates */

	/**
	 * Updates the element. Called at {@link GuiScreen#updateScreen()}.
	 */
	@SideOnly(Side.CLIENT)
	void updateClient();

	/* State */

	/**
	 * @return True if this element can be focused and processes keys.
	 */
	default boolean canFocus() {
		return false;
	}

	/**
	 * The element can be hided with {@link #hide()} and be made visible again with {@link #show()}.
	 *
	 * @return True of this element is currently visible.
	 */
	boolean isVisible();

	/**
	 * Makes this element visible again.
	 */
	void show();

	/**
	 * Hides this element.
	 */
	void hide();

	/**
	 * The most elements are enabled by default. Only a few elements are disabled at a certain time like buttons.
	 *
	 * @return True if this element is enabled.
	 */
	boolean isEnabled();

	/* Tooltip */

	/**
	 * Adds an additional tooltip to the current tooltip of the element.
	 */
	IGuiElement addTooltip(String line);

	/**
	 * Adds an additional tooltip to the current tooltip of the element.
	 */
	IGuiElement addTooltip(Collection<String> lines);

	IGuiElement addTooltip(ITooltipSupplier supplier);

	/**
	 * @return True if this element currently has a tooltip.
	 */
	boolean hasTooltip();

	/**
	 * Clears the tooltips that were added with {@link #addTooltip(String)} and {@link #addTooltip(Collection)}.
	 * It does not remove default tooltips of an element like the fluid information of a tank or the item information
	 * of an slot element.
	 */
	void clearTooltip();

	/**
	 * Returns the tooltip that this element provides at the given mouse position.
	 *
	 * @param mouseX The x position of the mouse relative to the parent of the element.
	 * @param mouseY The y position of the mouse relative to the parent of the element.
	 */
	List<String> getTooltip(int mouseX, int mouseY);

	/**
	 * @return Returns the tooltips that were added with {@link #addTooltip(String)} and {@link #addTooltip(Collection)}.
	 */
	List<String> getTooltip();

	/* Events */

	/**
	 * Adds an event handler that handles events that this element receives with {@link #receiveEvent(GuiElementEvent)}.
	 */
	<E extends GuiElementEvent> void addEventHandler(Consumer<E> eventHandler);

	/**
	 * Adds an event handler that handles events that this element receives with {@link #receiveEvent(GuiElementEvent)}.
	 */
	default <E extends GuiElementEvent> void addEventHandler(Class<? super E> eventClass, Consumer<E> eventHandler) {
		addEventHandler(new GuiEventHandler<>(eventClass, eventHandler));
	}

	/**
	 * Adds an event handler that handles events that this element receives with {@link #receiveEvent(GuiElementEvent)}.
	 */
	default <E extends GuiElementEvent> void addEventHandler(Class<? super E> eventClass, GuiEventOrigin origin, IGuiElement relative, Consumer<E> eventHandler) {
		addEventHandler(new GuiEventHandler<>(eventClass, origin, relative, eventHandler));
	}

	/**
	 * Adds an event handler that handles events that this element receives with {@link #receiveEvent(GuiElementEvent)}.
	 */
	default <E extends GuiElementEvent> void addSelfEventHandler(Class<? super E> eventClass, Consumer<E> eventHandler) {
		addEventHandler(new GuiEventHandler<>(eventClass, GuiEventOrigin.SELF, this, eventHandler));
	}

	/**
	 * Distributes the event to the elements that are defined by the {@link GuiEventDestination}.
	 */
	default void postEvent(GuiElementEvent event, GuiEventDestination destination) {
		destination.sendEvent(this, event);
	}

	default void postEvent(GuiElementEvent event) {
		postEvent(event, GuiEventDestination.SINGLE);
	}

	/**
	 * Receives an event and distributes them to the event handlers of this element.
	 */
	void receiveEvent(GuiElementEvent event);
}
