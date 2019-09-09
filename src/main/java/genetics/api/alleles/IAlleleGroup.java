package genetics.api.alleles;

import java.util.Collection;

import genetics.api.individual.IChromosomeType;

public interface IAlleleGroup<V> {

	Collection<IAllele> getAlleles();

	Collection<IChromosomeType> getTypes();

	String getName();

	Collection<V> getValues();
}
