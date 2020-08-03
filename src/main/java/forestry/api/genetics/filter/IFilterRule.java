package forestry.api.genetics.filter;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IFilterRule {
    boolean isValid(ItemStack itemStack, IFilterData data);

    /**
     * If a root with this uid is registered, the filter will only get stack with individuals from this root.
     */
    @Nullable
    default String getRootUID() {
        return null;
    }
}
