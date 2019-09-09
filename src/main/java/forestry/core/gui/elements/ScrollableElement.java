package forestry.core.gui.elements;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;

import forestry.api.gui.IGuiElement;
import forestry.api.gui.events.GuiEvent;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.widgets.IScrollable;

public class ScrollableElement extends ElementGroup implements IScrollable {
	@Nullable
	private IGuiElement content;
	private double scrollPercentage;
	private float step;

	public ScrollableElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
		addSelfEventHandler(GuiEvent.WheelEvent.class, event -> {
			movePercentage(event.getDWheel());
		});
	}

	public int getInvisibleArea() {
		step = (12 * 0.5F);
		if (content == null) {
			return 0;
		}
		return (int) ((content.getHeight() - height) / (step));
	}

	protected void movePercentage(double percentage) {
		scrollPercentage = (percentage * step);
	}

	@Override
	public void onScroll(int value) {
		scrollPercentage = (value * step);
		if (content != null) {
			content.setOffset(0, -((int) scrollPercentage));
		}
	}

	public void setContent(@Nullable IGuiElement content) {
		this.content = content;
		if (content != null) {
			content.setCroppedZone(this, 0, 0, width, height);
		}
	}

	@Override
	public void clear() {
		remove(Lists.newArrayList(elements));
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return isMouseOver();
	}
}
