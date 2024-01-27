package forestry.modules.features;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraftforge.network.IContainerFactory;

import forestry.api.core.IContainerTypeProvider;
import net.minecraftforge.registries.RegisterEvent;

public interface IContainerTypeFeature<C extends AbstractContainerMenu> extends IContainerTypeProvider<C>, IModFeature {

	@Override
	default void create() {
        setContainerType(IForgeMenuType.create(getContainerFactory()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default void register(RegisterEvent event) {
		if (hasContainerType()) {
            event.register(Registry.MENU_REGISTRY, new ResourceLocation(getModId(), getIdentifier()), this::containerType);
        }
	}

	@Override
	default MenuType<C> containerType() {
		MenuType<C> containerType = getContainerType();
		if (containerType == null) {
			throw new IllegalStateException("Called feature getter method before content creation.");
		}
		return containerType;
	}

	void setContainerType(MenuType<C> containerType);

	IContainerFactory<C> getContainerFactory();
}
