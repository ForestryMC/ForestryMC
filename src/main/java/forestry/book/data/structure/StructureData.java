package forestry.book.data.structure;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StructureData {
	public int[] size = new int[0];
	public BlockData[] structure = new BlockData[0];

	public StructureData() {
	}
}
