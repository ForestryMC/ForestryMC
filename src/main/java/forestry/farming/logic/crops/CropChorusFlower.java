package forestry.farming.logic.crops;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.event.ForgeEventFactory;

import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;

public class CropChorusFlower extends Crop {
	private static final BlockState BLOCK_STATE = Blocks.CHORUS_FLOWER.getDefaultState();

	public CropChorusFlower(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() == Blocks.CHORUS_FLOWER;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		NonNullList<ItemStack> harvested = NonNullList.create();
		harvested.add(new ItemStack(Blocks.CHORUS_FLOWER));
		float chance = ForgeEventFactory.fireBlockHarvesting(harvested, world, pos, BLOCK_STATE, 0, 1.0F, false, null);

		harvested.removeIf(next -> world.rand.nextFloat() > chance);

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, BLOCK_STATE);
		NetworkUtil.sendNetworkPacket(packet, pos, world);

		world.removeBlock(pos, false);

		return harvested;
	}
}
