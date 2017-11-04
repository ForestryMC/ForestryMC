package forestry.api.genetics;

import javax.annotation.Nullable;

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
	IAlleleTemplate copy();

	ISpeciesRoot getRoot();

	IIndividual toIndividual(@Nullable IAlleleTemplate inactiveTemplate);

	IGenome toGenome(@Nullable IAlleleTemplate inactiveTemplate);

	IChromosome[] toChromosomes(@Nullable IAlleleTemplate inactiveTemplate);
}
