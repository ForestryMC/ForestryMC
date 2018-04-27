package forestry.book.data.structure;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class StructureData {
	public int[] size = new int[0];
	public BlockData[] structure = new BlockData[0];

	public StructureData() {
	}
}
