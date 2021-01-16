package genetics.api;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IIndividualBuilder;
import genetics.api.individual.IKaryotype;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismHandler;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IDisplayHelper;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;

/**
 * A factory that can be used to create some default implementations.
 * <p>
 * Get the instance from {@link IGeneticApiInstance#getGeneticFactory()}.
 */
public interface IGeneticFactory {

	/**
	 * Creates a {@link IAlleleTemplateBuilder} that contains the default template alleles.
	 *
	 * @param karyotype The karyotype that defines the size of the allele array and contains the default template.
	 */
	IAlleleTemplateBuilder createTemplateBuilder(IKaryotype karyotype);

	/**
	 * Creates a {@link IAlleleTemplateBuilder} that contains the given allele array.
	 *
	 * @param karyotype The karyotype that defines the size of the allele array.
	 * @param alleles   A array that contains all alleles for this template. It must have the same length like the
	 *                  karyotype of the individual.
	 */
	IAlleleTemplateBuilder createTemplateBuilder(IKaryotype karyotype, IAllele[] alleles);

	/**
	 * Creates a {@link IAlleleTemplate} that contains the given allele array.
	 *
	 * @param karyotype The karyotype that defines the size of the allele array.
	 * @param alleles   A array that contains all alleles for this template. It must have the same length like the
	 *                  karyotype of the individual.
	 */
	IAlleleTemplate createTemplate(IKaryotype karyotype, IAllele[] alleles);

	/**
	 * Creates a instance of the default implementation of a {@link IGenome} out of the NBT-Data.
	 *
	 * @param karyotype The karyotype of the individual that contains the genome.
	 * @param compound  The NBT-Data that contains the information about the genome. You can use
	 *                  {@link IGenome#writeToNBT(CompoundNBT)} or
	 *                  {@link IGeneticSaveHandler#writeTag(IChromosome[], IKaryotype, CompoundNBT)} to get the data.
	 */
	IGenome createGenome(IKaryotype karyotype, CompoundNBT compound);

	/**
	 * Creates a instance of the default implementation of a {@link IGenome}.
	 *
	 * @param karyotype   The karyotype of the individual that contains the genome.
	 * @param chromosomes The chromosomes that the genome should contain
	 */
	IGenome createGenome(IKaryotype karyotype, IChromosome[] chromosomes);

	/**
	 * Creates an instance of a {@link IChromosome} with the same active and inactive allele.
	 *
	 * @return A instance of {@link IChromosome}.
	 */
	IChromosome createChromosome(IAllele allele, IChromosomeType type);

	/**
	 * Creates an instance of a {@link IChromosome}.
	 * <p>
	 * The order of the alleles only matters if both alleles are recessive.
	 *
	 * @param firstAllele  The first allele.
	 * @param secondAllele The second allele.
	 * @return A instance of {@link IChromosome}.
	 */
	IChromosome createChromosome(IAllele firstAllele, IAllele secondAllele, IChromosomeType type);

	/**
	 * Creates a default implementation of a {@link IOrganism}
	 *
	 * @param itemStack The item that contains the genetic information.
	 * @param type      The species type of the individual.
	 * @param root      The definition that describes the individual.
	 * @return A instance of {@link IOrganism}.
	 */
	<I extends IIndividual> IOrganism<I> createOrganism(ItemStack itemStack, IOrganismType type, IRootDefinition<? extends IIndividualRoot<I>> root);

	/**
	 * Creates the default implementation of a {@link IOrganismHandler}.
	 *
	 * @param rootDefinition The definition of the root that the {@link IOrganismHandler} will be registered for.
	 * @param stack          A supplier that supplies the stack that will be used as the default stack for every stack that
	 *                       will be created with {@link IOrganismHandler#createStack(IIndividual)}.
	 * @return A instance of {@link IOrganismHandler}.
	 */
	<I extends IIndividual> IOrganismHandler<I> createOrganismHandler(IRootDefinition<? extends IIndividualRoot<I>> rootDefinition, Supplier<ItemStack> stack);

	<I extends IIndividual> IDisplayHelper createDisplayHelper(IIndividualRoot<I> root);

	/**
	 * Creates a default implementation of a {@link IGeneTemplate}
	 */
	IGeneTemplate createGeneTemplate();

	<I extends IIndividual> IIndividualBuilder<I> createIndividualBuilder(I individual);
}
