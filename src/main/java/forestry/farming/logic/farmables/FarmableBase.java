package forestry.farming.logic.farmables;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.utils.BlockUtil;
import forestry.farming.logic.crops.CropDestroy;

public abstract class FarmableBase implements IFarmable {
	protected final ItemStack germling;
	protected final BlockState plantedState;
	protected final BlockState matureState;
	protected final boolean replant;

	public FarmableBase(ItemStack germling, BlockState plantedState, BlockState matureState, boolean replant) {
		this.germling = germling;
		this.plantedState = plantedState;
		this.matureState = matureState;
		this.replant = replant;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == plantedState.getBlock() && blockState != matureState;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, BlockState blockState) {
		if (blockState != matureState) {
			return null;
		}

		BlockState replantState = replant ? plantedState : null;
		return new CropDestroy(world, blockState, pos, replantState);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStack.areItemsEqual(germling, itemstack);
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		info.addGermlings(germling);
	}

	@Override
	public boolean plantSaplingAt(PlayerEntity player, ItemStack germling, World world, BlockPos pos) {
		return BlockUtil.setBlockWithPlaceSound(world, pos, plantedState);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}
}
