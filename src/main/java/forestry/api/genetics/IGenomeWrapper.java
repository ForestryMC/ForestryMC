package forestry.api.genetics;

import forestry.api.apiculture.IBeeGenomeWrapper;

/**
 * A wrapper around a genome with methods that allow to get quick access to the value of an allele or to the allele
 * itself.
 *
 * The goal of this interface is to make it easier for other mods to create there own {@link IIndividual} and
 * that they not have to use the internal {@link IGenome} class.
 *
 * A example for the implementation of this is {@link IBeeGenomeWrapper#getSpeed()}.
 * <p>
 * You can get an instance of a genome wrapper from the {@link ISpeciesRoot} of a species.
 *
 * @since Forestry 5.8
 */
public interface IGenomeWrapper<T extends Enum<T> & IChromosomeType> {

	/**
	 * The genome that is wrapped by this wrapper.
	 */
	IGenome getGenome();

	/**
	 * @return The active species of the genome.
	 */
	IAlleleSpecies getPrimary();

	/**
	 * @return The inactive species of the genome.
	 */
	IAlleleSpecies getSecondary();

	<A extends IAllele> A getActiveAllele(T chromosomeType, Class<A> alleleClass);

	<A extends IAllele> A getInactiveAllele(T chromosomeType, Class<A> alleleClass);
}
