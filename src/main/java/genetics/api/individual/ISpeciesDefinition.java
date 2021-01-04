package genetics.api.individual;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.root.IGeneticListener;
import genetics.api.root.IIndividualRootBuilder;

/**
 * Help interface that can be used to define genetic species.
 * It provides method to get the an instance of the default template, the default genome or default individual
 * of the defined species.
 * It also can be used to register mutations or other species related data to the {@link IRootComponentBuilder}s of the
 * {@link IIndividualRootBuilder} of the root that the species belongs to.
 */
public interface ISpeciesDefinition<I extends IIndividual> extends ITemplateProvider, IGeneticListener<I> {

	/**
	 * @return An instance of the genome that contains the default template of this species.
	 */
	IGenome getGenome();

	/**
	 * @return The species that is defined by this interface.
	 */
	IAlleleSpecies getSpecies();

	/**
	 * @return Creates a instance of the {@link IIndividual} that contains the {@link #getGenome()}.
	 */
	I createIndividual();
}
