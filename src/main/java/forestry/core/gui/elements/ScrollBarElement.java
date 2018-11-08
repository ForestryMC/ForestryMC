package forestry.core.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import forestry.api.gui.IWindowElement;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.widgets.IScrollable;

import org.lwjgl.input.Mouse;

public class ScrollBarElement extends ElementGroup {
	/* Attributes - Final */
	private final GuiElement slider;

	/* Attributes - State */
	private boolean isScrolling;
	private boolean wasClicked;
	private boolean vertical = false;
	private int currentValue;
	private final ElementGroup interactionField;
	private int initialMouseClick;

	/* Attributes - Parameters */
	@Nullable
	private IScrollable listener;
	private int minValue;
	private int maxValue;
	private int step;
	private boolean initialised = false;

	public ScrollBarElement(int xPos, int yPos, int width, int height, Drawable sliderTexture) {
		super(xPos, yPos, width, height);

		interactionField = add(new ElementGroup(0, 0, width, height));
		isScrolling = false;
		wasClicked = false;
		visible = true;
		slider = interactionField.drawable(sliderTexture);
	}

	public ScrollBarElement(int xPos, int yPos, Drawable backgroundTexture, boolean hasBorder, Drawable sliderTexture) {
		super(xPos, yPos, backgroundTexture.uWidth, backgroundTexture.vHeight);

		int offset = hasBorder ? 1 : 0;

		interactionField = new ElementGroup(offset, offset, hasBorder ? width - 2 : width, hasBorder ? height - 2 : height);
		isScrolling = false;
		wasClicked = false;
		visible = true;

		drawable(backgroundTexture);
		slider = interactionField.drawable(sliderTexture);
		add(interactionField);
	}

	public ScrollBarElement setVertical() {
		this.vertical = true;
		return this;
	}

	public ScrollBarElement setParameters(IScrollable listener, int minValue, int maxValue, int step) {
		this.listener = listener;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;

		if (initialised) {
			setValue(currentValue);
		}
		return this;
	}

	@Override
	public boolean canMouseOver() {
		return true;
	}

	public int getValue() {
		return MathHelper.clamp(currentValue, minValue, maxValue);
	}

	public ScrollBarElement setValue(int value) {
		initialised = true;
		currentValue = MathHelper.clamp(value, minValue, maxValue);
		if (listener != null) {
			listener.onScroll(currentValue);
		}
		if (vertical) {
			int offset;
			if (value >= maxValue) {
				offset = interactionField.getWidth() - slider.width;
			} else if (value <= minValue) {
				offset = 0;
			} else {
				offset = (int) (((float) (currentValue - minValue) / (maxValue - minValue)) * (float) (interactionField.getWidth() - slider.width));
			}
			slider.setXPosition(offset);
		} else {
			int offset;
			if (value >= maxValue) {
				offset = interactionField.getHeight() - slider.height;
			} else if (value <= minValue) {
				offset = 0;
			} else {
				offset = (int) (((float) (currentValue - minValue) / (maxValue - minValue)) * (float) (interactionField.getHeight() - slider.height));
			}
			slider.setYPosition(offset);
		}
		return this;
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		if (!isVisible()) {
			return;
		}
		IWindowElement window = getWindow();
		updateSlider(window.getRelativeMouseX(interactionField), window.getRelativeMouseY(interactionField));

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		super.drawElement(mouseX, mouseY);
	}

	private void updateSlider(int mouseX, int mouseY) {
		boolean mouseDown = Mouse.isButtonDown(0);

		if (listener == null || listener.isFocused(mouseX, mouseY)) {
			int wheel = Mouse.getDWheel();
			if (wheel > 0) {
				setValue(currentValue - step);
				return;
			} else if (wheel < 0) {
				setValue(currentValue + step);
				return;
			}
		}

		//The position of the mouse relative to the position of the widget
		int pos = vertical ? mouseX - interactionField.getX() : mouseY - interactionField.getY();

		if (!mouseDown && wasClicked) {
			wasClicked = false;
		}

		//Not clicked and scrolling -> stop scrolling
		if (!mouseDown && isScrolling) {
			this.isScrolling = false;
		}

		//Clicked on the slider and scrolling
		if (this.isScrolling) {
			int range = maxValue - minValue;
			float value = (float) (pos - initialMouseClick) / (float) (vertical ? (interactionField.getWidth() - slider.width) : (interactionField.getHeight() - slider.height));
			value *= (float) range;
			if (value < (float) step / 2f) {
				setValue(minValue);
			} else if (value > maxValue - ((float) step / 2f)) {
				setValue(maxValue);
			} else {
				setValue((int) (minValue + (float) step * Math.round(value)));
			}
		} else if (slider.isMouseOver()) { //clicked on the slider
			if (mouseDown) {
				isScrolling = true;
				initialMouseClick = vertical ? pos - slider.getX() : pos - slider.getY();
			}
		} else if (mouseDown && !wasClicked && isMouseOver()) { //clicked on the bar but not on the slider
			int range = maxValue - minValue;
			float value = ((float) pos - (vertical ? slider.width : slider.height) / 2.0F) / (float) (vertical ? (interactionField.getWidth() - slider.width) : (interactionField.getHeight() - slider.height));
			value *= (float) range;
			if (value < (float) step / 2f) {
				setValue(minValue);
			} else if (value > maxValue - ((float) step / 2f)) {
				setValue(maxValue);
			} else {
				setValue((int) (minValue + (float) step * Math.round(value)));
			}
			wasClicked = true;
		}
	}
}
