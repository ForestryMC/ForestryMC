package forestry.api.book;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public interface IBookEntryBuilder {

	IBookEntryBuilder setStack(ItemStack stack);

	IBookEntryBuilder setLoader(IBookPageFactory loader);

	default IBookEntryBuilder addSubEntry(String name){
		return addSubEntry(name, ItemStack.EMPTY);
	}

	IBookEntryBuilder addSubEntry(String name, ItemStack stack);

	IBookEntryBuilder setContent(BookContent[][] content);

	IBookEntryBuilder setTitle(String title);

	IBookEntry build(@Nullable IBookEntry parent);

	IBookEntry build();

	IBookCategory addToCategory();
}
