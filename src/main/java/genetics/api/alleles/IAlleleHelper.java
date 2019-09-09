package genetics.api.alleles;

import java.util.Optional;

import genetics.api.individual.IChromosomeType;

public interface IAlleleHelper {
	<V> Optional<IAlleleValue<V>> getAllele(IChromosomeType chromosomeType, V value);

	<V> Optional<IAlleleValue<V>> getAllele(IAlleleData<V> alleleData);
}
