package forestry.core.gui.tooltips;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public interface ITextInstance {
	default ITextInstance text(String text) {
		return add(new StringTextComponent(text));
	}

	default ITextInstance translated(String text, Object... args) {
		return add(new TranslationTextComponent(text, args));
	}

	default ITextInstance style(TextFormatting... formatting) {
		applyFormatting((component) -> component.func_240701_a_(formatting));
		return this;
	}

	default ITextInstance style(TextFormatting formatting) {
		applyFormatting((component) -> component.func_240699_a_(formatting));
		return this;
	}

	default ITextInstance style(Style style) {
		applyFormatting((component) -> component.func_230530_a_(style));
		return this;
	}

	default ITextInstance add(ITextComponent line, TextFormatting format) {
		return add(line, Style.EMPTY.setFormatting(format));
	}

	default ITextInstance add(ITextComponent line, Style style) {
		if (line instanceof IFormattableTextComponent) {
			((IFormattableTextComponent) line).func_230530_a_(style);
		}
		return add(line);
	}

	default ITextInstance add(List<ITextComponent> lines) {
		for (ITextComponent line : lines) {
			add(line);
		}
		return this;
	}

	default ITextInstance applyFormatting(Consumer<IFormattableTextComponent> action) {
		ITextComponent last = lastComponent();
		if (last instanceof IFormattableTextComponent) {
			action.accept((IFormattableTextComponent) last);
		}
		return this;
	}

	default ITextInstance apply(Consumer<ITextComponent> action) {
		ITextComponent last = lastComponent();
		if (last != null) {
			action.accept(last);
		}
		return this;
	}

	@Nullable
	ITextComponent lastComponent();

	ITextInstance add(ITextComponent line);

	TextCompound singleLine();

	TextCollection end();

}
