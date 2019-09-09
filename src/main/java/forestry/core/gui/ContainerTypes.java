package forestry.core.gui;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.fml.network.IContainerFactory;

public class ContainerTypes {

	private IForgeRegistry<ContainerType<?>> registry;

	public ContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		this.registry = registry;
	}

	//TODO abstract this to work for all registry types?
	public <T extends Container> ContainerType<T> register(IContainerFactory<T> factory, String name) {
		ContainerType<T> type = IForgeContainerType.create(factory);
		type.setRegistryName(name);
		registry.register(type);
		return type;
	}
}
