package forestry.database;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;

public abstract class DatabaseFilter implements Predicate<ItemStack> {
	protected final Pattern pattern;

	protected DatabaseFilter(Pattern pattern) {
		this.pattern = pattern;
	}
}
