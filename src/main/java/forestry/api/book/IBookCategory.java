package forestry.api.book;

import javax.annotation.Nullable;
import java.util.Collection;

import net.minecraft.item.ItemStack;

public interface IBookCategory {
	ItemStack getStack();

	IBookCategory setStack(ItemStack stack);

	IBookCategory addEntry(IBookEntry entry);

	IBookCategory addEntry(String name, ItemStack stack);

	IBookEntryBuilder createEntry(String name);

	IBookEntryBuilder createEntry(String name, ItemStack stack);

	Collection<IBookEntry> getEntries();

	@Nullable
	IBookEntry getEntry(String name);

	String getLocalizedName();

	String getTooltip();

	String getName();
}
