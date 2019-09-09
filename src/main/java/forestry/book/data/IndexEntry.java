package forestry.book.data;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
/**
 * A entry of an index.
 */
@OnlyIn(Dist.CLIENT)
public class IndexEntry {
	/**
	 * The localized name of the entry.
	 */
	public String title;
	/**
	 * The page that a mouse click on this entry opens.
	 */
	public int page;
}
