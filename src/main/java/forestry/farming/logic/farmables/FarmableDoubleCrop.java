package forestry.farming.logic.farmables;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.farming.logic.crops.CropDestroyDouble;

public class FarmableDoubleCrop extends FarmableBase {
	private final IBlockState topMatureState;

	public FarmableDoubleCrop(ItemStack germling, IBlockState plantedState, IBlockState matureState, IBlockState topMatureState, boolean replant) {
		super(germling, plantedState, matureState, replant);
		this.topMatureState = topMatureState;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, IBlockState blockState) {
		return blockState.getBlock() == plantedState.getBlock() && blockState != topMatureState;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		BlockPos posUp = pos.up();
		IBlockState stateUp = world.getBlockState(posUp);
		if (blockState != matureState || stateUp != topMatureState) {
			return null;
		}
		return new CropDestroyDouble(world, blockState, stateUp, pos, replant ? plantedState : null);
	}
}
