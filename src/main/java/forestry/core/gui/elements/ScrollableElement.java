package forestry.core.gui.elements;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;

import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.widgets.IScrollable;

public class ScrollableElement extends ContainerElement implements IScrollable {
	@Nullable
	private GuiElement content;
	private double scrollPercentage;
	private float step;

	public ScrollableElement() {
		//addSelfEventHandler(GuiEvent.WheelEvent.class, event -> movePercentage(event.getDWheel()));
	}

	@Override
	public boolean onMouseScrolled(double mouseX, double mouseY, double dWheel) {
		//movePercentage(dWheel);
		return false;
	}

	public int getInvisibleArea() {
		step = (12 * 0.5F);
		if (content == null) {
			return 0;
		}
		return (int) ((content.getLayoutSize().getHeight() - getLayoutSize().getHeight()) / (step));
	}

	protected void movePercentage(double percentage) {
		scrollPercentage = (percentage * step);
	}

	@Override
	public void onScroll(int value) {
		scrollPercentage = (value * step);
		if (content != null) {
			content.setPos(0, -((int) scrollPercentage));
		}
	}

	public ScrollableElement addContent(@Nullable GuiElement newContent) {
		if (content != null) {
			remove(content);
			content.setCroppedZone(null, 0, 0, -1, -1);
		}
		content = newContent;
		if (newContent != null) {
			add(newContent);
			newContent.setCroppedZone(this, 0, 0, preferredSize.width, preferredSize.height);
		}
		return this;
	}

	@Override
	public void clear() {
		remove(Lists.newArrayList(elements));
	}

	@Override
	public boolean canMouseOver() {
		return true;
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return isMouseOver();
	}
}
