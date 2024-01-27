package forestry.api.genetics.products;

import java.util.Collection;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

/**
 * A dynamic extension of the {@link IProductList}. With this it is possible to create products dynamically based on the
 * world around the provider (e.g. bees or trees).
 * <p>
 * It is important to return all products that can be added through this list with {@link #getPossibleProducts()} so the
 * player knows what the provider produces.
 */
public interface IDynamicProductList extends IProductList {
	Collection<Product> getPossibleProducts();

	/**
	 * Adds the products of this provider to the given {@link NonNullList} that contains all products of the provider
	 * that are produced in this cycle.
	 *
	 * @param reader   The world that the provider exists in
	 * @param pos      The position of the provider
	 * @param stacks   All products of the provider that are produced in this cycle.
	 * @param modifier A function that is used by the provider to modify the chance of the product.
	 * @param rand     The instance of {@link Random} that should be used. In the most cases this is
	 *                 {@link net.minecraft.world.level.Level#random}.
	 */
	default void addProducts(BlockGetter reader, BlockPos pos, NonNullList<ItemStack> stacks, Function<Product, Float> modifier, RandomSource rand) {
		addProducts(stacks, modifier, rand);
	}
}
