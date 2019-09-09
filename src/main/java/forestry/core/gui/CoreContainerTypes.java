package forestry.core.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.circuits.ContainerSolderingIron;

public class CoreContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerAlyzer> ALYZER;
	public final ContainerType<ContainerAnalyzer> ANALYZER;
	public final ContainerType<ContainerEscritoire> ESCRITOIRE;
	public final ContainerType<ContainerNaturalistInventory> NATURALIST_INVENTORY;
	public final ContainerType<ContainerSolderingIron> SOLDERING_IRON;

	public CoreContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);
		ALYZER = register(ContainerAlyzer::fromNetwork, "alyzer");
		ANALYZER = register(ContainerAnalyzer::fromNetwork, "analyzer");
		ESCRITOIRE = register(ContainerEscritoire::fromNetwork, "escritoire");
		NATURALIST_INVENTORY = register(ContainerNaturalistInventory::fromNetwork, "naturalist_inventory");
		SOLDERING_IRON = register(ContainerSolderingIron::fromNetwork, "soldering_iron");
	}
}
