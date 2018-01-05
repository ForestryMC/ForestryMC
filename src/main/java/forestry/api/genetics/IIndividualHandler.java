package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

/**
 * Implement this interface as a capability which should provide the genetic information of an item.
 * <p/>
 * You can use {@link IGeneticFactory#createIndividualHandler(ItemStack, ISpeciesType, ISpeciesRoot)} to create an
 * instance of this or you can use your own implementation.
 */
public interface IIndividualHandler<I extends IIndividual> {

	/**
	 * @return Creates the individual out of the nbt of the item.
	 */
	@Nullable
	I getIndividual();

	/**
	 * @return The root of the individual.
	 */
	ISpeciesRoot getRoot();

	/**
	 * @return The species type of the individual.
	 */
	ISpeciesType getType();

	/**
	 * Quickly gets the allele without loading the whole genome.
	 *
	 * @param type The chromosome type of the chromosome that contains the allele.
	 * @param active True if the allele should be the active allele of the chromosome, false if not.
	 *
	 * @return The allele that is at that position of the genome.
	 */
	IAllele getAlleleDirectly(IChromosomeType type, boolean active);
}
