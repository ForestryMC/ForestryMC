package genetics.api.root;

import genetics.api.individual.IIndividual;

public interface IIndividualRootFactory<I extends IIndividual, R extends IIndividualRoot<I>> {
	/**
	 * Creates a new root.
	 * <p>
	 * Used by {@link IIndividualRootBuilder} to create the root object.
	 */
	R createRoot(IRootContext<I> context);
}
