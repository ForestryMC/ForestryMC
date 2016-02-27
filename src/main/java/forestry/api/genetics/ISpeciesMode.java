package forestry.api.genetics;

import javax.annotation.Nonnull;

public interface ISpeciesMode<C extends IChromosomeType> {

	/**
	 * @return Localized name of this mode.
	 */
	@Nonnull
	String getName();

	/**
	 * @return Float used to modify the base mutation chance.
	 */
	float getMutationModifier(IGenome<C> genome, IGenome<C> mate);
}
