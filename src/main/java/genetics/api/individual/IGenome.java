package genetics.api.individual;

import net.minecraft.nbt.CompoundNBT;

import genetics.api.IGeneticFactory;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleValue;

/**
 * Holds the {@link IChromosome}s which comprise the traits of a given individual.
 * <p>
 * You can create one with {@link IGeneticFactory#createGenome(IKaryotype, IChromosome[])} or
 * {@link IAlleleTemplate#toGenome()}.
 */
public interface IGenome {

	/**
	 * @return A array with all chromosomes of this genome.
	 */
	IChromosome[] getChromosomes();

	default IAlleleSpecies getPrimary() {
		return getPrimary(IAlleleSpecies.class);
	}

	default IAlleleSpecies getSecondary() {
		return getSecondary(IAlleleSpecies.class);
	}

	<A extends IAlleleSpecies> A getPrimary(Class<? extends A> alleleClass);

	<A extends IAlleleSpecies> A getSecondary(Class<? extends A> alleleClass);

	/**
	 * @return The active allele of the chromosome with the given type.
	 */
	IAllele getActiveAllele(IChromosomeType chromosomeType);

	<V> IAlleleValue<V> getActiveAllele(IChromosomeValue<V> chromosomeType);

	<A extends IAllele> A getActiveAllele(IChromosomeAllele<A> chromosomeType);

	<A extends IAllele> A getActiveAllele(IChromosomeAllele<A> chromosomeType, A fallback);

	<A extends IAllele> A getActiveAllele(IChromosomeType chromosomeType, Class<? extends A> alleleClass);

	<A extends IAllele> A getActiveAllele(IChromosomeType chromosomeType, Class<? extends A> alleleClass, A fallback);

	/**
	 * @return The value of the active allele of the chromosome with the given type.
	 */
	<V> V getActiveValue(IChromosomeType chromosomeType, Class<? extends V> valueClass);

	/**
	 * @return The value of the active allele of the chromosome with the given type.
	 */
	<V> V getActiveValue(IChromosomeType chromosomeType, Class<? extends V> valueClass, V fallback);

	<V> V getActiveValue(IChromosomeValue<V> chromosomeType);

	<V> V getActiveValue(IChromosomeValue<V> chromosomeType, V fallback);

	/**
	 * @return The inactive allele of the chromosome with the given type.
	 */
	IAllele getInactiveAllele(IChromosomeType chromosomeType);

	<V> IAlleleValue<V> getInactiveAllele(IChromosomeValue<V> chromosomeType);

	<A extends IAllele> A getInactiveAllele(IChromosomeAllele<A> chromosomeType);

	<A extends IAllele> A getInactiveAllele(IChromosomeAllele<A> chromosomeType, A fallback);

	<A extends IAllele> A getInactiveAllele(IChromosomeType chromosomeType, Class<? extends A> alleleClass);

	<A extends IAllele> A getInactiveAllele(IChromosomeType chromosomeType, Class<? extends A> alleleClass, A fallback);

	/**
	 * @return The value of the inactive allele of the chromosome with the given type.
	 * @throws IllegalArgumentException if the allele has no value or if the value is not an instance of the given class.
	 */
	<V> V getInactiveValue(IChromosomeType chromosomeType, Class<? extends V> valueClass);

	/**
	 * @return The value of the inactive allele of the chromosome with the given type.
	 */
	<V> V getInactiveValue(IChromosomeType chromosomeType, Class<? extends V> valueClass, V fallback);

	<V> V getInactiveValue(IChromosomeValue<V> chromosomeType);

	<V> V getInactiveValue(IChromosomeValue<V> chromosomeType, V fallback);

	/**
	 * @return The chromosome with the given type.
	 */
	IChromosome getChromosome(IChromosomeType chromosomeType);

	/**
	 * @return A 2-dimensional array that contains all alleles of this genome. The first dimension is the index of the
	 * chromosome type and the second dimension is 0 for the active allele and 1 for the inactive allele.
	 */
	IAllele[][] getAlleles();

	/**
	 * @return A array that contains all active alleles of this genome.
	 */
	IAllele[] getActiveAlleles();

	/**
	 * @return A array that contains all inactive alleles of this genome.
	 */
	IAllele[] getInactiveAlleles();

	/**
	 * @return true if the given other IGenome has the amount of chromosomes and their alleles are identical.
	 */
	boolean isGeneticEqual(IGenome other);

	/**
	 * @return true if this chromosome has the same active and inactive allele.
	 */
	boolean isPureBred(IChromosomeType chromosomeType);

	/**
	 * @return true if every chromosome of this genome has the same active and inactive allele.
	 */
	boolean isPureBred();

	/**
	 * @return The karyotype of this genome. It defines the positions of the chromosomes in the array and the length
	 * of it.
	 */
	IKaryotype getKaryotype();

	<W extends IGenomeWrapper> W asWrapper(Class<? extends W> wrapperClass);

	/**
	 * Writes the data of this genome to the NBT-Data.
	 * <p>
	 * You can read the NBT-Data with {@link IGeneticFactory#createGenome(IKaryotype, IChromosome[])}.
	 */
	CompoundNBT writeToNBT(CompoundNBT compound);
}
