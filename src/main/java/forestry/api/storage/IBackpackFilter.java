package forestry.api.storage;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

/**
 * Filters items that can be put into a backpack.
 * You can get a specific backpack's filter with {@link IBackpackDefinition#getFilter()}.
 * <p>
 * For Backpack Implementers: you can create a new filter with {@link IBackpackInterface#createBackpackFilter()} or implement your own.
 *
 * @see IBackpackFilterConfigurable
 */
public interface IBackpackFilter extends Predicate<ItemStack> {
	/**
	 * Returns true if the ItemStack is a valid item for this backpack filter.
	 */
	@Override
	boolean test(ItemStack itemstack);
}
