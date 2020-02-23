package forestry.core;

import net.minecraftforge.eventbus.api.EventPriority;

import genetics.api.GeneticPlugin;
import genetics.api.IGeneticPlugin;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.root.components.IRootComponentRegistry;

import forestry.api.genetics.ForestryComponentKeys;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.GeneticRegistry;
import forestry.core.genetics.root.ResearchHandler;

@GeneticPlugin(modId = Constants.MOD_ID, priority = EventPriority.HIGH)
public class CorePlugin implements IGeneticPlugin {
	@Override
	public void registerClassifications(IClassificationRegistry registry) {
		GeneticRegistry alleleRegistry = new GeneticRegistry();
		AlleleManager.geneticRegistry = alleleRegistry;
		alleleRegistry.registerClassifications(registry);
	}

	@Override
	public void registerAlleles(IAlleleRegistry registry) {
		((GeneticRegistry) AlleleManager.geneticRegistry).registerAlleles(registry);
	}

	@Override
	public void registerComponents(IRootComponentRegistry componentRegistry) {
		componentRegistry.registerFactory(ForestryComponentKeys.RESEARCH, ResearchHandler::new);
	}
}
