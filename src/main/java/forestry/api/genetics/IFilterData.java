package forestry.api.genetics;

public interface IFilterData {

	/**
	 * If the root is present, returns the root,
	 * otherwise throws {@code NoSuchElementException}.
	 */
	ISpeciesRoot getRoot();

	/**
	 * If the individual is present, returns the individual,
	 * otherwise throws {@code NoSuchElementException}.
	 */
	IIndividual getIndividual();

	/**
	 * If the type is present, returns the type,
	 * otherwise throws {@code NoSuchElementException}.
	 */
	ISpeciesType getType();

	/**
	 * @return True if this data contains a root, individual and type.
	 */
	boolean isPresent();
}
