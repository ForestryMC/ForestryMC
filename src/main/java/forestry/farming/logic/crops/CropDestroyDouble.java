package forestry.farming.logic.crops;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;

public class CropDestroyDouble extends Crop {

	protected final IBlockState blockState;
	protected final IBlockState blockStateUp;
	@Nullable
	protected final IBlockState replantState;

	public CropDestroyDouble(World world, IBlockState blockState, IBlockState blockStateUp, BlockPos position, @Nullable IBlockState replantState) {
		super(world, position);
		this.blockState = blockState;
		this.blockStateUp = blockStateUp;
		this.replantState = replantState;
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return world.getBlockState(pos) == blockState;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		Block block = blockState.getBlock();
		Block blockUp = blockStateUp.getBlock();
		NonNullList<ItemStack> harvested = NonNullList.create();
		block.getDrops(harvested, world, pos, blockState, 0);
		blockUp.getDrops(harvested, world, pos.up(), blockStateUp, 0);

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
		NetworkUtil.sendNetworkPacket(packet, pos, world);

		world.setBlockToAir(pos.up());
		if (replantState != null) {
			world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNC);
		} else {
			world.setBlockToAir(pos);
		}

		return harvested;
	}

	@Override
	public String toString() {
		return String.format("CropDestroyDouble [ position: [ %s ]; block: %s ]", position.toString(), blockState);
	}
}
