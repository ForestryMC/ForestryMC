package forestry.core;

import net.minecraftforge.eventbus.api.EventPriority;

import forestry.api.genetics.alleles.AlleleManager;
import forestry.apiculture.DisplayHelper;
import forestry.core.config.Constants;
import forestry.core.genetics.DefaultDisplayHandler;
import forestry.core.genetics.alleles.GeneticRegistry;

import genetics.api.GeneticPlugin;
import genetics.api.IGeneticApiInstance;
import genetics.api.IGeneticPlugin;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.root.IRootManager;

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
	public void onFinishRegistration(IRootManager manager, IGeneticApiInstance instance) {
		DefaultDisplayHandler.init(DisplayHelper.getInstance());
	}
}
