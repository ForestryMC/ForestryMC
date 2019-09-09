package genetics.api.alleles;

import net.minecraft.util.text.ITextComponent;

import genetics.api.classification.IClassification;
import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRoot;

/**
 * The species of an {@link genetics.api.individual.IIndividual} is one of the most important {@link IAllele} of the
 * whole {@link genetics.api.individual.IGenome}. In most cases it defines the model and name of the genetic item and
 * the products of the {@link genetics.api.individual.IIndividual} like for an example which type of honey combs a bee
 * produces or which type of fruit a tree bears in the case of the forestry bees and trees.
 * <p>
 * If the {@link genetics.api.individual.IIndividual} produces offsprings in the most cases the default templates of the
 * two species that  the {@link genetics.api.individual.IGenome} contains are providing the alleles for the template of
 * the offspring. This is the case in forestry as well as in as in binnies mods.
 */
public interface IAlleleSpecies extends IAllele {
	/**
	 * @return the {@link IIndividualRoot} associated with this species.
	 */
	IIndividualRoot<? extends IIndividual> getRoot();

	/**
	 * @return Localized short description of this species. (May be null.)
	 */
	ITextComponent getDescription();

	/**
	 * Binomial name of the species sans genus ("Apis"). Returning "humboldti" will have the bee species flavour name be "Apis humboldti". Feel free to use fun
	 * names or return null.
	 *
	 * @return flavour text (may be null)
	 */
	String getBinomial();

	/**
	 * Authority for the binomial name, e.g. "Sengir" on species of base Forestry.
	 *
	 * @return flavour text (may be null)
	 */
	String getAuthority();

	/**
	 * @return Branch this species is associated with.
	 */
	IClassification getBranch();
}
