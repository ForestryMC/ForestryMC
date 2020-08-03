package forestry.modules.features;

import forestry.api.core.IBlockSubtype;
import forestry.core.utils.datastructures.TriFunction;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FeatureBlockTable<B extends Block, R extends IBlockSubtype, C extends IBlockSubtype> extends FeatureTable<FeatureBlockTable.Builder<B, R, C>, FeatureBlock<B, BlockItem>, R, C> {

    public FeatureBlockTable(Builder<B, R, C> builder) {
        super(builder);
    }

    @Override
    protected FeatureBlock<B, BlockItem> createFeature(Builder<B, R, C> builder, R rowType, C columnType) {
        return builder.registry.block(() -> builder.constructor.apply(rowType, columnType), (block) -> builder.itemConstructor != null ? builder.itemConstructor.apply(block, rowType, columnType) : null, builder.getIdentifier(rowType, columnType));
    }

    public Collection<B> getBlocks() {
        return featureByTypes.values().stream().map(IBlockFeature::block).collect(Collectors.toList());
    }

    public Collection<BlockItem> getItems() {
        return featureByTypes.values().stream().filter(IBlockFeature::hasItem).map(IBlockFeature::item).collect(Collectors.toList());
    }

    public Collection<B> getRowBlocks(R rowType) {
        return getRowFeatures(rowType).stream().map(IBlockFeature::block).collect(Collectors.toList());
    }

    public Collection<B> getColumnBlocks(C columnType) {
        return getColumnFeatures(columnType).stream().map(IBlockFeature::block).collect(Collectors.toList());
    }

    public static class Builder<B extends Block, R extends IBlockSubtype, C extends IBlockSubtype> extends FeatureTable.Builder<R, C, FeatureBlockTable<B, R, C>> {
        private final IFeatureRegistry registry;
        private final BiFunction<R, C, B> constructor;
        @Nullable
        private TriFunction<B, R, C, BlockItem> itemConstructor;

        public Builder(IFeatureRegistry registry, BiFunction<R, C, B> constructor) {
            super(registry);
            this.registry = registry;
            this.constructor = constructor;
        }

        public Builder<B, R, C> itemWithType(TriFunction<B, R, C, BlockItem> itemConstructor) {
            this.itemConstructor = itemConstructor;
            return this;
        }

        public Builder<B, R, C> item(Function<B, BlockItem> itemConstructor) {
            this.itemConstructor = (block, rowType, columnType) -> itemConstructor.apply(block);
            return this;
        }

        public FeatureBlockTable<B, R, C> create() {
            return new FeatureBlockTable<>(this);
        }
    }
}
