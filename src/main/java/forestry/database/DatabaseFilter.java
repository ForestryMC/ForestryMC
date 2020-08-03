package forestry.database;

import net.minecraft.item.ItemStack;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class DatabaseFilter implements Predicate<ItemStack> {
    protected final Pattern pattern;

    protected DatabaseFilter(Pattern pattern) {
        this.pattern = pattern;
    }
}
