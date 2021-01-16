package genetics.api.individual;

import java.util.List;
import java.util.Optional;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import genetics.api.alleles.IAllele;
import genetics.api.root.IIndividualRoot;

/**
 * An actual individual organism with genetic information.
 */
public interface IIndividual {
	/**
	 * @return The {@link IAllele#getRegistryName()} of the allele that is at the {@link IKaryotype#getSpeciesType()}
	 * of the {@link IGenome} of this individual.
	 */
	String getIdentifier();

	/**
	 * Adds some information about the individual to the list.
	 *
	 * @param tooltip
	 */
	void addTooltip(List<ITextComponent> tooltip);

	/**
	 * @return The definition that describes this organism.
	 */
	IIndividualRoot getRoot();

	/**
	 * @return The genetic data of this individual.
	 */
	IGenome getGenome();

	/**
	 * Mate with the given organism.
	 *
	 * @param mate the {@link IIndividual} to mate this one with.
	 */
	boolean mate(IGenome mate);

	/**
	 * @return Genetic information of the mate, empty if unmated.
	 */
	Optional<IGenome> getMate();

	/**
	 * @return A deep copy of this organism.
	 */
	IIndividual copy();

	/**
	 * Called on {@link IIndividualBuilder#build()} to copy states like {@link #isAnalyzed()} or the mate that have no relation to
	 * the {@link IGenome} itself.
	 *
	 * @param otherIndividual The individual that was used to create the builder.
	 */
	void onBuild(IIndividual otherIndividual);

	/**
	 * @return Creates a {@link IIndividualBuilder} of the genetic information of this individual.
	 */
	IIndividualBuilder toBuilder();

	/**
	 * @return true if this organism has the same active and inactive allele at the position.
	 */
	boolean isPureBred(IChromosomeType geneType);

	/**
	 * Check whether the genetic makeup of two IIndividuals is identical. Ignores additional data like generations, irregular mating, etc..
	 *
	 * @return true if the given other IIndividual has the amount of chromosomes and their alleles are identical.
	 */
	boolean isGeneticEqual(IIndividual other);

	/**
	 * Writes the data of this into NBT-Data.
	 */
	CompoundNBT write(CompoundNBT compound);

	/**
	 * Call to mark the IIndividual as analyzed.
	 *
	 * @return true if the IIndividual has not been analyzed previously.
	 */
	boolean analyze();

	/**
	 * @return true if the IIndividual has been analyzed previously.
	 */
	boolean isAnalyzed();
}
