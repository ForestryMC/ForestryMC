package forestry.api.genetics;

public interface IAlleleProperty<A extends IAlleleProperty<A>> extends IAllele, Comparable<A> {

	/**
	 * To compare the allele for the properties
	 */
	@Override
	int compareTo(A o);

}
