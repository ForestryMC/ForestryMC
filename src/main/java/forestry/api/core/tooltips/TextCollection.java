package forestry.api.core.tooltips;

import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextCollection implements ITextInstance<TextCollection, TextCompound, TextCollection> {
    private final List<ITextComponent> lines = new ArrayList<>();
    @Nullable
    private ITextComponent last;

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
    public ITextComponent lastComponent() {
        return last;
    }

    @Override
    public TextCollection add(ITextComponent line) {
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

    public List<ITextComponent> getLines() {
        return Collections.unmodifiableList(lines);
    }
}
