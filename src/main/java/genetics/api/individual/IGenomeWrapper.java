package genetics.api.individual;

import genetics.api.root.IIndividualRoot;

/**
 * A wrapper around a genome with methods that allow to get quick access to the value of an allele or to the allele
 * itself.
 * <p>
 * The goal of this interface is to make it easier for other mods to create there own {@link IIndividual} and
 * that they not have to use the internal implementation of the {@link IGenome} interface.
 * <p>
 * You can get an instance of a genome wrapper from the {@link IIndividualRoot} of a species with
 * {@link IIndividualRoot#createWrapper(IGenome)}.
 */
public interface IGenomeWrapper {
	/**
	 * @return The wrapped genome.
	 */
	IGenome getGenome();

}
