package forestry.book;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.api.book.IBookCategory;
import forestry.api.book.IBookEntry;
import forestry.api.book.IBookEntryBuilder;
import forestry.api.book.IBookPageFactory;
import forestry.book.pages.JsonPageFactory;

@OnlyIn(Dist.CLIENT)
public class BookEntryBuilder implements IBookEntryBuilder {
	private final String name;
	private final IBookCategory category;
	private ItemStack stack = ItemStack.EMPTY;
	private IBookPageFactory loader = JsonPageFactory.INSTANCE;
	private List<IBookEntryBuilder> subEntries = new LinkedList<>();
	private String title = "missing.title";
	private BookContent[][] content = new BookContent[0][0];

	BookEntryBuilder(IBookCategory category, String name) {
		this.category = category;
		this.name = name;
	}

	@Override
	public BookEntryBuilder setStack(ItemStack stack) {
		this.stack = stack;
		return this;
	}

	@Override
	public BookEntryBuilder setLoader(IBookPageFactory loader) {
		this.loader = loader;
		return this;
	}

	@Override
	public BookEntryBuilder createSubEntry(String name, ItemStack stack) {
		BookEntryBuilder builder = new BookEntryBuilder(category, name).setStack(stack);
		subEntries.add(builder);
		return builder;
	}

	@Override
	public IBookEntryBuilder setContent(BookContent[][] content) {
		this.content = content;
		return this;
	}

	@Override
	public IBookEntryBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
	public IBookEntry build(@Nullable IBookEntry parent) {
		return new BookEntry(name, stack, loader, entry -> subEntries.stream().map(builder -> builder.build(entry)).toArray(IBookEntry[]::new), content, title, parent);
	}

	@Override
	public IBookEntry build() {
		return build(null);
	}

	@Override
	public IBookCategory addToCategory() {
		category.addEntry(build());
		return category;
	}
}
