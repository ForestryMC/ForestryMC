package forestry.api.core.tooltips;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;

public interface ITextInstance<I extends ITextInstance<?, ?, ?>, S, R> {
	default I text(String text) {
		return add(Component.literal(text));
	}

	default I translated(String text, Object... args) {
		return add(Component.translatable(text, args));
	}

	default I style(ChatFormatting... formatting) {
		applyFormatting((component) -> component.withStyle(formatting));
		return cast();
	}

	default I style(ChatFormatting formatting) {
		applyFormatting((component) -> component.withStyle(formatting));
		return cast();
	}

	default I style(Style style) {
		applyFormatting((component) -> component.withStyle(style));
		return cast();
	}

	default I add(Component line, ChatFormatting format) {
		if (line instanceof MutableComponent) {
			((MutableComponent) line).withStyle(format);
		}
		return add(line);
	}

	default I add(Component line, ChatFormatting... format) {
		if (line instanceof MutableComponent) {
			((MutableComponent) line).withStyle(format);
		}
		return add(line);
	}

	default I add(Component line, Style style) {
		if (line instanceof MutableComponent) {
			((MutableComponent) line).withStyle(style);
		}
		return add(line);
	}

	default I addAll(Component... lines) {
		for (Component line : lines) {
			add(line);
		}
		return cast();
	}

	default I addAll(Collection<Component> lines) {
		for (Component line : lines) {
			add(line);
		}
		return cast();
	}

	default I applyFormatting(Consumer<MutableComponent> action) {
		Component last = lastComponent();
		if (last instanceof MutableComponent) {
			action.accept((MutableComponent) last);
		}
		return cast();
	}

	default I apply(Consumer<Component> action) {
		Component last = lastComponent();
		if (last != null) {
			action.accept(last);
		}
		return cast();
	}

	I cast();

	@Nullable
	Component lastComponent();

	I add(Component line);

	S singleLine();

	R create();

	boolean isEmpty();
}
