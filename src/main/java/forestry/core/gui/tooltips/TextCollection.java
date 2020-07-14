package forestry.core.gui.tooltips;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.text.ITextComponent;

public class TextCollection implements ITextInstance {
	private final List<ITextComponent> lines = new ArrayList<>();
	@Nullable
	private ITextComponent last;

	public TextCompound singleLine() {
		return new TextCompound(this);
	}

	@Override
	public TextCollection end() {
		return this;
	}

	@Nullable
	@Override
	public ITextComponent lastComponent() {
		return last;
	}

	@Override
	public ITextInstance add(ITextComponent line) {
		lines.add(line);
		last = line;
		return this;
	}

	public void clear() {
		lines.clear();
	}

	public List<ITextComponent> getLines() {
		return Collections.unmodifiableList(lines);
	}
}
