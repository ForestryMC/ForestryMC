package forestry.core.gui.elements;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.client.gui.GuiUtils;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.elements.lib.events.ElementEvent;
import forestry.core.gui.elements.lib.events.GuiEvent;
import forestry.core.gui.elements.lib.events.GuiEventDestination;


/**
 * This element is the top parent.
 */
@OnlyIn(Dist.CLIENT)
public abstract class Window extends ElementGroup {
	@Nullable
	protected Minecraft mc = null;
	//The last x position of the mouse
	protected int mouseX = -1;
	//The last y position of the mouse
	protected int mouseY = -1;
	@Nullable
	protected GuiElement mousedOverElement;
	@Nullable
	protected GuiElement draggedElement;
	@Nullable
	protected GuiElement focusedElement;

	public Window(int width, int height) {
		super(0, 0, width, height);
		addEventHandler(ElementEvent.Deletion.class, deletion -> {
			GuiElement element = deletion.getOrigin();
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
	public Window getWindow() {
		return this;
	}

	@Override
	public GuiElement setParent(@Nullable GuiElement parent) {
		return this;
	}

	public void init(int guiLeft, int guiTop) {
		setLocation(guiLeft, guiTop);
	}

	/* Element Events */
	@Nullable
	public GuiElement getDraggedElement() {
		return this.draggedElement;
	}

	public void setDraggedElement(@Nullable GuiElement widget) {
		this.setDraggedElement(widget, -1);
	}

	public void setDraggedElement(@Nullable GuiElement widget, int button) {
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

	@Nullable
	public GuiElement getMousedOverElement() {
		return this.mousedOverElement;
	}

	public void setMousedOverElement(@Nullable GuiElement widget) {
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
	public GuiElement getFocusedElement() {
		return this.focusedElement;
	}

	public void setFocusedElement(@Nullable GuiElement widget) {
		GuiElement newElement = widget;
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

	public boolean isMouseOver(final GuiElement element) {
		return this.getMousedOverElement() == element;
	}

	public boolean isDragged(final GuiElement element) {
		return this.getDraggedElement() == element;
	}

	public boolean isFocused(final GuiElement element) {
		return this.getFocusedElement() == element;
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
		this.setMousedOverElement(this.calculateMousedOverElement());
		if (this.getFocusedElement() != null && (!this.getFocusedElement().isVisible() || !this.getFocusedElement().isEnabled())) {
			this.setFocusedElement(null);
		}
		//|TODO - mousehelper.left down?
		if (!Minecraft.getInstance().mouseHandler.isLeftPressed()) {
			if (this.draggedElement != null) {
				this.setDraggedElement(null);
			}
		}
	}

	@Nullable
	private GuiElement calculateMousedOverElement() {
		Deque<GuiElement> queue = this.calculateMousedOverElements();
		while (!queue.isEmpty()) {
			GuiElement element = queue.removeFirst();
			if (element.isEnabled() && element.isVisible() && element.canMouseOver()) {
				return element;
			}
		}
		return null;
	}

	private Deque<GuiElement> calculateMousedOverElements() {
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
		if (element instanceof ElementGroup) {
			ElementGroup group = (ElementGroup) element;
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

	public void drawTooltip(MatrixStack transform, int mouseX, int mouseY) {
		ToolTip lines = getTooltip(mouseX, mouseY);
		if (!lines.isEmpty()) {
			GlStateManager._pushMatrix();
			//TODO test
			MainWindow window = Minecraft.getInstance().getWindow();
			GuiUtils.drawHoveringText(transform, lines.getLines(), mouseX - getX(), mouseY - getY(), window.getGuiScaledWidth(), window.getGuiScaledHeight(), -1, getFontRenderer());
			GlStateManager._popMatrix();
		}
	}

	@Override
	public ToolTip getTooltip(int mouseX, int mouseY) {
		ToolTip toolTip = new ToolTip();
		Deque<GuiElement> queue = this.calculateMousedOverElements();
		while (!queue.isEmpty()) {
			GuiElement element = queue.removeFirst();
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

	public TextureManager getTextureManager() {
		return getMinecraft().getTextureManager();
	}

	public FontRenderer getFontRenderer() {
		return getMinecraft().font;
	}
}
