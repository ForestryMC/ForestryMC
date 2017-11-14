package forestry.api.genetics;

import net.minecraft.item.ItemStack;

public interface IGeneticFactory {

	/**
	 * Creates a default instance of a {@link IIndividualHandler}
	 *
	 * @param itemStack   The item that contains the genetic information.
	 * @param speciesType The species type of the individual.
	 * @param speciesRoot The root that describes the individual.
	 *
	 * @return A instance of {@link IIndividualHandler}.
	 */
	IIndividualHandler createIndividualHandler(ItemStack itemStack, ISpeciesType speciesType, ISpeciesRoot speciesRoot);

	/**
	 * Creates an instance of a {@link IChromosome} with the same active and inactive allele.
	 *
	 * @return A instance of {@link IChromosome}.
	 */
	IChromosome createChromosome(IAllele allele);

	/**
	 * Creates an instance of a {@link IChromosome}.
	 *
	 * The order of the alleles only matters if both alleles are recessive.
	 *
	 * @param firstAllele  The first allele.
	 * @param secondAllele The second allele.
	 *
	 * @return A instance of {@link IChromosome}.
	 */
	IChromosome createChromosome(IAllele firstAllele, IAllele secondAllele);
}
