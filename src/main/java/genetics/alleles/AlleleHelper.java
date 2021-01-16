package genetics.alleles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleData;
import genetics.api.alleles.IAlleleHandler;
import genetics.api.alleles.IAlleleHelper;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeType;

public enum AlleleHelper implements IAlleleHelper, IAlleleHandler {
	INSTANCE;

	private final Map<IChromosomeType, Map<Object, IAllele>> allelesByChromosome = new HashMap<>();
	private final Map<IAlleleData, IAlleleValue> alleleByData = new HashMap<>();

	@Override
	public void onRegisterAllele(IAllele allele) {
	}

	@Override
	public void onAddTypes(IAllele allele, IChromosomeType... types) {
		if (!(allele instanceof IAlleleValue)) {
			return;
		}
		IAlleleValue alleleValue = (IAlleleValue) allele;
		for (IChromosomeType type : types) {
			allelesByChromosome.computeIfAbsent(type, t -> new HashMap<>()).put(alleleValue.getValue(), allele);
		}
	}

	@Override
	public <V> void onRegisterData(IAlleleValue<V> allele, IAlleleData<V> alleleData) {
		alleleByData.put(alleleData, allele);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Optional<IAlleleValue<V>> getAllele(IChromosomeType chromosomeType, V value) {
		Map<Object, IAllele> alleleByValue = allelesByChromosome.get(chromosomeType);
		if (alleleByValue == null) {
			return Optional.empty();
		}
		return Optional.ofNullable((IAlleleValue<V>) alleleByValue.get(value));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Optional<IAlleleValue<V>> getAllele(IAlleleData<V> alleleData) {
		IAlleleValue allele = alleleByData.get(alleleData);
		if (allele == null) {
			return Optional.empty();
		}
		return Optional.of((IAlleleValue<V>) allele);
	}
}
