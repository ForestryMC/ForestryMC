package forestry.api.genetics;

import net.minecraft.item.ItemStack;

public interface IGeneticFactory {

	/**
	 * Creates a default instance of a {@link IIndividualHandler}
	 *
	 * @param itemStack The item that contains the genetic information.
	 * @param speciesType The species type of the individual.
	 * @param speciesRoot The root that describes the individual.
	 *
	 * @return A instance of {@link IIndividualHandler}.
	 */
	IIndividualHandler createIndividualHandler(ItemStack itemStack, ISpeciesType speciesType, ISpeciesRoot speciesRoot);
}
