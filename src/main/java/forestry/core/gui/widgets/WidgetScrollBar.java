package forestry.core.gui.widgets;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import forestry.core.gui.Drawable;
import org.lwjgl.input.Mouse;

public class WidgetScrollBar extends Widget {
	@Nullable
	private Drawable background;
	private WidgetSlider slider;
	private boolean visible;

	private int minValue;
	private int maxValue;
	private int step;

	private int currentValue;
	@Nullable
	private IScrollable listener;

	private boolean isScrolling;
	private boolean wasClicked;

	private int initialMouseClickY;

	public WidgetScrollBar(WidgetManager manager, int xPos, int yPos, int width, int height, Drawable sliderTexture) {
		super(manager, xPos, yPos);

		this.background = null;
		this.width = width;
		this.height = height;

		isScrolling = false;
		wasClicked = false;
		visible = true;
		slider = new WidgetSlider(manager, xPos, yPos, sliderTexture);
	}

	public WidgetScrollBar(WidgetManager manager, int xPos, int yPos, Drawable backgroundTexture, boolean hasBorder, Drawable sliderTexture) {
		super(manager, xPos, yPos);

		int offset = hasBorder ? 1 : 0;

		this.background = backgroundTexture;
		this.width = backgroundTexture.uWidth;
		this.height = backgroundTexture.vHeight;

		isScrolling = false;
		wasClicked = false;
		visible = true;
		slider = new WidgetSlider(manager, xPos + offset, yPos + offset, sliderTexture);
	}

	public void setParameters(IScrollable listener, int minValue, int maxValue, int step){
		this.listener = listener;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;

		setValue(currentValue);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getValue() {
		return MathHelper.clamp(currentValue, minValue, maxValue);
	}

	public int setValue(int value) {
		currentValue = MathHelper.clamp(value, minValue, maxValue);
		if(listener != null){
			listener.onScroll(currentValue);
		}
		int offset;
		if(value >= maxValue){
			offset = height - slider.height;
		}else if(value <= minValue){
			offset = 0;
		}else{
			offset = (int)(((float)(currentValue - minValue) / (maxValue - minValue)) * (float) (height - slider.height));
		}
		slider.setOffset(0, offset);
		return currentValue;
	}

	@Override
	public void draw(int startX, int startY) {
		if(!isVisible()) {
			return;
		}
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if(background != null){
			background.draw(startX + xPos, startY + yPos);
		}
		slider.draw(startX, startY);
	}

	@Override
	public void update(int mouseX, int mouseY) {
		if(!isVisible()) {
			return;
		}
		boolean mouseDown = Mouse.isButtonDown(0);

		if(listener == null || listener.isFocused(mouseX, mouseY)) {
			int wheel = Mouse.getDWheel();
			if (wheel > 0) {
				setValue(currentValue - step);
				return;
			} else if (wheel < 0) {
				setValue(currentValue + step);
				return;
			}
		}

		//the position of the mouse relative to the position of the widget
		int y = mouseY - yPos;

		if(!mouseDown && wasClicked){
			wasClicked = false;
		}

		//not clicked and scrolling -> stop scrolling
		if (!mouseDown && isScrolling) {
			this.isScrolling = false;
		}

		//clicked on the slider and scrolling
		if (this.isScrolling) {
			float range = maxValue - minValue;
			float value = (float)(y - initialMouseClickY) / (float)(height - slider.height);
			value *= range;
			if(value < (float) step / 2f){
				setValue(minValue);
			}else if(value > maxValue - ((float) step / 2f)){
				setValue(maxValue);
			}else {
				setValue((int) (minValue + (float) step * Math.round(value)));
			}
		}
		//clicked on the slider
		else if(slider.isMouseOver(mouseX, mouseY)){
			if(mouseDown){
				isScrolling = true;
				initialMouseClickY = y - slider.getYOffset();
			}
		}
		//clicked on the bar but not on the slider
		else if(mouseDown && !wasClicked && isMouseOver(mouseX, mouseY)){
			float range = maxValue - minValue;
			float value = (float)(y - slider.height / 2) / (float)(height - slider.height);
			value *= range;
			if(value < (float) step / 2f){
				setValue(minValue);
			}else if(value > maxValue - ((float) step / 2f)){
				setValue(maxValue);
			}else {
				setValue((int) (minValue + (float) step * Math.round(value)));
			}
			wasClicked = true;
		}
	}
}
