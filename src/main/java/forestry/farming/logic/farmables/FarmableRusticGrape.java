package forestry.farming.logic.farmables;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableRusticGrape implements IFarmable {
	public static final PropertyBool GRAPES = PropertyBool.create("grapes");

	private final Block cropBlock;

	public FarmableRusticGrape(Block cropBlock) {
		Preconditions.checkNotNull(cropBlock);

		this.cropBlock = cropBlock;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, IBlockState blockState) {
		return blockState.getBlock() == cropBlock;
	}

	@Override
	@Nullable
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		if (blockState.getBlock() != cropBlock) {
			return null;
		}

		if (!blockState.getValue(GRAPES)) {
			return null;
		}

		IBlockState replantState = getReplantState(blockState);
		return new CropDestroy(world, blockState, pos, replantState);
	}

	@Nullable
	protected IBlockState getReplantState(IBlockState blockState) {
		return blockState.withProperty(GRAPES, false);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}
}
