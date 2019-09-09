package forestry.api.genetics;

import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

public interface IFilterData {

	/**
	 * If the root is present, returns the root,
	 * otherwise throws {@code NoSuchElementException}.
	 */
	IIndividualRoot getRoot();

	IRootDefinition getDefinition();

	/**
	 * If the individual is present, returns the individual,
	 * otherwise throws {@code NoSuchElementException}.
	 */
	IIndividual getIndividual();

	/**
	 * If the type is present, returns the type,
	 * otherwise throws {@code NoSuchElementException}.
	 */
	IOrganismType getType();

	/**
	 * @return True if this data contains a root, individual and type.
	 */
	boolean isPresent();
}
