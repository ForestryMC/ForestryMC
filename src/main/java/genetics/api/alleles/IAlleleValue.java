package genetics.api.alleles;

import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;

/**
 * A interface that allows the allele to provide a value that can be accessed through the genome with
 * {@link IGenome#getActiveValue(IChromosomeType, Class)} or {@link IGenome#getInactiveValue(IChromosomeType, Class)}.
 *
 * @param <V> the type of value that this allele contains.
 */
public interface IAlleleValue<V> extends IAllele {
	/**
	 * @return the value that this allele contains.
	 */
	V getValue();
}
