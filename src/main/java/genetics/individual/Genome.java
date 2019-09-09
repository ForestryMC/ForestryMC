package genetics.individual;

import com.google.common.base.MoreObjects;

import java.util.Arrays;

import net.minecraft.nbt.CompoundNBT;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IChromosomeValue;
import genetics.api.individual.IGenome;
import genetics.api.individual.IGenomeWrapper;
import genetics.api.individual.IKaryotype;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

import genetics.ApiInstance;
import genetics.utils.AlleleUtils;

public final class Genome implements IGenome {
	private final IChromosome[] chromosomes;
	private final IKaryotype karyotype;

	public Genome(IKaryotype karyotype, CompoundNBT compound) {
		this.karyotype = karyotype;
		this.chromosomes = GeneticSaveHandler.INSTANCE.readTag(karyotype, compound);
	}

	public Genome(IKaryotype karyotype, IChromosome[] chromosomes) {
		this.karyotype = karyotype;
		checkChromosomes(chromosomes);
		this.chromosomes = chromosomes;
	}

	@SuppressWarnings("all")
	private void checkChromosomes(IChromosome[] chromosomes) {
		if (chromosomes.length != karyotype.getChromosomeTypes().length) {
			String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template.\n%s", karyotype.getUID(), chromosomesToString(chromosomes));
			throw new IllegalArgumentException(message);
		}

		IAlleleRegistry registry = ApiInstance.INSTANCE.getAlleleRegistry();
		IChromosomeType[] chromosomeTypes = karyotype.getChromosomeTypes();
		for (int i = 0; i < chromosomeTypes.length; i++) {
			IChromosomeType chromosomeType = chromosomeTypes[i];
			IChromosome chromosome = chromosomes[i];
			if (chromosome == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
					"Missing chromosome '%s'.\n%s", karyotype.getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			IAllele primary = chromosome.getActiveAllele();
			if (primary == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
					"Missing active allele for '%s'.\n%s", karyotype.getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			IAllele secondary = chromosome.getInactiveAllele();
			if (secondary == null) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
					"Missing inactive allele for '%s'.\n%s", karyotype.getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			if (!registry.isValidAllele(primary, chromosomeType)) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
					"Incorrect type for active allele '%s'.\n%s.", karyotype.getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}

			if (!registry.isValidAllele(secondary, chromosomeType)) {
				String message = String.format("Tried to create a genome for '%s' from an invalid chromosome template. " +
					"Incorrect type for inaktive allele '%s'.\n%s.", karyotype.getUID(), chromosomeType.getName(), chromosomesToString(chromosomes));
				throw new IllegalArgumentException(message);
			}
		}
	}

	private String chromosomesToString(IChromosome[] chromosomes) {
		StringBuilder stringBuilder = new StringBuilder();
		IChromosomeType[] chromosomeTypes = karyotype.getChromosomeTypes();
		for (int i = 0; i < chromosomes.length; i++) {
			IChromosomeType chromosomeType = chromosomeTypes[i];
			IChromosome chromosome = chromosomes[i];
			stringBuilder.append(chromosomeType.getName()).append(": ").append(chromosome).append("\n");
		}

		return stringBuilder.toString();
	}

	// / INFORMATION RETRIEVAL
	@Override
	public IChromosome[] getChromosomes() {
		return Arrays.copyOf(chromosomes, chromosomes.length);
	}

	@Override
	public <A extends IAlleleSpecies> A getPrimary(Class<? extends A> alleleClass) {
		return getActiveAllele(karyotype.getSpeciesType(), alleleClass);
	}

	@Override
	public <A extends IAlleleSpecies> A getSecondary(Class<? extends A> alleleClass) {
		return getInactiveAllele(karyotype.getSpeciesType(), alleleClass);
	}

	@Override
	public IAllele getActiveAllele(IChromosomeType chromosomeType) {
		IChromosome chromosome = getChromosome(chromosomeType);
		return chromosome.getActiveAllele();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> IAlleleValue<V> getActiveAllele(IChromosomeValue<V> chromosomeType) {
		return (IAlleleValue<V>) getActiveAllele(chromosomeType, IAlleleValue.class);
	}

	@Override
	public <A extends IAllele> A getActiveAllele(IChromosomeAllele<A> chromosomeType) {
		Class<? extends A> alleleClass = chromosomeType.getAlleleClass();
		IAllele allele = getActiveAllele((IChromosomeType) chromosomeType);
		if (!alleleClass.isInstance(allele)) {
			throw new IllegalArgumentException(String.format("The allele '%s' at the active position of the chromosome type '%s' is not an instance of the class '%s'.", allele, chromosomeType, alleleClass));
		}
		return alleleClass.cast(allele);
	}

	@Override
	public <A extends IAllele> A getActiveAllele(IChromosomeAllele<A> chromosomeType, A fallback) {
		Class<? extends A> alleleClass = chromosomeType.getAlleleClass();
		IAllele allele = getActiveAllele(chromosomeType);
		if (!alleleClass.isInstance(allele)) {
			return fallback;
		}
		return alleleClass.cast(allele);
	}

	@Override
	public <V> V getActiveValue(IChromosomeValue<V> chromosomeType) {
		IAllele allele = getActiveAllele(chromosomeType);
		V value = AlleleUtils.getAlleleValue(allele, chromosomeType.getValueClass());
		if (value == null) {
			throw new IllegalArgumentException(String.format("The allele '%s' at the active position of the chromosome type '%s' has no value.", allele, chromosomeType));
		}
		return value;
	}

	@Override
	public <V> V getActiveValue(IChromosomeValue<V> chromosomeType, V fallback) {
		IAllele allele = getActiveAllele(chromosomeType);
		return AlleleUtils.getAlleleValue(allele, chromosomeType.getValueClass(), fallback);
	}

	@Override
	public <A extends IAllele> A getActiveAllele(IChromosomeType chromosomeType, Class<? extends A> alleleClass) {
		IAllele allele = getActiveAllele(chromosomeType);
		if (!alleleClass.isInstance(allele)) {
			throw new IllegalArgumentException(String.format("The allele '%s' at the active position of the chromosome type '%s' is not an instance of the class '%s'.", allele, chromosomeType, alleleClass));
		}
		return alleleClass.cast(allele);
	}

	@Override
	public <A extends IAllele> A getActiveAllele(IChromosomeType chromosomeType, Class<? extends A> alleleClass, A fallback) {
		IAllele allele = getActiveAllele(chromosomeType);
		if (!alleleClass.isInstance(allele)) {
			return fallback;
		}
		return alleleClass.cast(allele);
	}

	@Override
	public IAllele getInactiveAllele(IChromosomeType chromosomeType) {
		IChromosome chromosome = getChromosome(chromosomeType);
		return chromosome.getInactiveAllele();
	}

	@Override
	public <A extends IAllele> A getInactiveAllele(IChromosomeType chromosomeType, Class<? extends A> alleleClass) {
		IAllele allele = getInactiveAllele(chromosomeType);
		if (!alleleClass.isInstance(allele)) {
			throw new IllegalArgumentException(String.format("The allele '%s' at the inactive position of the chromosome type '%s' is not an instance of the class '%s'.", allele, chromosomeType, alleleClass));
		}
		return alleleClass.cast(allele);
	}

	@Override
	public <A extends IAllele> A getInactiveAllele(IChromosomeType chromosomeType, Class<? extends A> alleleClass, A fallback) {
		IAllele allele = getInactiveAllele(chromosomeType);
		if (!alleleClass.isInstance(allele)) {
			return fallback;
		}
		return alleleClass.cast(allele);
	}

	@Override
	public <V> V getActiveValue(IChromosomeType chromosomeType, Class<? extends V> valueClass) {
		IAllele allele = getActiveAllele(chromosomeType);
		V value = AlleleUtils.getAlleleValue(allele, valueClass);
		if (value == null) {
			throw new IllegalArgumentException(String.format("The allele '%s' at the active position of the chromosome type '%s' has no value.", allele, chromosomeType));
		}
		return value;
	}

	@Override
	public <V> V getActiveValue(IChromosomeType chromosomeType, Class<? extends V> valueClass, V fallback) {
		IAllele allele = getActiveAllele(chromosomeType);
		return AlleleUtils.getAlleleValue(allele, valueClass, fallback);
	}

	@Override
	public <V> V getInactiveValue(IChromosomeType chromosomeType, Class<? extends V> valueClass) {
		IAllele allele = getInactiveAllele(chromosomeType);
		V value = AlleleUtils.getAlleleValue(allele, valueClass);
		if (value == null) {
			throw new IllegalArgumentException(String.format("The allele '%s' at the inactive position of the chromosome type '%s' has no value.", allele, chromosomeType));
		}
		return value;
	}

	@Override
	public <V> V getInactiveValue(IChromosomeType chromosomeType, Class<? extends V> valueClass, V fallback) {
		IAllele allele = getInactiveAllele(chromosomeType);
		return AlleleUtils.getAlleleValue(allele, valueClass, fallback);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> IAlleleValue<V> getInactiveAllele(IChromosomeValue<V> chromosomeType) {
		return (IAlleleValue<V>) getInactiveAllele(chromosomeType, IAlleleValue.class);
	}

	@Override
	public <A extends IAllele> A getInactiveAllele(IChromosomeAllele<A> chromosomeType) {
		Class<? extends A> alleleClass = chromosomeType.getAlleleClass();
		IAllele allele = getInactiveAllele((IChromosomeType) chromosomeType);
		if (!alleleClass.isInstance(allele)) {
			throw new IllegalArgumentException(String.format("The allele '%s' at the active position of the chromosome type '%s' is not an instance of the class '%s'.", allele, chromosomeType, alleleClass));
		}
		return alleleClass.cast(allele);
	}

	@Override
	public <A extends IAllele> A getInactiveAllele(IChromosomeAllele<A> chromosomeType, A fallback) {
		Class<? extends A> alleleClass = chromosomeType.getAlleleClass();
		IAllele allele = getInactiveAllele(chromosomeType);
		if (!alleleClass.isInstance(allele)) {
			return fallback;
		}
		return alleleClass.cast(allele);
	}

	@Override
	public <V> V getInactiveValue(IChromosomeValue<V> chromosomeType) {
		IAllele allele = getInactiveAllele(chromosomeType);
		V value = AlleleUtils.getAlleleValue(allele, chromosomeType.getValueClass());
		if (value == null) {
			throw new IllegalArgumentException(String.format("The allele '%s' at the active position of the chromosome type '%s' has no value.", allele, chromosomeType));
		}
		return value;
	}

	@Override
	public <V> V getInactiveValue(IChromosomeValue<V> chromosomeType, V fallback) {
		IAllele allele = getInactiveAllele(chromosomeType);
		return AlleleUtils.getAlleleValue(allele, chromosomeType.getValueClass(), fallback);
	}

	@Override
	public IChromosome getChromosome(IChromosomeType chromosomeType) {
		return chromosomes[chromosomeType.getIndex()];
	}

	@Override
	public IAllele[][] getAlleles() {
		IAllele[][] alleles = new IAllele[chromosomes.length][2];
		for (IChromosome chromosome : chromosomes) {
			IAllele[] chromosomeAlleles = alleles[chromosome.getType().getIndex()];
			chromosomeAlleles[0] = chromosome.getActiveAllele();
			chromosomeAlleles[1] = chromosome.getInactiveAllele();
		}
		return alleles;
	}

	@Override
	public IAllele[] getActiveAlleles() {
		IAllele[] alleles = new IAllele[chromosomes.length];
		for (IChromosome chromosome : chromosomes) {
			alleles[chromosome.getType().getIndex()] = chromosome.getActiveAllele();
		}
		return alleles;
	}

	@Override
	public IAllele[] getInactiveAlleles() {
		IAllele[] alleles = new IAllele[chromosomes.length];
		for (IChromosome chromosome : chromosomes) {
			alleles[chromosome.getType().getIndex()] = chromosome.getInactiveAllele();
		}
		return alleles;
	}

	@Override
	public IKaryotype getKaryotype() {
		return karyotype;
	}

	@Override
	public <W extends IGenomeWrapper> W asWrapper(Class<? extends W> wrapperClass) {
		IRootDefinition definition = GeneticsAPI.apiInstance.getRoot(karyotype.getUID());
		if (!definition.isRootPresent()) {
			throw new IllegalStateException();
		}
		IIndividualRoot root = definition.get();
		IGenomeWrapper wrapper = root.createWrapper(this);
		if (!wrapperClass.isInstance(wrapper)) {
			throw new IllegalStateException();
		}
		return wrapperClass.cast(wrapper);
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT compound) {
		return GeneticSaveHandler.INSTANCE.writeTag(chromosomes, karyotype, compound);
	}

	@Override
	public boolean isPureBred(IChromosomeType chromosomeType) {
		IChromosome chromosome = getChromosome(chromosomeType);
		return chromosome.isPureBred();
	}

	@Override
	public boolean isPureBred() {
		for (IChromosomeType chromosomeType : karyotype) {
			if (!isPureBred(chromosomeType)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isGeneticEqual(IGenome other) {
		IChromosome[] otherChromosomes = other.getChromosomes();
		if (chromosomes.length != otherChromosomes.length) {
			return false;
		}

		for (int i = 0; i < chromosomes.length; i++) {
			IChromosome chromosome = chromosomes[i];
			IChromosome otherChromosome = otherChromosomes[i];
			if (chromosome == null && otherChromosome == null) {
				continue;
			}
			if (chromosome == null || otherChromosome == null) {
				return false;
			}

			if (!chromosome.isGeneticEqual(otherChromosome)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(this);
		int i = 0;
		for (IChromosome chromosome : chromosomes) {
			toStringHelper.add(String.valueOf(i++), chromosome);
		}
		return toStringHelper.toString();
	}
}
