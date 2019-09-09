package forestry.farming.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class FarmingContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerFarm> FARM;

	public FarmingContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		FARM = register(ContainerFarm::fromNetwork, "farm");
	}
}
