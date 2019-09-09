package genetics.api.individual;

import net.minecraft.util.ResourceLocation;

import genetics.api.alleles.IAllele;
import genetics.api.root.IIndividualRoot;

/**
 * Can be used to create a modified version of an {@link IIndividual}. At the crate of this builder all genetic
 * information will be copied and the the {@link IIndividual} that was used to crate this builder will not be changed.
 */
public interface IIndividualBuilder<I extends IIndividual> {

	/**
	 * @return The definition of the individual.
	 */
	IIndividualRoot<I> getRoot();

	/**
	 * Sets a allele at a position of the chromosome.
	 *
	 * @param allele The allele that should be set at the position.
	 * @param type   The position at the chromosome array.
	 * @param active True if you want to set the active allele, false otherwise.
	 */
	void setAllele(IChromosomeType type, IAllele allele, boolean active);

	/**
	 * Sets a allele at a position of the chromosome.
	 *
	 * @param registryName The registry name of the allele that should be set at the position.
	 * @param type         The position at the chromosome array.
	 * @param active       True if you want to set the active allele, false otherwise.
	 */
	void setAllele(IChromosomeType type, ResourceLocation registryName, boolean active);

	/**
	 * @return The {@link IIndividual} that was used to create this builder.
	 */
	I getCreationIndividual();

	/**
	 * @return Creates a individual.
	 */
	I build();
}
