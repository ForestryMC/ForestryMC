package forestry.api.genetics.products;

import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

/**
 * A mutable version of the {@link IProductList} this will later be {@link #bake()}d to a {@link IProductList} and can
 * be used to add products.
 */
public interface IMutableProductList {
    /**
     * Adds a product directly to the list.
     *
     * @param stack  The stack of the product.
     * @param chance The chance of the product from 0F to 1.0F
     * @return The instance of the mutable product list for builder chaining.
     */
    IMutableProductList addProduct(ItemStack stack, float chance);

    /**
     * Adds a product that later will be added to the list. {@link Supplier#get()} of the given supplier will be called
     * later at {@link #bake()}. This can be used to lazy load products and is needed because not all items are available
     * at the creation of the mutable list.
     *
     * @param stack  A supplier that returns the product stack.
     * @param chance The chance of the product from 0F to 1.0F
     * @return The instance of the mutable product list for builder chaining.
     */
    IMutableProductList addProduct(Supplier<ItemStack> stack, float chance);

    /**
     * Adds a product list that will be baked and added to the baked list of this instance.
     *
     * @return The instance of the mutable product list for builder chaining.
     */
    IMutableProductList addList(IMutableProductList list);

    /**
     * Adds a dynamic product list to this list. This will be stored in the baked {@link IProductList} and used later
     * in the game.
     *
     * @return The instance of the mutable product list for builder chaining.
     */
    IMutableProductList addDynamic(IDynamicProductList list);

    /**
     * @return Baked this mutable list to a immutable version.
     */
    IProductList bake();
}
