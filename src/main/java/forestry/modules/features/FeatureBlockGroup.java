package forestry.modules.features;

import forestry.api.core.IBlockSubtype;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FeatureBlockGroup<B extends Block, S extends IBlockSubtype> extends FeatureGroup<FeatureBlockGroup.Builder<B, S>, FeatureBlock<B, BlockItem>, S> {

    private FeatureBlockGroup(Builder<B, S> builder) {
        super(builder);
    }

    @Override
    protected FeatureBlock<B, BlockItem> createFeature(Builder<B, S> builder, S type) {
        return builder.registry.block(() -> builder.constructor.apply(type), builder.itemConstructor != null ? (block) -> builder.itemConstructor.apply(block, type) : null, builder.getIdentifier(type));
    }

    public Collection<B> getBlocks() {
        return featureByType.values().stream().map(IBlockFeature::block).collect(Collectors.toList());
    }

    public Collection<BlockItem> getItems() {
        return featureByType.values().stream().filter(IBlockFeature::hasItem).map(IBlockFeature::item).collect(Collectors.toList());
    }

    @Nullable
    public BlockState findState(String typeName) {
        Optional<FeatureBlock> block = featureByType.entrySet().stream()
                .filter(e -> e.getKey().getString().equals(typeName))
                .findFirst()
                .flatMap(e -> Optional.of(e.getValue()));
        return block.map(FeatureBlock::defaultState).orElse(null);
    }

    public boolean blockEqual(BlockState state) {
        return getFeatures().stream().anyMatch(f -> f.blockEqual(state));
    }

    public boolean blockEqual(Block block) {
        return getFeatures().stream().anyMatch(f -> f.blockEqual(block));
    }

    public Block[] blockArray() {
        return getBlocks().toArray(new Block[0]);
    }

    public static class Builder<B extends Block, S extends IBlockSubtype> extends FeatureGroup.Builder<S, FeatureBlockGroup<B, S>> {
        private final IFeatureRegistry registry;
        private final Function<S, B> constructor;
        @Nullable
        private BiFunction<B, S, BlockItem> itemConstructor;

        public Builder(IFeatureRegistry registry, Function<S, B> constructor) {
            super(registry);
            this.registry = registry;
            this.constructor = constructor;
        }

        public Builder<B, S> itemWithType(BiFunction<B, S, BlockItem> itemConstructor) {
            this.itemConstructor = itemConstructor;
            return this;
        }

        public Builder<B, S> item(Function<B, BlockItem> itemConstructor) {
            this.itemConstructor = (block, type) -> itemConstructor.apply(block);
            return this;
        }

        public FeatureBlockGroup<B, S> create() {
            return new FeatureBlockGroup<>(this);
        }
    }
}
