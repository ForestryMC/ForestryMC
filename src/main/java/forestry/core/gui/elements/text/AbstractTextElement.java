package forestry.core.gui.elements.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;

import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class AbstractTextElement<T, E extends AbstractTextElement<T, E>> extends LabelElement {
	/* Constants */
	public static final FontRenderer FONT_RENDERER = Minecraft.getInstance().font;
	public static final int DEFAULT_HEIGHT = FONT_RENDERER.lineHeight + 3;

	/* Attributes - State */
	protected T text;
	protected int originalWidth;
	protected boolean fitText;
	protected boolean shadow = false;

	public AbstractTextElement(T text) {
		this(0, 0, -1, DEFAULT_HEIGHT, text, true);
	}

	public AbstractTextElement(int xPos, int yPos, int width, int height, T text, boolean fitText) {
		super(xPos, yPos, width, height);
		this.originalWidth = width;
		this.text = text;
		setFitText(fitText);
	}

	@Override
	@SuppressWarnings("unchecked")
	public E setFitText(boolean fitText) {
		this.fitText = fitText;
		calculateWidth();
		return (E) this;
	}

	public boolean isFitText() {
		return fitText;
	}

	@SuppressWarnings("unchecked")
	public E setShadow(boolean value) {
		shadow = value;
		return (E) this;
	}

	public boolean hasShadow() {
		return shadow;
	}

	public Style getStyle() {
		if (text instanceof IFormattableTextComponent) {
			return ((IFormattableTextComponent) text).getStyle();
		}
		return Style.EMPTY;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E setStyle(Style style) {
		if (text instanceof IFormattableTextComponent) {
			((IFormattableTextComponent) text).setStyle(style);
		}
		calculateWidth();
		return (E) this;
	}

	public T getText() {
		return text;
	}

	public boolean setText(T value) {
		this.text = value;
		calculateWidth();
		return true;
	}

	@Override
	public abstract void drawElement(MatrixStack transform, int mouseX, int mouseY);

	protected abstract int calcWidth(FontRenderer font);

	@Override
	public int getWidth() {
		if (width < 0) {
			calculateWidth();
		}
		return width;
	}

	protected void calculateWidth() {
		if (fitText) {
			setWidth(calcWidth(FONT_RENDERER));
		} else {
			setWidth(originalWidth);
		}
	}


}
