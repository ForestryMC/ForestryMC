package genetics.api.individual;

import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;

/**
 * ITemplateProvider is a helper interface that can be implemented if a class contains immutable genetic information
 * about a purebred {@link IIndividual}.
 */
public interface ITemplateProvider {
	/**
	 * @return The active and inactive template of the individual.
	 */
	IAlleleTemplate getTemplate();

	default IAlleleTemplateBuilder getTemplateBuilder() {
		return getTemplate().createBuilder();
	}
}
