package forestry.core.gui.elements;

import javax.annotation.Nullable;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.elements.layouts.ContainerElement;

/**
 * This element is the top parent.
 */
@OnlyIn(Dist.CLIENT)
public abstract class Window extends ContainerElement {
	@Nullable
	protected Minecraft mc = null;
	//The last x position of the mouse
	protected int mouseX = -1;
	//The last y position of the mouse
	protected int mouseY = -1;
	@Nullable
	protected GuiElement mousedOver;
	@Nullable
	protected GuiElement dragged;
	@Nullable
	protected GuiElement focused;
	/**
	 * Collections with contains all elements that the mouse is over since the last mouse update.
	 */
	protected Collection<GuiElement> hoverElements = Collections.emptyList();

	public Window(int width, int height) {
		//setSize(width, height);
		//setBounds(0, 0, width, height);
		setAssignedBounds(new Rectangle(0, 0, width, height));
	}

	public void onRemove(GuiElement element) {
		if (isMouseOver(element)) {
			setMousedOver(null);
		}
		if (isDragged(element)) {
			setDragged(null);
		}
		if (isFocused(element)) {
			setFocused(null);
		}
	}

	@Override
	public Window getWindow() {
		return this;
	}

	@Override
	public GuiElement setParent(@Nullable GuiElement parent) {
		return this;
	}

	public void init(int guiLeft, int guiTop) {
		setAssignedBounds(new Rectangle(guiLeft, guiTop, bounds.width, bounds.height));
	}

	/* Element Events */
	@Nullable
	public GuiElement getDragged() {
		return this.dragged;
	}

	public void setDragged(@Nullable GuiElement widget) {
		this.setDraggedElement(widget, -1);
	}

	public void setDraggedElement(@Nullable GuiElement widget, int button) {
		if (this.dragged == widget) {
			return;
		}
		if (this.dragged != null) {
			dragged.onDragEnd(mouseX, mouseY);
		}
		this.dragged = widget;
		if (this.dragged != null) {
			dragged.onDragStart(mouseX, mouseY);
		}
	}

	@Nullable
	public GuiElement getMousedOver() {
		return this.mousedOver;
	}

	public void setMousedOver(@Nullable GuiElement widget) {
		if (this.mousedOver == widget) {
			return;
		}
		if (this.mousedOver != null) {
			mousedOver.onMouseEnter(mouseX, mouseY);
		}
		this.mousedOver = widget;
		if (this.mousedOver != null) {
			mousedOver.onMouseLeave(mouseX, mouseY);
		}
	}

	@Nullable
	public GuiElement getFocused() {
		return this.focused;
	}

	public void setFocused(@Nullable GuiElement widget) {
		if (this.focused == widget) {
			return;
		}
		if (widget != null && !widget.canFocus()) {
			widget = null;
		}
		if (this.focused != null) {
			//postEvent(new ElementEvent.LoseFocus(this.focusedElement), GuiEventDestination.ALL);
		}
		this.focused = widget;
		if (this.focused != null) {
			//postEvent(new ElementEvent.GainFocus(this.focusedElement), GuiEventDestination.ALL);
		}
	}

	public boolean isMouseOver(final GuiElement element) {
		return this.getMousedOver() == element;
	}

	public boolean isDragged(final GuiElement element) {
		return this.getDragged() == element;
	}

	public boolean isFocused(final GuiElement element) {
		return this.getFocused() == element;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void updateClient() {
		if (!isVisible()) {
			return;
		}
		updateWindow();
		onUpdateClient();
		for (GuiElement widget : getElements()) {
			widget.updateClient();
		}
	}

	protected void updateWindow() {
		hoverElements = this.calculateHovered();
		this.setMousedOver(this.calculateMousedOver());
		if (this.getFocused() != null && (!this.getFocused().isVisible() || !this.getFocused().isEnabled())) {
			this.setFocused(null);
		}
		//|TODO - mousehelper.left down?
		if (!Minecraft.getInstance().mouseHandler.isLeftPressed()) {
			if (this.dragged != null) {
				this.setDragged(null);
			}
		}
	}

	@Nullable
	private GuiElement calculateMousedOver() {
		for (GuiElement element : hoverElements) {
			if (element.isEnabled() && element.isVisible() && element.canMouseOver()) {
				return element;
			}
		}
		return null;
	}

	private Deque<GuiElement> calculateHovered() {
		Deque<GuiElement> list = new ArrayDeque<>();
		for (GuiElement element : this.getQueuedElements(this)) {
			if (element.isMouseOver()) {
				list.addLast(element);
			}
		}
		return list;
	}

	private Collection<GuiElement> getQueuedElements(final GuiElement element) {
		List<GuiElement> widgets = new ArrayList<>();
		if (element instanceof ContainerElement group) {
			boolean addChildren = true;
			if (element.isCropped()) {
				int mouseX = getRelativeMouseX(element);
				int mouseY = getRelativeMouseY(element);
				GuiElement cropRelative = element.getCropElement() != null ? element.getCropElement() : this;
				int posX = cropRelative.getAbsoluteX() - element.getAbsoluteX();
				int posY = cropRelative.getAbsoluteY() - element.getAbsoluteY();
				addChildren = mouseX >= posX && mouseY >= posY && mouseX <= posX + element.getCropWidth() && mouseY <= posY + element.getCropHeight();
			}
			if (addChildren) {
				ListIterator<GuiElement> iterator = group.getElements().listIterator(group.getElements().size());
				while (iterator.hasPrevious()) {
					final GuiElement child = iterator.previous();
					widgets.addAll(this.getQueuedElements(child));
				}
			}
		}
		widgets.add(element);
		return widgets;
	}

	public void drawTooltip(PoseStack transform, int mouseX, int mouseY) {
		ToolTip lines = getTooltip(mouseX, mouseY);
		if (!lines.isEmpty()) {
			transform.pushPose();
			//TODO test
			com.mojang.blaze3d.platform.Window window = Minecraft.getInstance().getWindow();
			// GuiUtils.drawHoveringText(transform, lines.getLines(), mouseX - getX(), mouseY - getY(), window.getGuiScaledWidth(), window.getGuiScaledHeight(), -1, getFontRenderer());
			transform.popPose();
		}
	}

	@Override
	public ToolTip getTooltip(int mouseX, int mouseY) {
		ToolTip toolTip = new ToolTip();
		for (GuiElement element : hoverElements) {
			if (element.isEnabled() && element.isVisible() && element.hasTooltip()) {
				toolTip.addAll(element.getTooltip(getRelativeMouseX(element), getRelativeMouseY(element)));
			}
		}
		return toolTip;
	}

	/* Mouse */
	public void setMousePosition(int mouseX, int mouseY) {
		float dx = (float) mouseX - (float) this.mouseX;
		float dy = (float) mouseY - (float) this.mouseY;
		if (dx != 0.0f || dy != 0.0f) {
			if (dragged != null) {
				onMouseDrag(mouseX, mouseY);
			} else {
				onMouseMove(mouseX, mouseY);
			}
		}
		if (mouseX != this.mouseX || mouseY != this.mouseY) {
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			setMousedOver(calculateMousedOver());
		}
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getRelativeMouseX(@Nullable GuiElement element) {
		if (element == null) {
			return mouseX;
		}
		return mouseX - element.getAbsoluteX();
	}

	public int getRelativeMouseY(@Nullable GuiElement element) {
		if (element == null) {
			return mouseY;
		}
		return mouseY - element.getAbsoluteY();
	}

	@Override
	public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		return focused != null && focused.onKeyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
		return focused != null && focused.onKeyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean onCharTyped(char keyCode, int modifiers) {
		return focused != null && focused.onCharTyped(keyCode, modifiers);
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		this.setDraggedElement(mousedOver, mouseButton);
		this.setFocused(mousedOver);
		return super.onMouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
		setDragged(null);
		return super.onMouseReleased(mouseX, mouseY, mouseButton);
	}

	/* Gui Screen */
	public abstract int getScreenWidth();

	public abstract int getScreenHeight();

	public abstract int getGuiLeft();

	public abstract int getGuiTop();

	public abstract Screen getGui();

	public abstract int getGuiHeight();

	public abstract int getGuiWidth();

	protected Minecraft getMinecraft() {
		if (mc == null) {
			mc = Minecraft.getInstance();
		}
		return mc;
	}

	public Font getFontRenderer() {
		return getMinecraft().font;
	}
}
