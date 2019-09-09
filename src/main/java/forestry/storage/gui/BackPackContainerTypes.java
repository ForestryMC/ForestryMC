package forestry.storage.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class BackPackContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerBackpack> BACKPACK;
	public final ContainerType<ContainerNaturalistBackpack> NATURALIST_BACKPACK;

	public BackPackContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		BACKPACK = register(ContainerBackpack::fromNetwork, "backpack");
		NATURALIST_BACKPACK = register(ContainerNaturalistBackpack::fromNetwork, "naturalist_backpack");
	}
}
