package forestry.modules.features;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.minecraftforge.fml.network.IContainerFactory;

import forestry.api.core.IContainerTypeProvider;

public interface IContainerTypeFeature<C extends AbstractContainerMenu> extends IContainerTypeProvider<C>, IModFeature {

	@Override
	default void create() {
		MenuType<C> containerType = IForgeContainerType.create(getContainerFactory());
		containerType.setRegistryName(getModId(), getIdentifier());
		setContainerType(containerType);
	}

	@Override
	@SuppressWarnings("unchecked")
	default <R extends IForgeRegistryEntry<R>> void register(RegistryEvent.Register<R> event) {
		IForgeRegistry<R> registry = event.getRegistry();
		Class<R> superType = registry.getRegistrySuperType();
		if (MenuType.class.isAssignableFrom(superType) && hasContainerType()) {
			registry.register((R) containerType());
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
