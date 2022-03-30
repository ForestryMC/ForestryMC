package forestry.farming.logic.crops;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;

public class CropChorusFlower extends Crop {
	private static final BlockState BLOCK_STATE = Blocks.CHORUS_FLOWER.defaultBlockState();

	public CropChorusFlower(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	protected boolean isCrop(Level world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() == Blocks.CHORUS_FLOWER;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(Level world, BlockPos pos) {
		NonNullList<ItemStack> harvested = NonNullList.create();
		harvested.add(new ItemStack(Blocks.CHORUS_FLOWER));
		//TODO: Fix dropping
		//float chance = ForgeEventFactory.fireBlockHarvesting(harvested, world, pos, BLOCK_STATE, 0, 1.0F, false, null);
		float chance = 1.0F;
		harvested.removeIf(next -> world.random.nextFloat() > chance);

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, BLOCK_STATE);
		NetworkUtil.sendNetworkPacket(packet, pos, world);

		world.removeBlock(pos, false);

		return harvested;
	}
}
