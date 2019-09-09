package forestry.apiculture.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class ApicultureContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerAlveary> ALVEARY;
	public final ContainerType<ContainerAlvearyHygroregulator> ALVEARY_HYGROREGULATOR;
	public final ContainerType<ContainerAlvearySieve> ALVEARY_SIEVE;
	public final ContainerType<ContainerAlvearySwarmer> ALVEARY_SWARMER;
	public final ContainerType<ContainerBeeHousing> BEE_HOUSING;
	public final ContainerType<ContainerHabitatLocator> HABITAT_LOCATOR;
	public final ContainerType<ContainerImprinter> IMPRINTER;
	public final ContainerType<ContainerMinecartBeehouse> BEEHOUSE_MINECART;

	public ApicultureContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		ALVEARY = register(ContainerAlveary::fromNetwork, "alveary");
		ALVEARY_HYGROREGULATOR = register(ContainerAlvearyHygroregulator::fromNetwork, "alveary_hygroregulator");
		ALVEARY_SIEVE = register(ContainerAlvearySieve::fromNetwork, "alveary_sieve");
		ALVEARY_SWARMER = register(ContainerAlvearySwarmer::fromNetwork, "alveary_swarmer");
		BEE_HOUSING = register(ContainerBeeHousing::fromNetwork, "bee_housing");
		HABITAT_LOCATOR = register(ContainerHabitatLocator::fromNetwork, "habitat_locator");
		IMPRINTER = register(ContainerImprinter::fromNetwork, "imprinter");
		BEEHOUSE_MINECART = register(ContainerMinecartBeehouse::fromNetwork, "minecart_beehouse");

	}
}
