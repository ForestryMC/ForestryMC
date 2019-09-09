package forestry.core.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

public class BlockStairs extends StairsBlock {

	public BlockStairs(BlockState blockState) {
		super(blockState, Properties.from(blockState.getBlock()));
	}
}
