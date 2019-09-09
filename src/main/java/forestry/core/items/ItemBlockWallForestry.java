package forestry.core.items;

import javax.annotation.Nullable;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IWorldReader;

public class ItemBlockWallForestry<B extends Block, W extends Block> extends ItemBlockForestry<B> {
	private final W wallBlock;

	public ItemBlockWallForestry(B block, W wallBlock, Properties builder) {
		super(block, builder);
		this.wallBlock = wallBlock;
	}

	public ItemBlockWallForestry(B block, W wallBlock) {
		super(block);
		this.wallBlock = wallBlock;
	}

	@Nullable
	protected BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = this.wallBlock.getStateForPlacement(context);
		BlockState blockstate1 = null;
		IWorldReader iworldreader = context.getWorld();
		BlockPos blockpos = context.getPos();

		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction != Direction.UP) {
				BlockState blockstate2 = direction == Direction.DOWN ? this.getBlock().getStateForPlacement(context) : blockstate;
				if (blockstate2 != null && blockstate2.isValidPosition(iworldreader, blockpos)) {
					blockstate1 = blockstate2;
					break;
				}
			}
		}

		return blockstate1 != null && iworldreader.func_217350_a(blockstate1, blockpos, ISelectionContext.dummy()) ? blockstate1 : null;
	}

	@Override
	public void addToBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
		super.addToBlockToItemMap(blockToItemMap, itemIn);
		blockToItemMap.put(this.wallBlock, itemIn);
	}

	@Override
	public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
		super.removeFromBlockToItemMap(blockToItemMap, itemIn);
		blockToItemMap.remove(this.wallBlock);
	}
}
