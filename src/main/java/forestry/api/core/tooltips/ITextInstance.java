package forestry.api.core.tooltips;

import net.minecraft.util.text.*;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

public interface ITextInstance<I extends ITextInstance<?, ?, ?>, S, R> {
    default I text(ITextComponent text) {
        return add(text);
    }

    default I translated(String text, Object... args) {
        return add(new TranslationTextComponent(text, args));
    }

    default I style(TextFormatting... formatting) {
        applyFormatting((component) -> component.mergeStyle(formatting));
        return cast();
    }

    default I style(TextFormatting formatting) {
        applyFormatting((component) -> component.mergeStyle(formatting));
        return cast();
    }

    default I style(Style style) {
        applyFormatting((component) -> component.mergeStyle(style));
        return cast();
    }

    default I add(ITextComponent line, TextFormatting format) {
        if (line instanceof IFormattableTextComponent) {
            ((IFormattableTextComponent) line).mergeStyle(format);
        }
        return add(line);
    }

    default I add(ITextComponent line, TextFormatting... format) {
        if (line instanceof IFormattableTextComponent) {
            ((IFormattableTextComponent) line).mergeStyle(format);
        }
        return add(line);
    }

    default I add(ITextComponent line, Style style) {
        if (line instanceof IFormattableTextComponent) {
            ((IFormattableTextComponent) line).mergeStyle(style);
        }
        return add(line);
    }

    default I addAll(ITextComponent... lines) {
        for (ITextComponent line : lines) {
            add(line);
        }
        return cast();
    }

    default I addAll(Collection<ITextComponent> lines) {
        for (ITextComponent line : lines) {
            add(line);
        }
        return cast();
    }

    default I applyFormatting(Consumer<IFormattableTextComponent> action) {
        ITextComponent last = lastComponent();
        if (last instanceof IFormattableTextComponent) {
            action.accept((IFormattableTextComponent) last);
        }
        return cast();
    }

    default I apply(Consumer<ITextComponent> action) {
        ITextComponent last = lastComponent();
        if (last != null) {
            action.accept(last);
        }
        return cast();
    }

    I cast();

    @Nullable
    ITextComponent lastComponent();

    I add(ITextComponent line);

    S singleLine();

    R create();

    boolean isEmpty();
}
