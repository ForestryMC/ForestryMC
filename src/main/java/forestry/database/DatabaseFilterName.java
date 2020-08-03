package forestry.database;

import net.minecraft.item.ItemStack;

import java.util.Locale;
import java.util.regex.Pattern;

public class DatabaseFilterName extends DatabaseFilter {
    public DatabaseFilterName(Pattern pattern) {
        super(pattern);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        final String name = DatabaseHelper.getItemName(itemStack).getString();
        return pattern.matcher(name.toLowerCase(Locale.ENGLISH)).find() || itemStack.isEmpty();
    }
}
