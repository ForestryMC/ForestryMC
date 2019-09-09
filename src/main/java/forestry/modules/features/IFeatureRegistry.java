package forestry.modules.features;

import java.util.function.Function;

import net.minecraft.item.Item;

public interface IFeatureRegistry {

	static IFeatureRegistry get(String s) {
		return null;
	}

	<I extends Item> FeatureItem<I> item(IFeatureConstructor<I> constructor, String identifier);

	<I extends Item, S extends IItemSubtype> FeatureItemGroup<I, S> itemGroup(Function<S, IFeatureConstructor<I>> constructor, String identifier, S[] subTypes);
}
