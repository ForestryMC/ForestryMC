package forestry.farming.logic.farmables;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.farming.logic.crops.CropDestroyDouble;

public class FarmableDoubleCrop extends FarmableBase {
	private final BlockState topMatureState;

	public FarmableDoubleCrop(ItemStack germling, BlockState plantedState, BlockState matureState, BlockState topMatureState, boolean replant) {
		super(germling, plantedState, matureState, replant);
		this.topMatureState = topMatureState;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == plantedState.getBlock() && blockState != topMatureState;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, BlockState blockState) {
		BlockPos posUp = pos.up();
		BlockState stateUp = world.getBlockState(posUp);
		if (blockState != matureState || stateUp != topMatureState) {
			return null;
		}
		return new CropDestroyDouble(world, blockState, stateUp, pos, replant ? plantedState : null);
	}
}
