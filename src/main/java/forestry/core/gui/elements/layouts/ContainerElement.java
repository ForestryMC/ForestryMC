package forestry.core.gui.elements.layouts;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.gui.Drawable;
import forestry.core.gui.GuiConstants;
import forestry.core.gui.elements.ActionConfig;
import forestry.core.gui.elements.ActionOrigin;
import forestry.core.gui.elements.ActionType;
import forestry.core.gui.elements.Alignment;
import forestry.core.gui.elements.DrawableElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.ItemElement;
import forestry.core.gui.elements.text.LabelElement;
import forestry.core.utils.Log;

@OnlyIn(Dist.CLIENT)
public class ContainerElement extends GuiElement {
	@Nullable
	protected Layout layout = FreeLayout.INSTANCE;
	protected final List<GuiElement> elements = new ArrayList<>();
	protected boolean dirty;

	public ContainerElement() {
	}

	public <E extends GuiElement> E add(E element) {
		elements.add(element);
		element.setParent(this);
		markDirty();
		return element;
	}

	public <E extends GuiElement> E add(E element, Consumer<E> callback) {
		callback.accept(element);
		elements.add(element);
		element.setParent(this);
		markDirty();
		return element;
	}

	public <E extends GuiElement> E remove(E element) {
		elements.remove(element);
		getWindow().onRemove(element);
		markDirty();
		return element;
	}

	public ContainerElement add(GuiElement... elements) {
		for (GuiElement element : elements) {
			add(element);
		}
		return this;
	}

	public ContainerElement remove(GuiElement... elements) {
		for (GuiElement element : elements) {
			remove(element);
		}
		return this;
	}

	public ContainerElement add(Collection<GuiElement> elements) {
		elements.forEach(this::add);
		return this;
	}

	public ContainerElement remove(Collection<GuiElement> elements) {
		elements.forEach(this::remove);
		return this;
	}

	@Override
	public GuiElement setLocation(int xPos, int yPos) {
		markDirty();
		return super.setLocation(xPos, yPos);
	}

	@Override
	public GuiElement setPos(int x, int y) {
		markDirty();
		return super.setPos(x, y);
	}

	@Override
	public GuiElement setSize(int width, int height) {
		markDirty();
		return super.setSize(width, height);
	}

	@Override
	public void setAssignedBounds(Rectangle bounds) {
		markDirty();
		super.setAssignedBounds(bounds);
	}

	public ContainerElement setLayout(@Nullable Layout layout) {
		this.layout = layout;
		markDirty();
		return this;
	}

	public void forceLayout() {
		doLayout();
	}

	protected void doLayout() {
		if (layout == null || !dirty) {
			return;
		}
		if (bounds == null) {
			Log.error("Failed to layout container %s!", this);
			markClean();
			return;
		}
		layout.layoutContainer(bounds, elements);
		for (GuiElement element : elements) {
			element.afterLayout();
		}
		markClean();
	}

	@Override
	public Dimension getLayoutSize() {
		if (layout != null) {
			return layout.getLayoutSize(this);
		}
		return super.getLayoutSize();
	}

	public void markDirty() {
		dirty = true;
	}

	public void markClean() {
		dirty = false;
	}

	public void clear() {
		for (GuiElement element : new ArrayList<>(elements)) {
			remove(element);
		}
		markClean();
	}

	public List<GuiElement> getElements() {
		return elements;
	}

	@Override
	protected void drawElement(PoseStack transform, int mouseX, int mouseY) {
		doLayout();
		int mX = mouseX - getX();
		int mY = mouseY - getY();
		elements.forEach(element -> element.draw(transform, mX, mY));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void updateClient() {
		if (!isVisible()) {
			return;
		}
		onUpdateClient();
		for (GuiElement widget : getElements()) {
			widget.updateClient();
		}
	}

	@Nullable
	public GuiElement getLastElement() {
		return elements.isEmpty() ? null : elements.get(elements.size() - 1);
	}

	public DrawableElement drawable(Drawable drawable) {
		return add(new DrawableElement(drawable));
	}

	public DrawableElement drawable(int x, int y, Drawable drawable) {
		return add(new DrawableElement(x, y, drawable));
	}

	public ItemElement item(int xPos, int yPos, ItemStack itemStack) {
		ItemElement element = new ItemElement(xPos, yPos, itemStack);
		add(element);
		return element;
	}

	public Style defaultStyle() {
		return GuiConstants.DEFAULT_STYLE;
	}

	public LabelElement label(Component component) {
		return new LabelElement.Builder(this::add, component).create();
	}

	public LabelElement label(FormattedCharSequence component) {
		return new LabelElement.Builder(this::add, component).create();
	}

	public LabelElement translated(String key, Object... args) {
		return label(Component.translatable(key, args));
	}

	public LabelElement.Builder labelLine(MutableComponent component) {
		return new LabelElement.Builder(this::add, component);
	}

	public LabelElement.Builder translatedLine(String key, Object... args) {
		return labelLine(Component.translatable(key, args));
	}

	public LabelElement.Builder labelLine(String text) {
		return labelLine(Component.literal(text));
	}

	public LabelElement label(String text) {
		return label(text, defaultStyle());
	}

	public LabelElement label(String text, Style style) {
		return label(text, Alignment.TOP_LEFT, style);
	}

	public LabelElement label(String text, Alignment align) {
		return label(text, align, defaultStyle());
	}

	public LabelElement label(String text, Alignment align, Style textStyle) {
		return label(text, 0, 0, -1, 12, align, textStyle);
	}

	public LabelElement label(String text, int x, int y, int width, int height, Alignment align, Style textStyle) {
		return new LabelElement.Builder(this::add, text, (element) -> element.setPos(x, y).setSize(width, height).setAlign(align)).fitText().setStyle(textStyle).create();
	}

	public ContainerElement vertical(int width, int spacing) {
		return add(GuiElementFactory.vertical(width, spacing));
	}

	public ContainerElement horizontal(int height, int spacing) {
		return add(GuiElementFactory.horizontal(height, spacing));
	}

	public ContainerElement pane() {
		return pane(0, 0, UNKNOWN_WIDTH, UNKNOWN_HEIGHT);
	}

	public ContainerElement pane(int width, int height) {
		return pane(0, 0, width, height);
	}

	public ContainerElement pane(int xPos, int yPos, int width, int height) {
		return add(new ContainerElement(), (container) -> container.setPreferredBounds(xPos, yPos, width, height));
	}

	public LayoutHelper layoutHelper(LayoutHelper.LayoutFactory layoutFactory, int width, int height) {
		return new LayoutHelper(layoutFactory, width, height, this);
	}

	/* Actions */
	@Override
	protected ActionConfig.Builder buildActions(ActionConfig.Builder builder) {
		return ActionConfig.allBuilder();
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		return callActions(mouseButton, ActionType.PRESSED, GuiElement::onMouseClicked);
	}

	@Override
	public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
		return callActions(mouseButton, ActionType.RELEASED, GuiElement::onMouseReleased);
	}

	@Override
	public boolean onMouseScrolled(double mouseX, double mouseY, double dWheel) {
		return callActions(dWheel, ActionType.SCROLLED, GuiElement::onMouseScrolled);
	}

	@Override
	public void onMouseMove(double mouseX, double mouseY) {
		/*callActions(mouseX, mouseY, ActionType.MOVE, (element, x, y)->{
			element.onMouseMove(x, y);
			return false;
		});*/
	}

	@Override
	public boolean onMouseDrag(double mouseX, double mouseY) {
		return callActions(ActionType.DRAG_MOVE, GuiElement::onMouseDrag);
	}

	/*@Override
	public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		return super.onKeyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
		return super.onKeyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean onCharTyped(char keyCode, int modifiers) {
		return super.onCharTyped(keyCode, modifiers);
	}*/

	/**
	 * Determines the origin of the action based on the mouse position and the currently top mouse over element.
	 *
	 * @param element The element the action will be called on
	 * @param mouseX  The x position of the mouse relative to the given element
	 * @param mouseY  The y position of the mouse relative to the given element
	 */
	protected ActionOrigin getOrigin(GuiElement element, double mouseX, double mouseY) {
		boolean isTop = getWindow().isMouseOver(element);
		if (element.isMouseOver(mouseX, mouseY)) {
			return isTop ? ActionOrigin.SELF_TOP : ActionOrigin.SELF;
		}
		return ActionOrigin.ALL;
	}

	/**
	 * If this element can currently handle this action.
	 *
	 * @param type The type of action
	 */
	protected boolean canHandleAction(ActionType type) {
		return true;
	}

	/**
	 * Checks if the given element can receive actions.
	 *
	 * @param element The element to be checked
	 */
	protected boolean canReceiveAction(GuiElement element) {
		return element.isVisible() && element.isEnabled();
	}

	/**
	 * Checks if the given element can handle the given action type.
	 *
	 * @param element The element to be checked
	 * @param mouseX  The x position of the mouse relative to the given element
	 * @param mouseY  The y position of the mouse relative to the given element
	 * @param type    The type of action
	 */
	protected boolean hasOrigin(GuiElement element, double mouseX, double mouseY, ActionType type) {
		return element.hasOrigin(type, getOrigin(element, mouseX, mouseY));
	}

	/**
	 * Calls the given action on every child of this container if the child can receive and handle the action type.
	 * Stops if the action returns true for any element.
	 * <p>
	 * This variant provides a additional value
	 *
	 * @param value  An additional information about the action
	 * @param type   The type of the action
	 * @param action The action itself, will be called for every valid child.
	 * @param <V>    The type of the additional value
	 * @return True if the action returned true for any child.
	 */
	protected <V> boolean callActions(V value, ActionType type, IMouseValueAction<V> action) {
		if (!canHandleAction(type)) {
			return false;
		}
		for (GuiElement element : elements) {
			double mX = getWindow().getRelativeMouseX(element);
			double mY = getWindow().getRelativeMouseY(element);
			if (!canReceiveAction(element) || !hasOrigin(element, mX, mY, type)) {
				continue;
			}
			if (action.handle(element, mX, mY, value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calls the given action on every child of this container if the child can receive and handle the action type.
	 * Stops if the action returns true for any element.
	 *
	 * @param type   The type of the action
	 * @param action The action itself, will be called for every valid child.
	 * @return True if the action returned true for any child.
	 */
	protected boolean callActions(ActionType type, IMouseAction action) {
		if (!canHandleAction(type)) {
			return false;
		}
		for (GuiElement element : elements) {
			double mX = getWindow().getRelativeMouseX(element);
			double mY = getWindow().getRelativeMouseY(element);
			if (!canReceiveAction(element) || !hasOrigin(element, mX, mY, type)) {
				continue;
			}
			if (action.handle(element, mX, mY)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper interface to handle mouse events on this element.
	 * This variant provides a additional value
	 *
	 * @param <V> The type of the additional parameter
	 */
	private interface IMouseValueAction<V> {
		boolean handle(GuiElement element, double mouseX, double mouseY, V value);
	}

	/**
	 * Helper interface to handle mouse events on this element.
	 */
	private interface IMouseAction {
		boolean handle(GuiElement element, double mouseX, double mouseY);
	}
}
