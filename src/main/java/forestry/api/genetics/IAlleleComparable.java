package forestry.api.genetics;

public interface IAlleleComparable<A extends IAlleleComparable<A>> extends IAllele, Comparable<A> {

	/**
	 * To campare the allele for the propertys
	 */
	@Override
	int compareTo(A o);
	
}
