package forestry.book;

import forestry.api.book.BookContent;
import forestry.api.book.IBookEntry;
import forestry.api.book.IBookPageFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class BookEntry implements IBookEntry {
    private final String name;
    private final ItemStack stack;
    private final IBookPageFactory loader;
    @Nullable
    private final IBookEntry parent;
    private final IBookEntry[] subEntries;
    private final BookContent[][] content;
    private final String title;

    BookEntry(String name, ItemStack stack, IBookPageFactory loader, Function<IBookEntry, IBookEntry[]> subEntryFactory, BookContent[][] content, String title, @Nullable IBookEntry parent) {
        this.name = name;
        this.stack = stack;
        this.loader = loader;
        this.subEntries = subEntryFactory.apply(this);
        this.content = content;
        this.title = title;
        this.parent = parent;
    }

    @Override
    public BookContent[][] getContent() {
        return content;
    }

    @Override
    public IBookPageFactory getPageFactory() {
        return loader;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ITextComponent getTitle() {
        return new TranslationTextComponent(title);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IBookEntry[] getSubEntries() {
        return subEntries;
    }

    @Override
    @Nullable
    public IBookEntry getParent() {
        return parent;
    }
}
