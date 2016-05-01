package forestry.api.genetics;

import javax.annotation.Nonnull;

public interface IAlleleProperty<A extends IAlleleProperty<A>> extends IAllele, Comparable<A> {

	/**
	 * To compare the allele for the properties
	 */
	@Override
	int compareTo(@Nonnull A o);
	
}
