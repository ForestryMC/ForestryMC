package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.state.Property;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.api.core.IBlockProvider;
import forestry.core.proxy.Proxies;

public interface IBlockFeature<B extends Block, I extends BlockItem> extends IItemFeature<I>, IBlockProvider<B, I> {

    @Override
    default B block() {
        B block = getBlock();
        if (block == null) {
            throw new IllegalStateException("Called feature getter method before content creation.");
        }
        return block;
    }

    @Override
    default Collection<B> collect() {
        return Collections.singleton(block());
    }

    @SuppressWarnings("unchecked")
    default <T extends Block> T cast() {
        return (T) block();
    }

    void setBlock(B block);

    Supplier<B> getBlockConstructor();

    @Nullable
    default Supplier<I> getItemConstructor() {
        if (!hasBlock()) {
            return null;
        }
        Function<B, I> itemBlockConstructor = getItemBlockConstructor();
        if (itemBlockConstructor == null) {
            return null;
        }
        return () -> itemBlockConstructor.apply(block());
    }

    @Nullable
    Function<B, I> getItemBlockConstructor();

    @Override
    default void create() {
        Supplier<B> blockConstructor = getBlockConstructor();
        B block = blockConstructor.get();
        block.setRegistryName(getModId(), getIdentifier());
        setBlock(block);
        Function<B, I> constructor = getItemBlockConstructor();
        if (constructor != null) {
            I item = constructor.apply(block);
            if (item.getRegistryName() == null && block.getRegistryName() != null) {
                item.setRegistryName(block.getRegistryName());
            }
            setItem(item);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T extends IForgeRegistryEntry<T>> void register(RegistryEvent.Register<T> event) {
        IItemFeature.super.register(event);
        IForgeRegistry<T> registry = event.getRegistry();
        Class<T> superType = registry.getRegistrySuperType();
        if (Block.class.isAssignableFrom(superType) && hasBlock()) {
            registry.register((T) block());
            Proxies.common.registerBlock(block());
        }
    }

    BlockState defaultState();

    <V extends Comparable<V>> BlockState with(Property<V> property, V value);
}
