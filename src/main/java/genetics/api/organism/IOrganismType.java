package genetics.api.organism;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRootBuilder;

/**
 * Genetic types, implemented by enums and compared with ==
 * <p>
 * You have to register your {@link IOrganismType} together with a {@link IOrganismHandler} at the
 * {@link IIndividualRootBuilder} that handles the {@link IIndividual} to that this type belongs.
 */
public interface IOrganismType {
	String getName();

	default boolean isEmpty() {
		return false;
	}
}
