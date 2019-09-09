package forestry.modules.features;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IBlockProvider<B extends Block, I extends Item> extends IItemProvider<I> {
	boolean hasBlock();

	@Nullable
	B getBlock();

	Block block();
}
