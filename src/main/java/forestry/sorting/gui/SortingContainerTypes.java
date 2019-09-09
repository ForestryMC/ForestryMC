package forestry.sorting.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class SortingContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerGeneticFilter> GENETIC_FILTER;

	public SortingContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		GENETIC_FILTER = register(ContainerGeneticFilter::fromNetwork, "genetic_filter");
	}
}
