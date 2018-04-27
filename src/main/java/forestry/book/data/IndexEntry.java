package forestry.book.data;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A entry of an index.
 */
@SideOnly(Side.CLIENT)
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
