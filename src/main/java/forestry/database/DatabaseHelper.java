package forestry.database;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

public class DatabaseHelper {
	public static boolean ascending;

	public static final Comparator<DatabaseItem> SORT_BY_NAME = (DatabaseItem firstStack, DatabaseItem secondStack) -> {
		if (firstStack.itemStack.isEmpty() && !secondStack.itemStack.isEmpty()) {
			return 1;
		} else if (!firstStack.itemStack.isEmpty() && secondStack.itemStack.isEmpty()) {
			return -1;
		}
		if (ascending) {
			return getItemName(firstStack.itemStack).getString().compareToIgnoreCase(getItemName(secondStack.itemStack).getString());
		}
		return getItemName(secondStack.itemStack).getString().compareToIgnoreCase(getItemName(firstStack.itemStack).getString());
	};

	//TODO simplify this?
	public static Component getItemName(ItemStack itemStack) {
		try {
			Component name = itemStack.getHoverName();
			if (name.getString().isEmpty()) {
				name = Component.translatable(itemStack.getItem().getDescriptionId(itemStack));
			}
			return name;
		} catch (final Exception errA) {
			try {
				String name = itemStack.getDescriptionId();
				return Component.translatable(name);
			} catch (final Exception errB) {
				return Component.literal("Exception");
			}
		}
	}

	public static void update(String searchText, List<DatabaseItem> items, ArrayList<DatabaseItem> sorted) {
		sorted.clear();
		sorted.ensureCapacity(items.size());

		Pattern pattern;
		try {
			pattern = Pattern.compile(searchText.toLowerCase(Locale.ENGLISH), Pattern.CASE_INSENSITIVE);
		} catch (Throwable ignore) {
			try {
				pattern = Pattern.compile(Pattern.quote(searchText.toLowerCase(Locale.ENGLISH)), Pattern.CASE_INSENSITIVE);
			} catch (Throwable e) {
				return;
			}
		}

		List<Predicate<ItemStack>> filters = getFilters(pattern);
		//boolean hasAddedItem;
		for (DatabaseItem databaseItem : items) {
			ItemStack item = databaseItem.itemStack;
			for (Predicate<ItemStack> filter : filters) {
				if (filter.test(item)) {
					sorted.add(databaseItem);
					break;
				}
			}
		}

		sorted.sort(SORT_BY_NAME);
	}

	//TODO: Add more filter options
	private static List<Predicate<ItemStack>> getFilters(Pattern pattern) {
		List<Predicate<ItemStack>> filters = new LinkedList<>();
		filters.add(new DatabaseFilterName(pattern));
		filters.add(new DatabaseFilterToolTip(pattern));
		return filters;
	}
}
