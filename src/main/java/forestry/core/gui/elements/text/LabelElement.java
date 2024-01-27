package forestry.core.gui.elements.text;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import forestry.api.core.tooltips.ITextInstance;
import forestry.core.gui.elements.GuiElement;

public abstract class LabelElement extends GuiElement {
	public LabelElement(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	public abstract LabelElement setStyle(Style style);

	public abstract LabelElement setFitText(boolean fitText);

	public abstract LabelElement setValue(Object text);

	public static class Builder implements ITextInstance<Builder, Builder, LabelElement> {
		@Nullable
		private final Consumer<GuiElement> parentAdder;
		@Nullable
		private final Function<LabelElement, GuiElement> finisher;
		private final Object root;
		boolean fitText = false;
		boolean shadow = false;

		public Builder(Object root) {
			this(null, root, null);
		}

		public Builder(@Nullable Consumer<GuiElement> parentAdder, Object root) {
			this(parentAdder, root, null);
		}

		public Builder(@Nullable Consumer<GuiElement> parentAdder, Object root, @Nullable Function<LabelElement, GuiElement> finisher) {
			this.parentAdder = parentAdder;
			this.finisher = finisher;
			if (root instanceof String) {
				root = Component.literal((String) root);
			}
			this.root = root;
		}

		@Nullable
		@Override
		public Component lastComponent() {
			if (root instanceof Component) {
				return (Component) root;
			}
			return null;
		}

		@Override
		public Builder add(Component line) {
			if (root instanceof MutableComponent) {
				((MutableComponent) root).append(line);
			}
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
			LabelElement element;
			if (root instanceof Component) {
				element = new ComponentText((Component) root)
						.setFitText(fitText)
						.setShadow(shadow);
			} else if (root instanceof FormattedCharSequence) {
				element = new ProcessorText((FormattedCharSequence) root)
						.setFitText(fitText)
						.setShadow(shadow);
			} else {
				throw new IllegalStateException(String.format("Tried to create text widget with an invalid type of text '%s'", root.getClass()));
			}
			if (finisher != null) {
				element = (LabelElement) finisher.apply(element);
			}
			if (parentAdder != null) {
				parentAdder.accept(element);
			}
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

		public Builder setStyle(Style style) {
			if (root instanceof MutableComponent) {
				((MutableComponent) root).setStyle(style);
			}
			return this;
		}

		public Builder withStyle(Style style) {
			if (root instanceof MutableComponent) {
				((MutableComponent) root).withStyle(style);
			}
			return this;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}
	}
}
