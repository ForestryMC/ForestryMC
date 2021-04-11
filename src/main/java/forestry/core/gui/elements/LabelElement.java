package forestry.core.gui.elements;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.api.core.tooltips.ITextInstance;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.ILabelElement;
import forestry.core.gui.elements.lib.ITextElement;

public class LabelElement extends GuiElement implements ILabelElement {
	/* Constants */
	public static final FontRenderer FONT_RENDERER = Minecraft.getInstance().font;
	public static final int DEFAULT_HEIGHT = FONT_RENDERER.lineHeight + 3;

	/* Attributes - State */
	protected ITextProperties component;
	protected int originalWidth;
	protected boolean fitText;
	protected boolean shadow = false;

	public LabelElement(ITextProperties component) {
		this(0, 0, -1, DEFAULT_HEIGHT, component, true);
	}

	public LabelElement(int xPos, int yPos, int width, int height, ITextProperties component, boolean fitText) {
		super(xPos, yPos, width, height);
		this.originalWidth = width;
		this.component = component;
		setFitText(fitText);
	}

	@Override
	public LabelElement setFitText(boolean fitText) {
		this.fitText = fitText;
		calculateWidth();
		return this;
	}

	@Override
	public boolean isFitText() {
		return fitText;
	}

	@Override
	public LabelElement setShadow(boolean value) {
		shadow = value;
		return this;
	}

	@Override
	public boolean hasShadow() {
		return shadow;
	}

	@Override
	public Style getStyle() {
		if (component instanceof IFormattableTextComponent) {
			return ((IFormattableTextComponent) component).getStyle();
		}
		return Style.EMPTY;
	}

	@Override
	public ILabelElement setStyle(Style style) {
		if (component instanceof IFormattableTextComponent) {
			((IFormattableTextComponent) component).setStyle(style);
		}
		return this;
	}

	public ITextProperties getValue() {
		return component;
	}

	public boolean setValue(ITextProperties value) {
		this.component = value;
		calculateWidth();
		return true;
	}

	protected void calculateWidth() {
		if (fitText) {
			setWidth(FONT_RENDERER.width(component.getString()));
		} else {
			setWidth(originalWidth);
		}
	}

	@Override
	public ITextElement setText(ITextComponent text) {
		setValue(text);
		return this;
	}

	@Override
	public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
		if (shadow) {
			FONT_RENDERER.drawShadow(transform, (IReorderingProcessor) component, 0, 0, 0);
		} else {
			FONT_RENDERER.draw(transform, (IReorderingProcessor) component, 0, 0, 0);
		}
	}

	public static class Builder implements ITextInstance<Builder, Builder, LabelElement> {
		private final Consumer<IGuiElement> parentAdder;
		private final IFormattableTextComponent root;
		private boolean fitText = false;
		private boolean shadow = false;

		public Builder(Consumer<IGuiElement> parentAdder, IFormattableTextComponent root) {
			this.parentAdder = parentAdder;
			this.root = root;
		}

		@Nullable
		@Override
		public ITextComponent lastComponent() {
			return root;
		}

		@Override
		public Builder add(ITextComponent line) {
			root.append(line);
			return this;
		}

		@Override
		public Builder singleLine() {
			return this;
		}

		@Override
		public Builder cast() {
			return this;
		}

		@Override
		public LabelElement create() {
			Preconditions.checkNotNull(root);
			LabelElement element = new LabelElement(root).setFitText(fitText).setShadow(shadow);
			parentAdder.accept(element);
			return element;
		}

		public Builder fitText() {
			this.fitText = true;
			return this;
		}

		public Builder shadow() {
			shadow = true;
			return this;
		}

		@Override
		public boolean isEmpty() {
			return root == null;
		}
	}

	public static class Processor extends GuiElement implements ILabelElement {
		/* Attributes - State */
		protected IReorderingProcessor component;
		protected int originalWidth;
		protected boolean fitText;
		protected boolean shadow = false;

		public Processor(IReorderingProcessor component) {
			this(0, 0, -1, DEFAULT_HEIGHT, component, true);
		}

		public Processor(int xPos, int yPos, int width, int height, IReorderingProcessor component, boolean fitText) {
			super(xPos, yPos, width, height);
			this.originalWidth = width;
			this.component = component;
			setFitText(fitText);
		}


		public Processor setFitText(boolean fitText) {
			this.fitText = fitText;
			calculateWidth();
			return this;
		}

		@Override
		public boolean isFitText() {
			return fitText;
		}

		@Override
		public Processor setShadow(boolean value) {
			shadow = value;
			return this;
		}

		@Override
		public boolean hasShadow() {
			return shadow;
		}

		@Override
		public Style getStyle() {
			if (component instanceof IFormattableTextComponent) {
				return ((IFormattableTextComponent) component).getStyle();
			}
			return Style.EMPTY;
		}

		@Override
		public ILabelElement setStyle(Style style) {
			if (component instanceof IFormattableTextComponent) {
				((IFormattableTextComponent) component).setStyle(style);
			}
			return this;
		}

		public IReorderingProcessor getValue() {
			return component;
		}

		public boolean setValue(IReorderingProcessor value) {
			this.component = value;
			calculateWidth();
			return true;
		}

		@Override
		public ITextElement setText(ITextComponent text) {
			//ToDO: Fix?
			//setValue(text.);
			return this;
		}

		protected void calculateWidth() {
			if (fitText) {
				setWidth(FONT_RENDERER.width(component));
			} else {
				setWidth(originalWidth);
			}
		}

		@Override
		public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
			if (shadow) {
				FONT_RENDERER.drawShadow(transform, component, 0, 0, 0);
			} else {
				FONT_RENDERER.draw(transform, component, 0, 0, 0);
			}
		}
	}
}
