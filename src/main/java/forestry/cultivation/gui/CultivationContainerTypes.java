package forestry.cultivation.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class CultivationContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerPlanter> PLANTER;

	public CultivationContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		PLANTER = register(ContainerPlanter::fromNetwork, "planter");
	}
}
