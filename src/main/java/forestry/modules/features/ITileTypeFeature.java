package forestry.modules.features;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.api.core.ITileTypeProvider;

public interface ITileTypeFeature<T extends BlockEntity> extends IModFeature, ITileTypeProvider<T> {

	@Override
	default void create() {
		BlockEntityType<T> tileEntityType = getTileTypeConstructor().build(null);
		tileEntityType.setRegistryName(getModId(), getIdentifier());
		setTileType(tileEntityType);
	}

	@Override
	@SuppressWarnings("unchecked")
	default <R extends IForgeRegistryEntry<R>> void register(RegistryEvent.Register<R> event) {
		IForgeRegistry<R> registry = event.getRegistry();
		Class<R> superType = registry.getRegistrySuperType();
		if (BlockEntityType.class.isAssignableFrom(superType) && hasTileType()) {
			registry.register((R) tileType());
		}
	}

	@Override
	default BlockEntityType<T> tileType() {
		BlockEntityType<T> tileType = getTileType();
		if (tileType == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return tileType;
	}

	void setTileType(BlockEntityType<T> tileType);

	BlockEntityType.Builder<T> getTileTypeConstructor();
}
