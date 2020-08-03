package forestry.modules.features;

import forestry.api.core.IContainerTypeProvider;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IContainerTypeFeature<C extends Container> extends IContainerTypeProvider<C>, IModFeature {

    @Override
    default void create() {
        ContainerType<C> containerType = IForgeContainerType.create(getContainerFactory());
        containerType.setRegistryName(getModId(), getIdentifier());
        setContainerType(containerType);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <R extends IForgeRegistryEntry<R>> void register(RegistryEvent.Register<R> event) {
        IForgeRegistry<R> registry = event.getRegistry();
        Class<R> superType = registry.getRegistrySuperType();
        if (ContainerType.class.isAssignableFrom(superType) && hasContainerType()) {
            registry.register((R) containerType());
        }
    }

    @Override
    default ContainerType<C> containerType() {
        ContainerType<C> containerType = getContainerType();
        if (containerType == null) {
            throw new IllegalStateException("Called feature getter method before content creation.");
        }
        return containerType;
    }

    void setContainerType(ContainerType<C> containerType);

    IContainerFactory<C> getContainerFactory();
}
