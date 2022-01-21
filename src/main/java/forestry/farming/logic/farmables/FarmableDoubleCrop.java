package forestry.farming.logic.farmables;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.ICrop;
import forestry.farming.logic.crops.CropDestroyDouble;

public class FarmableDoubleCrop extends FarmableBase {
	private final BlockState topMatureState;

	public FarmableDoubleCrop(ItemStack germling, BlockState plantedState, BlockState matureState, BlockState topMatureState, boolean replant) {
		super(germling, plantedState, matureState, replant);
		this.topMatureState = topMatureState;
	}

	@Override
	public boolean isSaplingAt(Level world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == plantedState.getBlock() && blockState != topMatureState;
	}

	@Override
	public ICrop getCropAt(Level world, BlockPos pos, BlockState blockState) {
		BlockPos posUp = pos.above();
		BlockState stateUp = world.getBlockState(posUp);
		if (blockState != matureState || stateUp != topMatureState) {
			return null;
		}
		return new CropDestroyDouble(world, blockState, stateUp, pos, replant ? plantedState : null);
	}
}
