package forestry.farming.logic.farmables;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableRusticGrape implements IFarmable {
	public static final BooleanProperty GRAPES = BooleanProperty.create("grapes");

	private final Block cropBlock;

	public FarmableRusticGrape(Block cropBlock) {
		Preconditions.checkNotNull(cropBlock);

		this.cropBlock = cropBlock;
	}

	@Override
	public boolean isSaplingAt(Level world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == cropBlock;
	}

	@Override
	@Nullable
	public ICrop getCropAt(Level world, BlockPos pos, BlockState blockState) {
		if (blockState.getBlock() != cropBlock) {
			return null;
		}

		if (!blockState.getValue(GRAPES)) {
			return null;
		}

		BlockState replantState = getReplantState(blockState);
		return new CropDestroy(world, blockState, pos, replantState);
	}

	@Nullable
	protected BlockState getReplantState(BlockState blockState) {
		return blockState.setValue(GRAPES, false);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean plantSaplingAt(Player player, ItemStack germling, Level world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}
}
