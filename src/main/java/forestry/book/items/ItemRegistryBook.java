package forestry.book.items;

import forestry.core.items.ItemRegistry;

public class ItemRegistryBook extends ItemRegistry {
	public final ItemForesterBook book;

	public ItemRegistryBook() {
		book = registerItem(new ItemForesterBook(), "book_forester");
	}
}
