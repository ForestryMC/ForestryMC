package forestry.core.items;

import javax.annotation.Nullable;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.item.Item.Properties;

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
	protected BlockState getPlacementState(BlockPlaceContext context) {
		BlockState blockstate = this.wallBlock.getStateForPlacement(context);
		BlockState blockstate1 = null;
		LevelReader iworldreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();

		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction != Direction.UP) {
				BlockState blockstate2 = direction == Direction.DOWN ? this.getBlock().getStateForPlacement(context) : blockstate;
				if (blockstate2 != null && blockstate2.canSurvive(iworldreader, blockpos)) {
					blockstate1 = blockstate2;
					break;
				}
			}
		}

		return blockstate1 != null && iworldreader.isUnobstructed(blockstate1, blockpos, CollisionContext.empty()) ? blockstate1 : null;
	}

	@Override
	public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
		super.registerBlocks(blockToItemMap, item);
		blockToItemMap.put(this.wallBlock, item);
	}

	@Override
	public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
		super.removeFromBlockToItemMap(blockToItemMap, itemIn);
		blockToItemMap.remove(this.wallBlock);
	}
}
