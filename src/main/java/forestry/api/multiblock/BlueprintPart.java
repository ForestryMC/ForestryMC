package forestry.api.multiblock;

import java.util.function.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class BlueprintPart<S> {
	private final IBlockState blockState;
	private final Function<S, String> name;
	private final S source;

	public static BlueprintPart<ItemStack> create(IBlockState blockState, ItemStack source){
		return new BlueprintPart<>(blockState, source, ItemStack::getDisplayName);
	}

	public BlueprintPart(IBlockState blockState, S source, Function<S, String> name) {
		this.blockState = blockState;
		this.name = name;
		this.source = source;
	}

	public S getSource() {
		return source;
	}

	public IBlockState getBlockState() {
		return blockState;
	}

	public String getName() {
		return name.apply(source);
	}
}
