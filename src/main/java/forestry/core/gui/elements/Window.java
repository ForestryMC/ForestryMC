package forestry.core.gui.elements;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;

import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IWindowElement;
import forestry.api.gui.events.ElementEvent;
import forestry.api.gui.events.GuiEvent;
import forestry.api.gui.events.GuiEventDestination;
import forestry.core.gui.IGuiSizable;
import forestry.core.gui.elements.layouts.ElementGroup;

import org.lwjgl.input.Mouse;

/**
 * This element is the top parent.
 */
@SideOnly(Side.CLIENT)
public class Window<G extends GuiScreen & IGuiSizable> extends ElementGroup implements IWindowElement {
	protected final G gui;
	@Nullable
	private Minecraft mc = null;
	//The last x position of the mouse
	protected int mouseX = -1;
	//The last y position of the mouse
	protected int mouseY = -1;
	@Nullable
	private IGuiElement mousedOverElement;
	@Nullable
	private IGuiElement draggedElement;
	@Nullable
	private IGuiElement focusedElement;

	public Window(int width, int height, G gui) {
		super(0, 0, width, height);
		this.gui = gui;
		addEventHandler(ElementEvent.Deletion.class, deletion -> {
			IGuiElement element = deletion.getOrigin();
			if (isMouseOver(element)) {
				setMousedOverElement(null);
			}
			if (isDragged(element)) {
				setDraggedElement(null);
			}
			if (isFocused(element)) {
				setFocusedElement(null);
			}
		});
		this.addEventHandler(GuiEvent.DownEvent.class, event -> {
			this.setDraggedElement(mousedOverElement, event.getButton());
			this.setFocusedElement(mousedOverElement);
		});
		this.addEventHandler(GuiEvent.UpEvent.class, event -> setDraggedElement(null));
	}

	@Override
	public IWindowElement getWindow() {
		return this;
	}

	@Override
	public IGuiElement setParent(@Nullable IGuiElement parent) {
		return this;
	}

	public void init(int guiLeft, int guiTop) {
		setLocation(guiLeft, guiTop);
	}

	/* Element Events */
	@Nullable
	public IGuiElement getDraggedElement() {
		return this.draggedElement;
	}

	public void setDraggedElement(@Nullable IGuiElement widget) {
		this.setDraggedElement(widget, -1);
	}

	public void setDraggedElement(@Nullable IGuiElement widget, int button) {
		if (this.draggedElement == widget) {
			return;
		}
		if (this.draggedElement != null) {
			postEvent(new ElementEvent.EndDrag(this.draggedElement), GuiEventDestination.ALL);
		}
		this.draggedElement = widget;
		if (this.draggedElement != null) {
			postEvent(new ElementEvent.StartDrag(this.draggedElement, button), GuiEventDestination.ALL);
		}
	}

	@Override
	@Nullable
	public IGuiElement getMousedOverElement() {
		return this.mousedOverElement;
	}

	public void setMousedOverElement(@Nullable IGuiElement widget) {
		if (this.mousedOverElement == widget) {
			return;
		}
		if (this.mousedOverElement != null) {
			postEvent(new ElementEvent.EndMouseOver(this.mousedOverElement), GuiEventDestination.ALL);
		}
		this.mousedOverElement = widget;
		if (this.mousedOverElement != null) {
			postEvent(new ElementEvent.StartMouseOver(this.mousedOverElement), GuiEventDestination.ALL);
		}
	}

	@Nullable
	public IGuiElement getFocusedElement() {
		return this.focusedElement;
	}

	public void setFocusedElement(@Nullable IGuiElement widget) {
		IGuiElement newElement = widget;
		if (this.focusedElement == newElement) {
			return;
		}
		if (newElement != null && !newElement.canFocus()) {
			newElement = null;
		}
		if (this.focusedElement != null) {
			postEvent(new ElementEvent.LoseFocus(this.focusedElement), GuiEventDestination.ALL);
		}
		this.focusedElement = newElement;
		if (this.focusedElement != null) {
			postEvent(new ElementEvent.GainFocus(this.focusedElement), GuiEventDestination.ALL);
		}
	}

	@Override
	public boolean isMouseOver(final IGuiElement element) {
		return this.getMousedOverElement() == element;
	}

	public boolean isDragged(final IGuiElement element) {
		return this.getDraggedElement() == element;
	}

	public boolean isFocused(final IGuiElement element) {
		return this.getFocusedElement() == element;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateClient() {
		if (!isVisible()) {
			return;
		}
		updateWindow();
		onUpdateClient();
		for (IGuiElement widget : getElements()) {
			widget.updateClient();
		}
	}

	protected void updateWindow() {
		this.setMousedOverElement(this.calculateMousedOverElement());
		if (this.getFocusedElement() != null && (!this.getFocusedElement().isVisible() || !this.getFocusedElement().isEnabled())) {
			this.setFocusedElement(null);
		}
		if (!Mouse.isButtonDown(0)) {
			if (this.draggedElement != null) {
				this.setDraggedElement(null);
			}
		}
	}

	@Nullable
	private IGuiElement calculateMousedOverElement() {
		Deque<IGuiElement> queue = this.calculateMousedOverElements();
		while (!queue.isEmpty()) {
			IGuiElement element = queue.removeFirst();
			if (element.isEnabled() && element.isVisible() && element.canMouseOver()) {
				return element;
			}
		}
		return null;
	}

	private Deque<IGuiElement> calculateMousedOverElements() {
		Deque<IGuiElement> list = new ArrayDeque<>();
		for (IGuiElement element : this.getQueuedElements(this)) {
			if (element.isMouseOver()) {
				list.addLast(element);
			}
		}
		return list;
	}

	private Collection<IGuiElement> getQueuedElements(final IGuiElement element) {
		List<IGuiElement> widgets = new ArrayList<>();
		if (element instanceof IElementGroup) {
			IElementGroup group = (IElementGroup) element;
			boolean addChildren = true;
			if (element.isCropped()) {
				int mouseX = getRelativeMouseX(element);
				int mouseY = getRelativeMouseY(element);
				IGuiElement cropRelative = element.getCropElement() != null ? element.getCropElement() : this;
				int posX = cropRelative.getAbsoluteX() - element.getAbsoluteX();
				int posY = cropRelative.getAbsoluteY() - element.getAbsoluteY();
				addChildren = mouseX >= posX && mouseY >= posY && mouseX <= posX + element.getCropWidth() && mouseY <= posY + element.getCropWidth();
			}
			if (addChildren) {
				ListIterator<IGuiElement> iterator = group.getElements().listIterator(group.getElements().size());
				while (iterator.hasPrevious()) {
					final IGuiElement child = iterator.previous();
					widgets.addAll(this.getQueuedElements(child));
				}
			}
		}
		widgets.add(element);
		return widgets;
	}

	public void drawTooltip(int mouseX, int mouseY) {
		List<String> lines = getTooltip(mouseX, mouseY);
		if (!lines.isEmpty()) {
			GlStateManager.pushMatrix();
			ScaledResolution scaledresolution = new ScaledResolution(getMinecraft());
			GuiUtils.drawHoveringText(lines, mouseX - getX(), mouseY - getY(), scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), -1, getFontRenderer());
			GlStateManager.popMatrix();
		}
	}

	@Override
	public List<String> getTooltip(int mouseX, int mouseY) {
		List<String> tooltip = new ArrayList<>();
		Deque<IGuiElement> queue = this.calculateMousedOverElements();
		while (!queue.isEmpty()) {
			IGuiElement element = queue.removeFirst();
			if (element.isEnabled() && element.isVisible() && element.hasTooltip()) {
				tooltip.addAll(element.getTooltip(getRelativeMouseX(element), getRelativeMouseY(element)));
			}
		}
		return tooltip;
	}

	/* Mouse */
	public void setMousePosition(int mouseX, int mouseY) {
		float dx = (float) mouseX - (float) this.mouseX;
		float dy = (float) mouseY - (float) this.mouseY;
		if (dx != 0.0f || dy != 0.0f) {
			if (draggedElement != null) {
				postEvent(new GuiEvent.DragEvent(draggedElement, dx, dy), GuiEventDestination.ALL);
			} else {
				postEvent(new GuiEvent.MoveEvent(this, dx, dy), GuiEventDestination.ALL);
			}
		}
		if (mouseX != this.mouseX || mouseY != this.mouseY) {
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			setMousedOverElement(calculateMousedOverElement());
		}
	}

	@Override
	public int getMouseX() {
		return mouseX;
	}

	@Override
	public int getMouseY() {
		return mouseY;
	}

	@Override
	public int getRelativeMouseX(@Nullable IGuiElement element) {
		if (element == null) {
			return mouseX;
		}
		return mouseX - element.getAbsoluteX();
	}

	@Override
	public int getRelativeMouseY(@Nullable IGuiElement element) {
		if (element == null) {
			return mouseY;
		}
		return mouseY - element.getAbsoluteY();
	}

	/* Gui Screen */
	@Override
	public int getScreenWidth() {
		return gui.width;
	}

	@Override
	public int getScreenHeight() {
		return gui.height;
	}

	@Override
	public int getGuiLeft() {
		return gui.getGuiLeft();
	}

	@Override
	public int getGuiTop() {
		return gui.getGuiTop();
	}

	@Override
	public G getGui() {
		return gui;
	}

	@Override
	public int getGuiHeight() {
		return gui.getSizeX();
	}

	@Override
	public int getGuiWidth() {
		return gui.getSizeY();
	}

	protected Minecraft getMinecraft() {
		if (mc == null) {
			mc = Minecraft.getMinecraft();
		}
		return mc;
	}

	@Override
	public TextureManager getTextureManager() {
		return getMinecraft().getTextureManager();
	}

	@Override
	public FontRenderer getFontRenderer() {
		return getMinecraft().fontRenderer;
	}
}
