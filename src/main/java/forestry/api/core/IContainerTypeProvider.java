package forestry.api.core;

import javax.annotation.Nullable;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface IContainerTypeProvider<C extends AbstractContainerMenu> {
	boolean hasContainerType();

	@Nullable
	MenuType<C> getContainerType();

	MenuType<C> containerType();
}
