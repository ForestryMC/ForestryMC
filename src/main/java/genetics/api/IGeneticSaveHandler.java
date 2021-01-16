package genetics.api;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;

/**
 * This handler provides some functions to save and load {@link IAllele}s, {@link IChromosome}s and {@link IGenome}s.
 * <p>
 * Get the instance from {@link IGeneticApiInstance#getSaveHandler()}.
 */
public interface IGeneticSaveHandler {

	/**
	 * Writes the given chromosomes to a NBT-Data.
	 *
	 * @param chromosomes The chromosomes that you want to write to the NBT-Data.
	 * @param karyotype   The karyotype of the chromosomes of the genome to that the chromosomes array belongs.
	 * @param tagCompound The NBT-Data to that the data of the chromosomes should be written.
	 */
	CompoundNBT writeTag(IChromosome[] chromosomes, IKaryotype karyotype, CompoundNBT tagCompound);

	/**
	 * Loads the chromosomes form the NBt-Data.
	 *
	 * @param karyotype   The karyotype of the chromosomes that the NBT-Data contains.
	 * @param tagCompound The NBT-Data that contain the information of the chromosomes
	 */
	IChromosome[] readTag(IKaryotype karyotype, CompoundNBT tagCompound);

	/**
	 * Quickly gets the species without loading the whole genome. And without creating absent chromosomes.
	 *
	 * @param genomeNBT      The NBT-Data that contains the information about the chromosome
	 * @param chromosomeType The gene type of the chromosome.
	 * @param active         if the returned allele should be the active one.
	 * @return The active or inactive allele of the chromosome if the chromosome is present.
	 */
	@Nullable
	IAllele getAlleleDirectly(CompoundNBT genomeNBT, IChromosomeType chromosomeType, boolean active);

	/**
	 * Quickly gets the allele without loading the whole genome. And without creating absent chromosomes.
	 *
	 * @param itemStack      The stack that contains the information about the chromosome
	 * @param type
	 * @param chromosomeType The gene type of the chromosome.   @return The active or inactive allele of the chromosome if the chromosome is present.
	 * @param active         if the returned allele should be the active one.
	 */
	@Nullable
	IAllele getAlleleDirectly(ItemStack itemStack, IOrganismType type, IChromosomeType chromosomeType, boolean active);

	/**
	 * Tries to load the chromosome of the given type and creates it if it is absent.
	 *
	 * @param itemStack      The stack that contains the information about the chromosome
	 * @param type
	 * @param chromosomeType The gene type of the chromosome.
	 * @param active         if the returned allele should be the active one.   @return The active or inactive allele of the chromosome.
	 */
	IAllele getAllele(ItemStack itemStack, IOrganismType type, IChromosomeType chromosomeType, boolean active);

	/**
	 * Tries to load a specific chromosome and creates it if it is absent.
	 *
	 * @param genomeNBT      The NBT-Data that contains the information about the chromosome
	 * @param chromosomeType The gene type of the chromosome.
	 * @return The chromosome.
	 */
	IChromosome getSpecificChromosome(CompoundNBT genomeNBT, IChromosomeType chromosomeType);

	/**
	 * Tries to load a specific chromosome and creates it if it is absent.
	 *
	 * @param itemStack      The stack that contains the information about the chromosome
	 * @param type
	 * @param chromosomeType The gene type of the chromosome.  @return The chromosome.
	 */
	IChromosome getSpecificChromosome(ItemStack itemStack, IOrganismType type, IChromosomeType chromosomeType);

	@Nullable
	CompoundNBT getIndividualDataDirectly(ItemStack itemStack, IOrganismType type, IIndividualRoot<IIndividual> root);

	CompoundNBT getIndividualData(ItemStack itemStack, IOrganismType type, IIndividualRoot<IIndividual> root);

	void setIndividualData(ItemStack itemStack, IOrganismType type, IIndividualRoot<IIndividual> root, CompoundNBT compound);
}
