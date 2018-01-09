package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

public interface IFilterLogic {
	boolean isValid(ItemStack itemStack, IFilterData data);

	/**
	 * If a root with this uid is registered, the filter will only get stack with individuals from this root.
	 */
	@Nullable
	default String getRootUID() {
		return null;
	}
}
