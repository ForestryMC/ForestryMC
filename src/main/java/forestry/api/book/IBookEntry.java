package forestry.api.book;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public interface IBookEntry {

	/**
	 * @return The stack that is displayed before the entry name.
	 */
	ItemStack getStack();

	/**
	 * @return A unique string identifier for this entry.
	 */
	String getName();

	String getLocalizedPages();

	IBookPageFactory getPageFactory();

	/**
	 * All sub entries of this entry are reachable over the buttons on the right side of the book.
	 */
	IBookEntry[] getSubEntries();

	@Nullable
	IBookEntry getParent();

	String getTitle();

	BookContent[][] getContent();

}
