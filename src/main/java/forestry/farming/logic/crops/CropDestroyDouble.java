package forestry.farming.logic.crops;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CropDestroyDouble extends Crop {

    protected final BlockState blockState;
    protected final BlockState blockStateUp;
    @Nullable
    protected final BlockState replantState;

    public CropDestroyDouble(
            World world,
            BlockState blockState,
            BlockState blockStateUp,
            BlockPos position,
            @Nullable BlockState replantState
    ) {
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
        //		block.getDrops(harvested, world, pos, blockState, 0);
        //		blockUp.getDrops(harvested, world, pos.up(), blockStateUp, 0);
        //TODO getDrops. Loot tables?
        PacketFXSignal packet = new PacketFXSignal(
                PacketFXSignal.VisualFXType.BLOCK_BREAK,
                PacketFXSignal.SoundFXType.BLOCK_BREAK,
                pos,
                blockState
        );
        NetworkUtil.sendNetworkPacket(packet, pos, world);

        world.removeBlock(pos.up(), false);
        if (replantState != null) {
            world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNC);
        } else {
            world.removeBlock(pos, false);
        }

        return harvested;
    }

    @Override
    public String toString() {
        return String.format("CropDestroyDouble [ position: [ %s ]; block: %s ]", position.toString(), blockState);
    }
}
