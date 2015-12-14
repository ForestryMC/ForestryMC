package forestry.core.genetics.alleles;

public interface IAlleleValue<V> {
	boolean isDominant();

	V getValue();
}
