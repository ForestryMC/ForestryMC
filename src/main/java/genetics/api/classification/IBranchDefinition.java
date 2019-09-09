package genetics.api.classification;

import genetics.api.individual.ITemplateProvider;

/**
 * Helper interface that can be implemented for internal usage in you mod.
 * <p>
 * This interface has no use in the genetics mod.
 */
public interface IBranchDefinition extends ITemplateProvider {
	IClassification getBranch();
}
