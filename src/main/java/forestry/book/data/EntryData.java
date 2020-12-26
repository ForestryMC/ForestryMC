package forestry.book.data;

import forestry.api.book.BookContent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntryData {
    /**
     * The localized title of the entry.
     */
    public final String title = "";
    /**
     * The content that gets displayed on the pages of the entry.
     */
    public final BookContent[][] content = new BookContent[0][0];
    /**
     * All sub entries of this entry.
     */
    public final String[] subEntries = new String[0];
    /**
     * The name of the page factory.
     */
    public final String loader = "json";
    /**
     * The item that will be displayed next to the title.
     */
    public final ItemStack icon = ItemStack.EMPTY;

    public EntryData() {
    }
}
