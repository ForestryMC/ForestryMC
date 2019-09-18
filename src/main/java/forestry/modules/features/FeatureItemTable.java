package forestry.modules.features;

import com.google.common.collect.ImmutableTable;

import java.util.Collection;
import java.util.function.BiFunction;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.api.core.IItemSubtype;

public class FeatureItemTable<I extends Item, R extends IItemSubtype, C extends IItemSubtype> {

	private final ImmutableTable<R, C, FeatureItem<I>> itemByTypes;

	public FeatureItemTable(IFeatureRegistry registry, String identifier, BiFunction<R, C, I> constructor, R[] rowTypes, C[] columnTypes) {
		ImmutableTable.Builder<R, C, FeatureItem<I>> builder = new ImmutableTable.Builder<>();
		for (R row : rowTypes) {
			for (C column : columnTypes) {
				builder.put(row, column, registry.item(() -> constructor.apply(row, column), (identifier.isEmpty() ? "" : identifier + '_') + row.getName() + "_" + column.getName()));
			}
		}
		itemByTypes = builder.build();
	}

	public boolean has(R rowType, C columnType) {
		return itemByTypes.contains(rowType, columnType);
	}

	public FeatureItem<I> get(R rowType, C columnType) {
		return itemByTypes.get(rowType, columnType);
	}

	public ImmutableTable<R, C, FeatureItem<I>> getItemByTypes() {
		return itemByTypes;
	}

	public Collection<FeatureItem<I>> getFeatures() {
		return itemByTypes.values();
	}

	public boolean itemEqual(ItemStack stack) {
		return getFeatures().stream().anyMatch(f -> f.itemEqual(stack));
	}

	public boolean itemEqual(Item item) {
		return getFeatures().stream().anyMatch(f -> f.itemEqual(item));
	}

	public ItemStack stack(R rowType, C columnType) {
		return stack(rowType, columnType, 1);
	}

	public ItemStack stack(R rowType, C columnType, int amount) {
		FeatureItem<I> featureItem = itemByTypes.get(rowType, columnType);
		if (featureItem == null) {
			throw new IllegalStateException("This feature group has no item registered for the given sub type to create a stack for.");
		}
		return featureItem.stack(amount);
	}

	public ItemStack stack(R rowType, C columnType, StackOption... options) {
		FeatureItem<I> featureItem = itemByTypes.get(rowType, columnType);
		if (featureItem == null) {
			throw new IllegalStateException("This feature group has no item registered for the given sub type to create a stack for.");
		}
		return featureItem.stack(options);
	}
}
