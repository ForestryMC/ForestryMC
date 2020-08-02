package forestry.api.farming;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.item.ItemStack;

/**
 * IFarmableInfo describes the valid seedlings and possible products of an IFarmable. This is mainly used by the jei farming
 * category to display the valid seedlings and possible products.
 */
public interface IFarmableInfo {

    /***
     * @return The identifier of the IFarmable.
     */
    String getIdentifier();

    default void addSeedlings(ItemStack... seedlings) {
        addSeedlings(Arrays.asList(seedlings));
    }

    void addSeedlings(Collection<ItemStack> seedlings);

    /**
     * @return a collection that contains all valid seedlings of a farmable.
     */
    Collection<ItemStack> getSeedlings();

    default void addProducts(ItemStack... products) {
        addProducts(Arrays.asList(products));
    }

    void addProducts(Collection<ItemStack> products);

    /**
     * @return a collection that contains all possible products of a farmable.
     */
    Collection<ItemStack> getProducts();
}
