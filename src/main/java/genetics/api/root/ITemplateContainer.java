package genetics.api.root;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;

/**
 * The ITemplateContainer contains all templates of the {@link IIndividualRoot} that were added with the
 * {@link IIndividualRootBuilder}.
 */
public interface ITemplateContainer<I extends IIndividual> extends IRootComponent<I> {

	/**
	 * Registers a allele template using the UID of the first allele as identifier.
	 */
	ITemplateContainer registerTemplate(IAllele[] template);

	/**
	 * Registers a allele template using the UID of the first allele as identifier.
	 */
	ITemplateContainer registerTemplate(IAlleleTemplate template);

	/**
	 * Retrieves a registered template using the passed species unique identifier.
	 *
	 * @param identifier the {@link IAllele#getRegistryName()} of the active allele at the
	 *                   {@link IKaryotype#getSpeciesType()} of the {@link #getKaryotype()}.
	 * @return Array of {@link IAllele} representing a genome. The array is empty if no template was registered for the
	 * given identifier.
	 */
	IAllele[] getTemplate(String identifier);

	/**
	 * @param rand Random to use.
	 * @return A random template from the pool of registered templates.
	 */
	IAllele[] getRandomTemplate(Random rand);

	/**
	 * All templates with there associated identifier.
	 */
	Map<String, IAllele[]> getGenomeTemplates();

	Collection<IAllele[]> getTemplates();

	/**
	 * @return The karyotype that defines the size of the allele array and which alleles it can contain.
	 */
	default IKaryotype getKaryotype() {
		return getRoot().getKaryotype();
	}

	@Override
	ComponentKey<ITemplateContainer> getKey();
}
