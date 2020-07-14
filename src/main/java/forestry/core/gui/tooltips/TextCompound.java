package forestry.core.gui.tooltips;

import javax.annotation.Nullable;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;

/**
 * Helper class to allow simple appending of siblings to a text collection.
 */
public class TextCompound implements ITextInstance {
	private final TextCollection parent;
	@Nullable
	private IFormattableTextComponent root;

	public TextCompound(TextCollection parent) {
		this.parent = parent;
	}

	@Nullable
	@Override
	public ITextComponent lastComponent() {
		return root;
	}

	public ITextInstance add(ITextComponent line) {
		if (root == null) {
			if (!(line instanceof IFormattableTextComponent)) {
				return this;
			}
			root = (IFormattableTextComponent) line;
			return this;
		}
		root.func_230529_a_(line);
		return this;
	}

	@Override
	public TextCompound singleLine() {
		return this;
	}

	public TextCollection end() {
		if (root != null) {
			parent.add(root);
		}
		return parent;
	}
}
