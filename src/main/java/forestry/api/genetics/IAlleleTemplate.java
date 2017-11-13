package forestry.api.genetics;

import javax.annotation.Nullable;

/**
 * Can be used to create {@link IGenome}s, {@link IIndividual}s or {@link IChromosome}s or get a allele.
 */
public interface IAlleleTemplate<T extends IChromosomeType, S extends IAlleleSpecies> {

	/**
	 * @return The allele at the position of the chromosomeType at the allele array.
	 */
	@Nullable
	IAllele get(T chromosomeType);

	@Nullable
	S getSpecies();

	/**
	 * @return A copy of the allele array.
	 */
	IAllele[] alleles();

	/**
	 * @return The size of the allele array.
	 */
	int size();

	/**
	 * @return A copy of this template with a copied allele array.
	 */
	IAlleleTemplate<T, S> copy();

	ISpeciesRoot getRoot();

	/**
	 * Creates an individual with the help of the species root using
	 * {@link ISpeciesRoot#templateAsIndividual(IAllele[], IAllele[])}.
	 */
	IIndividual toIndividual(@Nullable IAlleleTemplate<T, S> inactiveTemplate);

	/**
	 * Creates a genome with the help of the species root using
	 * {@link ISpeciesRoot#templateAsGenome(IAllele[], IAllele[])}.
	 */
	IGenome toGenome(@Nullable IAlleleTemplate<T, S> inactiveTemplate);

	/**
	 * Creates a chromosome array with the help of the species root using
	 * {@link ISpeciesRoot#templateAsChromosomes(IAllele[], IAllele[])}.
	 */
	IChromosome[] toChromosomes(@Nullable IAlleleTemplate<T, S> inactiveTemplate);
}
