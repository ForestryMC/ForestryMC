package forestry.api.apiculture.genetics;

import java.util.function.BiConsumer;

import net.minecraft.item.ItemStack;

public interface IBeeProductProvider {
	default void addProducts(BiConsumer<ItemStack, Float> registry) {
	}

	default void addSpecialties(BiConsumer<ItemStack, Float> registry) {
	}
}
