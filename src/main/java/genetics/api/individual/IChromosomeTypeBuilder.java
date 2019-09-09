package genetics.api.individual;

import genetics.api.alleles.IAllele;

public interface IChromosomeTypeBuilder {

	IChromosomeTypeBuilder name(String name);

	<V> IChromosomeValue<V> asValue(Class<? extends V> valueClass);

	<A extends IAllele> IChromosomeAllele<A> asAllele(Class<? extends A> alleleClass);
}
