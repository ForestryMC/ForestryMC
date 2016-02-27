package forestry.api.genetics;

public interface IAlleleProperty<A extends IAlleleProperty<A>> extends IAllele, Comparable<A> {

	/**
	 * To campare the allele for the propertys
	 */
	@Override
	int compareTo(A o);
	
}
