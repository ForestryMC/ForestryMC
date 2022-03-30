package forestry.api.core.tooltips;

import javax.annotation.Nullable;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

/**
 * Helper class to allow simple appending of siblings to a text collection.
 */
public class TextCompound implements ITextInstance<TextCompound, TextCompound, TextCollection> {
	private final TextCollection parent;
	@Nullable
	private MutableComponent root;

	public TextCompound(TextCollection parent) {
		this.parent = parent;
	}

	@Nullable
	@Override
	public Component lastComponent() {
		return root;
	}

	@Override
	public TextCompound add(Component line) {
		if (root == null) {
			if (!(line instanceof MutableComponent)) {
				return this;
			}
			root = (MutableComponent) line;
			return this;
		}
		root.append(line);
		return this;
	}

	@Override
	public TextCompound singleLine() {
		return this;
	}

	@Override
	public TextCompound cast() {
		return this;
	}

	@Override
	public TextCollection create() {
		if (root != null) {
			parent.add(root);
		}
		return parent;
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}
}
