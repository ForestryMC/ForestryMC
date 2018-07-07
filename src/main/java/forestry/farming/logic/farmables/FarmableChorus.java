package forestry.farming.logic.farmables;

import javax.annotation.Nullable;

import net.minecraft.block.BlockChorusFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.farming.IFarmableInfo;
import forestry.core.utils.BlockUtil;
import forestry.farming.logic.crops.CropChorusFlower;

public class FarmableChorus implements IFarmable {
	public static final IFarmable INSTANCE = new FarmableChorus();

	private final ItemStack germling;
	private final ItemStack fruit;

	private FarmableChorus() {
		this.germling = new ItemStack(Blocks.CHORUS_FLOWER);
		this.fruit = new ItemStack(Items.CHORUS_FRUIT);
	}

	@Override
	public boolean isSaplingAt(World world, BlockPos pos, IBlockState blockState) {
		return blockState.getBlock() == Blocks.CHORUS_FLOWER;
	}

	@Nullable
	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		if (blockState.getBlock() != Blocks.CHORUS_FLOWER) {
			return null;
		}

		if (blockState.getValue(BlockChorusFlower.AGE) < 5) {
			return null;
		}

		return new CropChorusFlower(world, pos);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStack.areItemsEqual(germling, itemstack);
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		info.addGermlings(germling);
		info.addProducts(germling, fruit);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return ItemStack.areItemsEqual(fruit, itemstack);
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		if (!canPlace(world, pos)) {
			return false;
		}
		return BlockUtil.setBlockWithPlaceSound(world, pos, Blocks.CHORUS_FLOWER.getDefaultState());
	}

	private boolean canPlace(World world, BlockPos position) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				BlockPos offsetPosition = position.add(x, 0, z);
				IBlockState state = world.getBlockState(offsetPosition);
				if (state.getBlock() == Blocks.CHORUS_FLOWER || state.getBlock() == Blocks.CHORUS_PLANT) {
					return false;
				}
			}
		}

		return true;
	}
}
