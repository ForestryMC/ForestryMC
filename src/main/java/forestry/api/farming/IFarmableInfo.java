package forestry.api.farming;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.item.ItemStack;

/**
 * IFarmableInfo describes the valid germlings and possible products of an IFarmable. This is mainly used by the jei farming
 * category to display the valid germlings and possible products.
 */
public interface IFarmableInfo {

	/***
	 * @return The identifier of the IFarmable.
	 */
	String getIdentifier();

	default void addGermlings(ItemStack... germlings) {
		addGermlings(Arrays.asList(germlings));
	}

	void addGermlings(Collection<ItemStack> germlings);

	/**
	 * @return a collection that contains all valid germlings of a farmable.
	 */
	Collection<ItemStack> getGermlings();

	default void addProducts(ItemStack... products) {
		addProducts(Arrays.asList(products));
	}

	void addProducts(Collection<ItemStack> products);

	/**
	 * @return a collection that contains all possible products of a farmable.
	 */
	Collection<ItemStack> getProducts();
}
