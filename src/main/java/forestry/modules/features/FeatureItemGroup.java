package forestry.modules.features;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.core.IItemSubtype;

public class FeatureItemGroup<I extends Item, S extends IItemSubtype> {

	private final Map<S, FeatureItem<I>> itemByType = new HashMap<>();

	public FeatureItemGroup(IFeatureRegistry registry, String identifier, Function<S, I> constructor, S[] subTypes) {
		Arrays.stream(subTypes).forEach(subType -> itemByType.put(subType, registry.item(() -> constructor.apply(subType), (identifier.isEmpty() ? "" : identifier + '_') + subType.getName())));
	}

	public boolean has(S subType) {
		return itemByType.containsKey(subType);
	}

	public FeatureItem<I> get(S subType) {
		return itemByType.get(subType);
	}

	public Map<S, FeatureItem<I>> getItemByType() {
		return Collections.unmodifiableMap(itemByType);
	}

	public Collection<FeatureItem<I>> getFeatures() {
		return itemByType.values();
	}

	public boolean itemEqual(ItemStack stack) {
		return getFeatures().stream().anyMatch(f -> f.itemEqual(stack));
	}

	public boolean itemEqual(Item item) {
		return getFeatures().stream().anyMatch(f -> f.itemEqual(item));
	}

	public ItemStack stack(S subType) {
		return stack(subType, 1);
	}

	public ItemStack stack(S subType, int amount) {
		FeatureItem<I> featureItem = itemByType.get(subType);
		if (featureItem == null) {
			throw new IllegalStateException("This feature group has no item registered for the given sub type to create a stack for.");
		}
		return featureItem.stack(amount);
	}

	public ItemStack stack(S subType, StackOption... options) {
		FeatureItem<I> featureItem = itemByType.get(subType);
		if (featureItem == null) {
			throw new IllegalStateException("This feature group has no item registered for the given sub type to create a stack for.");
		}
		return featureItem.stack(options);
	}
}
