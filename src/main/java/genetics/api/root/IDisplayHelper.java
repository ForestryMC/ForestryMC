package genetics.api.root;

import net.minecraft.item.ItemStack;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

public interface IDisplayHelper<I extends IIndividual> {

	String getLocalizedShortName(IChromosomeType type);

	String getTranslationKeyShort(IChromosomeType type);

	String getLocalizedName(IChromosomeType type);

	String getTranslationKey(IChromosomeType type);

	/**
	 * Retrieves a stack that can and should only be used on the client side in a gui.
	 *
	 * @return A empty stack, if the species was not registered before the creation of this handler or if the species is
	 * not a species of the {@link IIndividualRoot}.
	 */
	ItemStack getDisplayStack(IAlleleSpecies species, IOrganismType type);

	ItemStack getDisplayStack(IAlleleSpecies species);
}
