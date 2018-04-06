package forestry.book.data;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.BookContent;

@SideOnly(Side.CLIENT)
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
	public String loader = "json";
	/**
	 * The item that will be displayed next to the title.
	 */
	public ItemStack icon = ItemStack.EMPTY;

	public EntryData() {
	}
}
