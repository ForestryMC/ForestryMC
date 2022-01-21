package forestry.core.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import forestry.core.gui.Drawable;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.gui.widgets.IScrollable;

public class ScrollBarElement extends ContainerElement {
	/* Attributes - Final */
	private final GuiElement slider;

	/* Attributes - State */
	private boolean isScrolling;
	private boolean wasClicked;
	private boolean vertical = false;
	private int currentValue;
	private final ContainerElement interactionField;
	private int initialMouseClick;

	/* Attributes - Parameters */
	@Nullable
	private IScrollable listener;
	private int minValue;
	private int maxValue;
	private int step;
	private boolean initialised = false;
	private boolean mouseDown = false;

	public ScrollBarElement(Drawable sliderTexture) {
		interactionField = add(new ContainerElement());
		interactionField.setSize(preferredSize.width, preferredSize.height);
		isScrolling = false;
		wasClicked = false;
		visible = true;
		slider = interactionField.drawable(sliderTexture);
		addListeners();
	}

	public ScrollBarElement(Drawable backgroundTexture, boolean hasBorder, Drawable sliderTexture) {
		setSize(backgroundTexture.uWidth, backgroundTexture.vHeight);
		int offset = hasBorder ? 1 : 0;

		interactionField = new ContainerElement();
		interactionField.setPreferredBounds(offset, offset, hasBorder ? preferredSize.width - 2 : preferredSize.width, hasBorder ? preferredSize.height - 2 : preferredSize.height);
		isScrolling = false;
		wasClicked = false;
		visible = true;

		drawable(backgroundTexture);
		slider = interactionField.drawable(sliderTexture);
		add(interactionField);
		addListeners();
	}

	@Override
	public GuiElement setPreferredBounds(int xPos, int yPos, int width, int height) {
		interactionField.setSize(width, height);
		return super.setPreferredBounds(xPos, yPos, width, height);
	}

	protected void addListeners() {
		/*addEventHandler(GuiEvent.DownEvent.class, event -> {
			mouseDown = true;
		});
		addEventHandler(GuiEvent.UpEvent.class, event -> {
			mouseDown = false;
		});
		addEventHandler(GuiEvent.WheelEvent.class, event -> {
			if (listener == null || listener.isFocused((int) event.getX(), (int) event.getY()) || isMouseOver()) {
				double wheel = event.getDWheel();
				if (wheel > 0) {
					setValue(currentValue - step);
				} else if (wheel < 0) {
					setValue(currentValue + step);
				}
			}
		});*/
	}

	@Override
	protected ActionConfig.Builder buildActions(ActionConfig.Builder builder) {
		return builder.all(ActionType.PRESSED, ActionType.RELEASED, ActionType.SCROLLED);
	}

	@Override
	public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
		mouseDown = true;
		return false;
	}

	@Override
	public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
		mouseDown = false;
		return false;
	}

	@Override
	public boolean onMouseScrolled(double mouseX, double mouseY, double dWheel) {
		if (listener == null || listener.isFocused((int) mouseX, (int) mouseY) || isMouseOver()) {
			if (dWheel > 0) {
				setValue(currentValue - step);
			} else if (dWheel < 0) {
				setValue(currentValue + step);
			}
		}
		return false;
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
		return Mth.clamp(currentValue, minValue, maxValue);
	}

	public ScrollBarElement setValue(int value) {
		initialised = true;
		int oldValue = this.currentValue;
		currentValue = Mth.clamp(value, minValue, maxValue);
		if (oldValue == currentValue) {
			return this;
		}
		if (listener != null) {
			listener.onScroll(currentValue);
		}
		if (vertical) {
			int offset;
			if (value >= maxValue) {
				offset = interactionField.getWidth() - slider.getWidth();
			} else if (value <= minValue) {
				offset = 0;
			} else {
				offset = (int) (((float) (currentValue - minValue) / (maxValue - minValue)) * (float) (interactionField.getWidth() - slider.getWidth()));
			}
			slider.setXPosition(offset);
		} else {
			int offset;
			if (value >= maxValue) {
				offset = interactionField.getHeight() - slider.getHeight();
			} else if (value <= minValue) {
				offset = 0;
			} else {
				offset = (int) (((float) (currentValue - minValue) / (maxValue - minValue)) * (float) (interactionField.getHeight() - slider.getHeight()));
			}
			slider.setYPosition(offset);
		}
		return this;
	}

	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		if (!isVisible()) {
			return;
		}
		Window window = getWindow();
		updateSlider(window.getRelativeMouseX(interactionField), window.getRelativeMouseY(interactionField));

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.drawElement(transform, mouseX, mouseY);
	}

	private void updateSlider(int mouseX, int mouseY) {

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
			float value = (float) (pos - initialMouseClick) / (float) (vertical ? (interactionField.getWidth() - slider.getWidth()) : (interactionField.getHeight() - slider.getHeight()));
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
			float value = ((float) pos - (vertical ? slider.getWidth() : slider.getHeight()) / 2.0F) / (float) (vertical ? (interactionField.getWidth() - slider.getWidth()) : (interactionField.getHeight() - slider.getHeight()));
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
