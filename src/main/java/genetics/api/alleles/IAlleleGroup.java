package genetics.api.alleles;

import genetics.api.individual.IChromosomeType;

import java.util.Collection;

public interface IAlleleGroup<V> {

	Collection<IAllele> getAlleles();

	Collection<IChromosomeType> getTypes();

	String getName();

	Collection<V> getValues();
}
