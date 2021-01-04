package genetics.api;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleData;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.individual.IChromosomeType;
import genetics.api.root.IGeneticListenerRegistry;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootManager;
import genetics.api.root.components.IRootComponentRegistry;

/**
 * The main class to implement to create a Genetic plugin. Everything communicated between a mod and Genetics is through
 * this class. IGeneticPlugins must have the {@link GeneticPlugin} annotation to get loaded by Genetics.
 */
public interface IGeneticPlugin {
	/**
	 * Register classifications
	 */
	default void registerClassifications(IClassificationRegistry registry) {
		//Default Implementation
	}

	default void registerListeners(IGeneticListenerRegistry registry) {
		//Default Implementation
	}

	/**
	 * This method can be used to register alleles.
	 * <p>
	 * {@link IAlleleRegistry#registerAllele(IAllele, IChromosomeType...)} can be use to register your own implementation
	 * of {@link IAllele} or {@link genetics.api.alleles.Allele}.
	 * You also can use {@link IAlleleRegistry#registerAllele(String, String, Object, boolean, IChromosomeType...)} to
	 * register a allele that with one value or you use {@link IAlleleRegistry#registerAllele(IAlleleData, IChromosomeType...)}
	 * if you want to keep the data in an object like an enum.
	 */
	default void registerAlleles(IAlleleRegistry registry) {
		//Default Implementation
	}

	default void registerComponents(IRootComponentRegistry componentRegistry) {
		//Default Implementation
	}

	/**
	 * This method can be used to create {@link genetics.api.root.IIndividualRootBuilder}s. They will later automatically
	 * create the {@link IIndividualRoot}s. You can accesses the root object through the {@link genetics.api.root.IRootDefinition}
	 * of the root.
	 */
	default void createRoot(IRootManager rootManager, IGeneticFactory geneticFactory) {
		//Default Implementation
	}

	/**
	 * Called after {@link #createRoot(IRootManager, IGeneticFactory)} was called at all {@link IGeneticPlugin}s.
	 */
	default void initRoots(IRootManager manager) {
		//Default Implementation
	}

	/**
	 * Called after the previous methods were called and every thing is registered.
	 * Can be used to get created {@link IIndividualRoot}s.
	 */
	default void onFinishRegistration(IRootManager manager, IGeneticApiInstance instance) {
		//Default Implementation
	}
}
