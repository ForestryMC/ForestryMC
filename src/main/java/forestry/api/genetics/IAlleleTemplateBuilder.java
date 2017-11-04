package forestry.api.genetics;

public interface IAlleleTemplateBuilder<T extends IChromosomeType, S extends IAlleleSpecies> {

	/**
	 * Sets the species allele at the position of the species chromosome.
	 */
	IAlleleTemplateBuilder<T, S> setSpecies(S species);

	/**
	 * Sets the allele at the position of the chromosome.
	 */
	IAlleleTemplateBuilder<T, S> set(T chromosomeType, IAllele allele);

	IAlleleTemplateBuilder<T, S> set( T chromosomeType, boolean value);

	IAlleleTemplateBuilder<T, S> set(T chromosomeType, int value);

	IAlleleTemplateBuilder<T, S> set(T chromosomeType, float value);

	ISpeciesRoot getRoot();

	int size();

	IAlleleTemplate<T, S> build();
}
