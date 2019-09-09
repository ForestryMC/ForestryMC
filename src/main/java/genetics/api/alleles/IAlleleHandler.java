package genetics.api.alleles;

import genetics.api.individual.IChromosomeType;

/**
 * Handler for events that occur in IAlleleRegistry, such as registering alleles, etc. Useful for handling
 * plugin specific behavior (i.e. creating a list of all bee species etc.)
 */
public interface IAlleleHandler {
	/**
	 * Called after a allele was registered.
	 *
	 * @param allele The registered allele.
	 */
	default void onRegisterAllele(IAllele allele) {
	}

	/**
	 * Called after keys were added to a allele.
	 *
	 * @param allele The allele to that the keys were added.
	 * @param types  The added chromosome types.
	 */
	default void onAddTypes(IAllele allele, IChromosomeType... types) {
	}

	default <V> void onRegisterData(IAlleleValue<V> allele, IAlleleData<V> alleleData) {
	}
}
