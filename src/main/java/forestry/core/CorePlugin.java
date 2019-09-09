package forestry.core;

import net.minecraftforge.eventbus.api.EventPriority;

import genetics.api.GeneticPlugin;
import genetics.api.IGeneticPlugin;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.classification.IClassificationRegistry;

import forestry.api.genetics.AlleleManager;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.GeneticRegistry;

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
}
