package genetics.api.alleles;

import genetics.api.individual.IChromosomeType;

import java.util.Optional;

public interface IAlleleHelper {
	<V> Optional<IAlleleValue<V>> getAllele(IChromosomeType chromosomeType, V value);

	<V> Optional<IAlleleValue<V>> getAllele(IAlleleData<V> alleleData);
}
