package forestry.modules.features;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.core.proxy.Proxies;

public interface IBlockFeature<B extends Block, I extends BlockItem> extends IItemFeature<B, I>, IBlockProvider<B, I> {

	default Block block() {
		B block = getBlock();
		if (block == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return block;
	}

	@SuppressWarnings("unchecked")
	default <T extends Block> T cast() {
		return (T) block();
	}

	default B apply(B block) {
		return block;
	}

	void setBlock(B block);

	@Nullable
	IFeatureConstructor<I> getItemConstructor();

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
}
