package forestry.core.gui.elements;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;


public abstract class ElementBuilder<B extends ElementBuilder<B, E>, E extends GuiElement> {
	public final List<ITooltipSupplier> tooltipSuppliers = new LinkedList<>();
	public Function<ActionConfig.Builder, ActionConfig.Builder> actionsCallback = (builder) -> builder;
	public ActionOrigin defaultOrigin = ActionOrigin.SELF;
	@Nullable
	public Point pos;
	public Dimension size = GuiElement.UNKNOWN_SIZE;
	public Alignment align = Alignment.TOP_LEFT;
	public boolean defaultVisibility = true;

	public ElementBuilder() {
	}

	public B actionOrigin(ActionOrigin defaultOrigin) {
		this.defaultOrigin = defaultOrigin;
		return cast();
	}

	public B defineActions(ActionOrigin defaultOrigin, Function<ActionConfig.Builder, ActionConfig.Builder> callback) {
		this.actionsCallback = callback;
		this.defaultOrigin = defaultOrigin;
		return cast();
	}

	public B defineActions(Function<ActionConfig.Builder, ActionConfig.Builder> callback) {
		this.actionsCallback = callback;
		return cast();
	}

	public B pos(Point pos) {
		this.pos = pos;
		return cast();
	}

	public B pos(int x, int y) {
		return pos(new Point(x, y));
	}

	public B size(Dimension size) {
		this.size = size;
		return cast();
	}

	public B size(int width, int height) {
		return size(new Dimension(width, height));
	}

	public B size(int value) {
		return size(value, value);
	}

	public B align(Alignment align) {
		this.align = align;
		return cast();
	}

	public B visible(boolean visible) {
		this.defaultVisibility = visible;
		return cast();
	}

	public B hide() {
		return visible(false);
	}

	public B show() {
		return visible(true);
	}

	public B addTooltip(Component line) {
		addTooltip((toolTip, element, mouseX, mouseY) -> toolTip.add(line));
		return cast();
	}

	public B addTooltip(Collection<Component> lines) {
		addTooltip((toolTip, element, mouseX, mouseY) -> toolTip.addAll(lines));
		return cast();
	}

	public B addTooltip(Supplier<Component> line) {
		addTooltip((toolTip, element, mouseX, mouseY) -> toolTip.add(line.get()));
		return cast();
	}

	public B addTooltip(ITooltipSupplier supplier) {
		tooltipSuppliers.add(supplier);
		return cast();
	}

	@SuppressWarnings("unchecked")
	protected B cast() {
		return (B) this;
	}

	public abstract E create();
}
