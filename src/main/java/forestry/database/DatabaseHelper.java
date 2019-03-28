package forestry.database;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;

public class DatabaseHelper {
	public static boolean ascending;

	public static final Comparator<DatabaseItem> SORT_BY_NAME = (DatabaseItem firstStack, DatabaseItem secondStack) -> {
		if (firstStack.itemStack.isEmpty() && !secondStack.itemStack.isEmpty()) {
			return 1;
		} else if (!firstStack.itemStack.isEmpty() && secondStack.itemStack.isEmpty()) {
			return -1;
		}
		if (ascending) {
			return getItemName(firstStack.itemStack).compareToIgnoreCase(getItemName(secondStack.itemStack));
		}
		return getItemName(secondStack.itemStack).compareToIgnoreCase(getItemName(firstStack.itemStack));
	};

	public static String getItemName(ItemStack itemStack) {
		try {
			String name = itemStack.getDisplayName();
			if (name == null || name.isEmpty()) {
				name = itemStack.getItem().getTranslationKey(itemStack);
			}
			return name == null ? "Null" : name;
		} catch (final Exception errA) {
			try {
				String name = itemStack.getTranslationKey();
				return name == null ? "Null" : name;
			} catch (final Exception errB) {
				return "Exception";
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

		List<Predicate<ItemStack>> filters = getFilters(pattern, searchText);
		//boolean hasAddedItem;
		for (DatabaseItem databaseItem : items) {
			ItemStack item = databaseItem.itemStack;
			final String name = DatabaseHelper.getItemName(item);
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
	private static List<Predicate<ItemStack>> getFilters(Pattern pattern, String searchText) {
		List<Predicate<ItemStack>> filters = new LinkedList<>();
		filters.add(new DatabaseFilterName(pattern));
		filters.add(new DatabaseFilterToolTip(pattern));
		return filters;
	}
}
