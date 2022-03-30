package forestry.api.core.tooltips;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.Component;

public class TextCollection implements ITextInstance<TextCollection, TextCompound, TextCollection> {
	private final List<Component> lines = new ArrayList<>();
	@Nullable
	private Component last;

	@Override
	public TextCompound singleLine() {
		return new TextCompound(this);
	}

	@Override
	public TextCollection create() {
		return this;
	}

	@Override
	public boolean isEmpty() {
		return lines.isEmpty();
	}

	@Nullable
	@Override
	public Component lastComponent() {
		return last;
	}

	@Override
	public TextCollection add(Component line) {
		lines.add(line);
		last = line;
		return this;
	}

	public TextCollection addAll(@Nullable TextCollection lines) {
		if (lines == null) {
			return this;
		}
		addAll(lines.getLines());
		return this;
	}

	@Override
	public TextCollection cast() {
		return this;
	}

	public void clear() {
		lines.clear();
	}

	public List<Component> getLines() {
		return Collections.unmodifiableList(lines);
	}
}
