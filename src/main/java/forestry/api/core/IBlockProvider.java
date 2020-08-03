package forestry.api.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.Collection;

public interface IBlockProvider<B extends Block, I extends Item> extends IItemProvider<I> {
    boolean hasBlock();

    @Nullable
    B getBlock();

    Block block();

    Collection<B> collect();

    default boolean blockEqual(BlockState state) {
        return blockEqual(state.getBlock());
    }

    default boolean blockEqual(Block block) {
        return hasBlock() && block() == block;
    }
}
