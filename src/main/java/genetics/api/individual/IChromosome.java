package genetics.api.individual;

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;

import genetics.api.IGeneticFactory;
import genetics.api.IGeneticSaveHandler;
import genetics.api.alleles.IAllele;


/**
 * Contains two alleles. One is active and the other is inactive.
 * <p>
 * The active allele is the active allele either because the allele is dominant or
 * because both alleles are recessive.
 * <p>
 * Implementations other than Genetic's default one are not supported!
 * <p>
 * You can uses {@link IGeneticFactory#createChromosome(IAllele, IAllele, IChromosomeType)} to create an instance of this.
 */
public interface IChromosome {
	/**
	 * @return The position of the chromosome at the {@link IGenome}.
	 */
	IChromosomeType getType();

	/**
	 * @return The active allele of this chromosome that is used in the most situations.
	 */
	IAllele getActiveAllele();

	/**
	 * @return The inactive allele of this chromosome.
	 */
	IAllele getInactiveAllele();

	/**
	 * Writes the data of this chromosome to the NBT-Data.
	 *
	 * @implNote If possible please use the {@link IGeneticSaveHandler} to write the whole genome instead.
	 */
	CompoundNBT writeToNBT(CompoundNBT compound);

	/**
	 * Creates a new chromosome out of the alleles of this chromosome and the other chromosome.
	 * <p>
	 * It always uses one allele from this and one from the other chromosome to create the new chromosome.
	 * It uses {@link Random#nextBoolean()} to decide which of the two alleles of one chromosome it should use.
	 *
	 * @param rand            The instance of random it should uses to figure out which of the two alleles if should
	 *                        use.
	 * @param otherChromosome The other chromosome that this chromosome uses to create the new one.
	 */
	IChromosome inheritChromosome(Random rand, IChromosome otherChromosome);

	/**
	 * @return true if this chromosome has the same active and inactive allele.
	 */
	boolean isPureBred();

	/**
	 * @return true if the given chromosome and this chromosome have identical alleles.
	 */
	boolean isGeneticEqual(IChromosome other);
}
