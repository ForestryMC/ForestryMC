package forestry.factory.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class FactoryContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerBottler> BOTTLER;
	public final ContainerType<ContainerCarpenter> CARPENTER;
	public final ContainerType<ContainerCentrifuge> CENTRIFUGE;
	public final ContainerType<ContainerFabricator> FABRICATOR;
	public final ContainerType<ContainerFermenter> FERMENTER;
	public final ContainerType<ContainerMoistener> MOISTENER;
	public final ContainerType<ContainerRaintank> RAINTANK;
	public final ContainerType<ContainerSqueezer> SQUEEZER;
	public final ContainerType<ContainerStill> STILL;


	public FactoryContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		BOTTLER = register(ContainerBottler::fromNetwork, "bottler");
		CARPENTER = register(ContainerCarpenter::fromNetwork, "carpenter");
		CENTRIFUGE = register(ContainerCentrifuge::fromNetwork, "centrifuge");
		FABRICATOR = register(ContainerFabricator::fromNetwork, "fabricator");
		FERMENTER = register(ContainerFermenter::fromNetwork, "fermenter");
		MOISTENER = register(ContainerMoistener::fromNetwork, "moistener");
		RAINTANK = register(ContainerRaintank::fromNetwork, "raintank");
		SQUEEZER = register(ContainerSqueezer::fromNetwork, "squeezer");
		STILL = register(ContainerStill::fromNetwork, "still");

	}
}
