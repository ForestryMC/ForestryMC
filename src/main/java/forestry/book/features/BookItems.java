package forestry.book.features;

import forestry.book.items.ItemForesterBook;
import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class BookItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.BOOK);

	public static final FeatureItem<ItemForesterBook> BOOK = REGISTRY.item(ItemForesterBook::new, "book_forester");

	private BookItems() {
	}
}
