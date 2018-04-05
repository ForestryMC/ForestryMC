package forestry.book.data;

import net.minecraft.item.ItemStack;

import forestry.api.book.BookContent;

public class EntryData {
	/**
	 * The localized title of the entry.
	 */
	public String title = "";
	/**
	 * The content that gets displayed on the pages of the entry.
	 */
	public BookContent[][] content = new BookContent[0][0];
	/**
	 * All sub entries of this entry.
	 */
	public String[] subEntries = new String[0];
	/**
	 * The name of the page factory.
	 */
	public String loader;
	/**
	 * The item that will be displayed next to the title.
	 */
	public ItemStack icon = ItemStack.EMPTY;

	public EntryData() {
	}
}
