package forestry.book.data;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IndexData {
    /**
     * All entries of this index.
     */
    public final IndexEntry[] entries = new IndexEntry[0];
}
