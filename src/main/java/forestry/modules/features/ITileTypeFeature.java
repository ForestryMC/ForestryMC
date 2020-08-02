package forestry.modules.features;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import forestry.api.core.ITileTypeProvider;

public interface ITileTypeFeature<T extends TileEntity> extends IModFeature, ITileTypeProvider<T> {

    @Override
    default void create() {
        TileEntityType<T> tileEntityType = getTileTypeConstructor().build(null);
        tileEntityType.setRegistryName(getModId(), getIdentifier());
        setTileType(tileEntityType);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <R extends IForgeRegistryEntry<R>> void register(RegistryEvent.Register<R> event) {
        IForgeRegistry<R> registry = event.getRegistry();
        Class<R> superType = registry.getRegistrySuperType();
        if (TileEntityType.class.isAssignableFrom(superType) && hasTileType()) {
            registry.register((R) tileType());
        }
    }

    @Override
    default TileEntityType<T> tileType() {
        TileEntityType<T> tileType = getTileType();
        if (tileType == null) {
            throw new IllegalStateException("Called feature getter method before content creation.");
        }
        return tileType;
    }

    void setTileType(TileEntityType<T> tileType);

    TileEntityType.Builder<T> getTileTypeConstructor();
}
