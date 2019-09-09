package forestry.database;

import java.util.Locale;
import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;

public class DatabaseFilterName extends DatabaseFilter {
	public DatabaseFilterName(Pattern pattern) {
		super(pattern);
	}

	@Override
	public boolean test(ItemStack itemStack) {
		final String name = DatabaseHelper.getItemName(itemStack).getFormattedText();
		return pattern.matcher(name.toLowerCase(Locale.ENGLISH)).find() || itemStack.isEmpty();
	}
}
