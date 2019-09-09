package forestry.climatology.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class ClimatologyContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerHabitatFormer> HABITAT_FORMER;

	public ClimatologyContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		HABITAT_FORMER = register(ContainerHabitatFormer::fromNetwork, "habitat_former");
	}
}
