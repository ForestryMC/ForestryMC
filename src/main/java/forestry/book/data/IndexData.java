package forestry.book.data;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IndexData {
	/**
	 * All entries of this index.
	 */
	public IndexEntry[] entries = new IndexEntry[0];
}
