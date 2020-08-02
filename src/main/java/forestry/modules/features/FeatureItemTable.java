package forestry.modules.features;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import net.minecraft.item.Item;

import forestry.api.core.IItemSubtype;

public class FeatureItemTable<I extends Item, R extends IItemSubtype, C extends IItemSubtype> extends FeatureTable<FeatureItemTable.Builder<I, R, C>, FeatureItem<I>, R, C> {

    public FeatureItemTable(Builder<I, R, C> builder) {
        super(builder);
    }

    @Override
    protected FeatureItem<I> createFeature(Builder<I, R, C> builder, R rowType, C columnType) {
        return builder.registry.item(() -> builder.constructor.apply(rowType, columnType), builder.getIdentifier(rowType, columnType));
    }

    public Collection<I> getBlocks() {
        return featureByTypes.values().stream().map(IItemFeature::item).collect(Collectors.toList());
    }

    public static class Builder<I extends Item, R extends IItemSubtype, C extends IItemSubtype> extends FeatureTable.Builder<R, C, FeatureItemTable<I, R, C>> {
        private final IFeatureRegistry registry;
        private final BiFunction<R, C, I> constructor;

        public Builder(IFeatureRegistry registry, BiFunction<R, C, I> constructor) {
            super(registry);
            this.registry = registry;
            this.constructor = constructor;
        }

        public FeatureItemTable<I, R, C> create() {
            return new FeatureItemTable<>(this);
        }
    }
}
