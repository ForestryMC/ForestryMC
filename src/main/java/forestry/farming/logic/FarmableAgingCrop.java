package forestry.farming.logic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;

/**
 * For blocks that are harvestable once they are a certain age.
 */
public class FarmableAgingCrop implements IFarmable {
	protected final ItemStack germling;
	protected final Block cropBlock;
	protected final IProperty<Integer> ageProperty;
	protected final int minHarvestAge;
	@Nullable
	protected final Integer replantAge;

	public FarmableAgingCrop(@Nonnull ItemStack germling, @Nonnull Block cropBlock, @Nonnull IProperty<Integer> ageProperty, int minHarvestAge) {
		this(germling, cropBlock, ageProperty, minHarvestAge, null);
	}

	public FarmableAgingCrop(ItemStack germling, Block cropBlock, IProperty<Integer> ageProperty, int minHarvestAge, @Nullable Integer replantAge) {
		this.germling = germling;
		this.cropBlock = cropBlock;
		this.ageProperty = ageProperty;
		this.minHarvestAge = minHarvestAge;
		this.replantAge = replantAge;
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() == cropBlock && blockState.getValue(ageProperty) < minHarvestAge;
	}

	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		if (blockState.getBlock() != cropBlock) {
			return null;
		}

		if (blockState.getValue(ageProperty) < minHarvestAge) {
			return null;
		}

		if (replantAge != null) {
			IBlockState replantState = getReplantState(world, pos, blockState);
			return new CropDestroy(world, blockState, pos, replantState);
		} else {
			return new CropDestroy(world, blockState, pos, null);
		}
	}

	protected IBlockState getReplantState(World world, BlockPos pos, IBlockState blockState) {
		return blockState.withProperty(ageProperty, replantAge);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStack.areItemsEqual(germling, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		IBlockState plantedState = cropBlock.getDefaultState().withProperty(ageProperty, 0);
		return BlockUtil.setBlockWithPlaceSound(world, pos, plantedState);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}
}
