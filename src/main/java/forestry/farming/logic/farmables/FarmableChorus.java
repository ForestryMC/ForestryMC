package forestry.farming.logic.farmables;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

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
	public boolean isSaplingAt(Level world, BlockPos pos, BlockState blockState) {
		return blockState.getBlock() == Blocks.CHORUS_FLOWER;
	}

	@Nullable
	@Override
	public ICrop getCropAt(Level world, BlockPos pos, BlockState blockState) {
		if (blockState.getBlock() != Blocks.CHORUS_FLOWER) {
			return null;
		}

		if (blockState.getValue(ChorusFlowerBlock.AGE) < 5) {
			return null;
		}

		return new CropChorusFlower(world, pos);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return ItemStack.isSame(germling, itemstack);
	}

	@Override
	public void addInformation(IFarmableInfo info) {
		info.addSeedlings(germling);
		info.addProducts(germling, fruit);
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return ItemStack.isSame(fruit, itemstack);
	}

	@Override
	public boolean plantSaplingAt(Player player, ItemStack germling, Level world, BlockPos pos) {
		if (!canPlace(world, pos)) {
			return false;
		}
		return BlockUtil.setBlockWithPlaceSound(world, pos, Blocks.CHORUS_FLOWER.defaultBlockState());
	}

	private boolean canPlace(Level world, BlockPos position) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				BlockPos offsetPosition = position.offset(x, 0, z);
				BlockState state = world.getBlockState(offsetPosition);
				if (state.getBlock() == Blocks.CHORUS_FLOWER || state.getBlock() == Blocks.CHORUS_PLANT) {
					return false;
				}
			}
		}

		return true;
	}
}
