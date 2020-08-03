package forestry.api.core;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nullable;

public interface IContainerTypeProvider<C extends Container> {
    boolean hasContainerType();

    @Nullable
    ContainerType<C> getContainerType();

    ContainerType<C> containerType();
}
