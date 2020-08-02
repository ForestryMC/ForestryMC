package forestry.book.features;

import forestry.book.ModuleBook;
import forestry.book.items.ItemForesterBook;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class BookItems {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleBook.class);

    public static final FeatureItem<ItemForesterBook> BOOK = REGISTRY.item(ItemForesterBook::new, "book_forester");

    private BookItems() {
    }
}
