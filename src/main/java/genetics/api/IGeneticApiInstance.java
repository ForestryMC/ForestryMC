package genetics.api;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleHelper;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.classification.IClassification;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.individual.IChromosomeList;
import genetics.api.individual.IKaryotype;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IIndividualRootHelper;
import genetics.api.root.IRootDefinition;
import genetics.api.root.components.IRootComponentRegistry;

import java.util.Map;
import java.util.Optional;

public interface IGeneticApiInstance {
	/**
	 * This instance of the classification registry can be used to get or register {@link IClassification}s.
	 *
	 * @throws IllegalStateException if the method gets called before the pre init phase of the genetics mod.
	 */
	IClassificationRegistry getClassificationRegistry();

	/**
	 * This instance of the allele registry can be used to get {@link IAllele}s.
	 * It's available after all alleles where registered at {@link IGeneticPlugin#registerAlleles(IAlleleRegistry)}.
	 *
	 * @throws IllegalStateException if the method gets called before {@link IGeneticPlugin#registerAlleles(IAlleleRegistry)}  was called at all plugins.
	 */
	IAlleleRegistry getAlleleRegistry();

	IAlleleHelper getAlleleHelper();

	/**
	 * This instance is available before any method of a {@link IGeneticPlugin} was called.
	 *
	 * @throws IllegalStateException if the method gets called before the pre-init phase of fml.
	 */
	IGeneticFactory getGeneticFactory();

	/**
	 * This instance is available before any method of a {@link IGeneticPlugin} was called.
	 *
	 * @throws IllegalStateException if the method gets called before the pre-init phase of fml.
	 */
	IGeneticSaveHandler getSaveHandler();

	/**
	 * This instance is available before any method of a {@link IGeneticPlugin} was called.
	 *
	 * @throws IllegalStateException if the method gets called before the pre-init phase of fml.
	 */
	IIndividualRootHelper getRootHelper();

	/**
	 * This instance is available before any method of a {@link IGeneticPlugin} was called.
	 *
	 * @throws IllegalStateException if the method gets called before the pre-init phase of fml.
	 */
	IRootComponentRegistry getComponentRegistry();

	/**
	 * Retrieve the {@link IRootDefinition} with the given uid and computes one if there currently is no definition
	 * for the given uid.
	 *
	 * @param rootUID The uid that the {@link IIndividualRoot} object of the {@link IRootDefinition} is associated with.
	 * @param <R>     The type of the {@link IIndividualRoot} object that the definition contains.
	 * @return The definition that is associated with given uid.
	 */
	<R extends IIndividualRoot> IRootDefinition<R> getRoot(String rootUID);

	IChromosomeList getChromosomeList(String rootUID);

	Optional<IKaryotype> getKaryotype(String rootUID);

	/**
	 * @return A map that contains every root definition that was created by calling {@link #getRoot(String)}.
	 */
	Map<String, IRootDefinition> getRoots();

	/**
	 * @return Checks if the genetics mod is present.
	 */
	boolean isModPresent();
}
