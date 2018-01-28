package forestry.book.data;

import net.minecraft.item.ItemStack;

import forestry.api.book.BookContent;

public class EntryData {
	public String title;
	public BookContent[][] content;
	public String[] subEntries = new String[0];
	public ItemStack icon = ItemStack.EMPTY;
}
