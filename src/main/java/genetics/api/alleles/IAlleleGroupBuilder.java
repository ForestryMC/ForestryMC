package genetics.api.alleles;

import genetics.api.individual.IChromosomeType;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public interface IAlleleGroupBuilder<V> {

    IAlleleGroupBuilder<V> addValues(V... values);

    IAlleleGroupBuilder<V> addValues(Collection<V> values);

    IAlleleGroupBuilder<V> addChromosome(IChromosomeType type);

    IAlleleGroupBuilder<V> addChromosome(IChromosomeType type, Predicate<V> validator);

    IAlleleGroupBuilder<V> addChromosome(IChromosomeType type, V defaultValue, Predicate<V> validator, Function<V, String> nameSupplier);
}
