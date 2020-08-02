package forestry.api.core;

import javax.annotation.Nullable;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public interface IContainerTypeProvider<C extends Container> {
    boolean hasContainerType();

    @Nullable
    ContainerType<C> getContainerType();

    ContainerType<C> containerType();
}
