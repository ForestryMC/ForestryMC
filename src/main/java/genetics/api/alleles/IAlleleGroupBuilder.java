package genetics.api.alleles;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import genetics.api.individual.IChromosomeType;

public interface IAlleleGroupBuilder<V> {

	IAlleleGroupBuilder<V> addValues(V... values);

	IAlleleGroupBuilder<V> addValues(Collection<V> values);

	IAlleleGroupBuilder<V> addChromosome(IChromosomeType type);

	IAlleleGroupBuilder<V> addChromosome(IChromosomeType type, Predicate<V> validator);

	IAlleleGroupBuilder<V> addChromosome(IChromosomeType type, V defaultValue, Predicate<V> validator, Function<V, String> nameSupplier);
}
