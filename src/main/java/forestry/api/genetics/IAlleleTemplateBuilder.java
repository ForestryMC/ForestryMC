package forestry.api.genetics;

/**
 * Can be used to create allele templates.
 * </p>
 * You can get an instance of this from the species root with
 * {@link ISpeciesRoot#createTemplateBuilder()} or {@link ISpeciesRoot#createTemplateBuilder(IAllele[])}.
 */
public interface IAlleleTemplateBuilder<T extends IChromosomeType, S extends IAlleleSpecies> {

	/**
	 * Sets the species allele at the position of the species chromosome.
	 */
	IAlleleTemplateBuilder<T, S> setSpecies(S species);

	/**
	 * Sets a allele at a position of the chromosome.
	 *
	 * @param allele The allele that should be set at the position.
	 * @param chromosomeType The position at the chromosome array.
	 */
	IAlleleTemplateBuilder<T, S> set(T chromosomeType, IAllele allele);

	/**
	 * Sets a allele, that represents the given value, at a position of the chromosome.
	 *
	 * @param value The value that the allele should be represent.
	 * @param chromosomeType The position at the chromosome array.
	 */
	IAlleleTemplateBuilder<T, S> set( T chromosomeType, boolean value);

	/**
	 * Sets a allele, that represents the given value, at a position of the chromosome.
	 *
	 * @param value The value that the allele should be represent.
	 * @param chromosomeType The position at the chromosome array.
	 */
	IAlleleTemplateBuilder<T, S> set(T chromosomeType, int value);

	/**
	 * Sets a allele, that represents the given value, at a position of the chromosome.
	 *
	 * @param value The value that the allele should be represent.
	 * @param chromosomeType The position at the chromosome array.
	 */
	IAlleleTemplateBuilder<T, S> set(T chromosomeType, float value);

	ISpeciesRoot getRoot();

	/**
	 * @return The count of chromosome types.
	 */
	int size();

	/**
	 * @return Builds a allele template out of the data of this builder.
	 */
	IAlleleTemplate<T, S> build();
}
