package forestry.database;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class DatabaseHelper {
    public static boolean ascending;

    public static final Comparator<DatabaseItem> SORT_BY_NAME = (DatabaseItem firstStack, DatabaseItem secondStack) -> {
        if (firstStack.itemStack.isEmpty() && !secondStack.itemStack.isEmpty()) {
            return 1;
        } else if (!firstStack.itemStack.isEmpty() && secondStack.itemStack.isEmpty()) {
            return -1;
        }
        if (ascending) {
            return getItemName(firstStack.itemStack).getString()
                                                    .compareToIgnoreCase(getItemName(secondStack.itemStack).getString());
        }
        return getItemName(secondStack.itemStack).getString()
                                                 .compareToIgnoreCase(getItemName(firstStack.itemStack).getString());
    };

    public static ITextComponent getItemName(ItemStack itemStack) {
        return itemStack.getDisplayName();
    }

    public static void update(String searchText, List<DatabaseItem> items, ArrayList<DatabaseItem> sorted) {
        sorted.clear();
        sorted.ensureCapacity(items.size());

        Pattern pattern;
        try {
            pattern = Pattern.compile(searchText.toLowerCase(Locale.ENGLISH), Pattern.CASE_INSENSITIVE);
        } catch (Throwable ignore) {
            try {
                pattern = Pattern.compile(
                        Pattern.quote(searchText.toLowerCase(Locale.ENGLISH)),
                        Pattern.CASE_INSENSITIVE
                );
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
