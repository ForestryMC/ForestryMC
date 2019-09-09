package forestry.energy.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class EnergyContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerEngineElectric> ENGINE_ELECTRIC;
	public final ContainerType<ContainerEngineBiogas> ENGINE_BIOGAS;
	public final ContainerType<ContainerEnginePeat> ENGINE_PEAT;
	public final ContainerType<ContainerGenerator> GENERATOR;


	public EnergyContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		ENGINE_ELECTRIC = register(ContainerEngineElectric::fromNetwork, "engine_electric");
		ENGINE_BIOGAS = register(ContainerEngineBiogas::fromNetwork, "engine_biogas");
		ENGINE_PEAT = register(ContainerEnginePeat::fromNetwork, "engine_peat");
		GENERATOR = register(ContainerGenerator::fromNetwork, "generator");
	}
}
