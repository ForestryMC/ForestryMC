package forestry.core.utils;

import java.util.Comparator;

import net.minecraft.util.math.BlockPos;

public class TopDownBlockPosComparator implements Comparator<BlockPos> {
	public static final TopDownBlockPosComparator INSTANCE = new TopDownBlockPosComparator();

	private TopDownBlockPosComparator() {

	}

	@Override
	public int compare(BlockPos o1, BlockPos o2) {
		return Integer.compare(o2.getY(), o1.getY());
	}
}
