package forestry.api.genetics.products;

import java.util.Collection;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;

/**
 * A list of products that a bee or tree produces.
 */
public interface IProductList {

	/**
	 * @return A collection with all constant products of this list.
	 */
	Collection<Product> getConstantProducts();

	default Collection<Product> getPossibleProducts() {
		return getConstantProducts();
	}

	default NonNullList<ItemStack> getPossibleStacks() {
		NonNullList<ItemStack> stacks = NonNullList.create();
		getPossibleProducts().forEach(product -> stacks.add(product.getStack()));
		return stacks;
	}

	/**
	 * Adds the products of this provider to the given {@link NonNullList} that contains all products of the provider
	 * that are produced in this cycle.
	 *
	 * @param stacks   All products of the provider that are produced in this cycle.
	 * @param modifier A function that is used by the provider to modify the chance of the product.
	 * @param rand     The instance of {@link Random} that should be used. In the most cases this is
	 *                 {@link net.minecraft.world.level.Level#random}.
	 */
	void addProducts(NonNullList<ItemStack> stacks, Function<Product, Float> modifier, RandomSource rand);
}
