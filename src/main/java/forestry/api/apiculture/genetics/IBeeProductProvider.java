package forestry.api.apiculture.genetics;

import net.minecraft.item.ItemStack;

import java.util.function.BiConsumer;

public interface IBeeProductProvider {
    default void addProducts(BiConsumer<ItemStack, Float> registry) {
    }

    default void addSpecialties(BiConsumer<ItemStack, Float> registry) {
    }
}
