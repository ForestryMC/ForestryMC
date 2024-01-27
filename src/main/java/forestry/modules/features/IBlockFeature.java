package forestry.modules.features;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import forestry.core.proxy.Proxies;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.properties.Property;


import forestry.api.core.IBlockProvider;
import net.minecraftforge.registries.RegisterEvent;

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
        B block = getBlockConstructor().get();
		setBlock(block);
		Function<B, I> constructor = getItemBlockConstructor();
		if (constructor != null) {
            setItem(constructor.apply(block));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	default void register(RegisterEvent event) {
        IItemFeature.super.register(event);

		if (hasBlock()) {
            event.register(Registry.BLOCK_REGISTRY, helper -> {
                helper.register(new ResourceLocation(getModId(), getIdentifier()), block());
                Proxies.common.registerBlock(block());
            });
		}
	}

	BlockState defaultState();

	<V extends Comparable<V>> BlockState with(Property<V> property, V value);
}
