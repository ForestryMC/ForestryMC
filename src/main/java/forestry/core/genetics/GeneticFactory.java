package forestry.core.genetics;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleTemplateBuilder;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IGeneticFactory;
import forestry.api.genetics.IIndividualHandler;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.genetics.ISpeciesType;
import forestry.core.genetics.alleles.AlleleTemplateBuilder;

public class GeneticFactory implements IGeneticFactory {

	@Override
	public IIndividualHandler createIndividualHandler(ItemStack itemStack, ISpeciesType speciesType, ISpeciesRoot speciesRoot) {
		return new IndividualHandler(itemStack, () -> speciesType, () -> speciesRoot);
	}

	@Override
	public IChromosome createChromosome(IAllele allele) {
		return Chromosome.create(allele);
	}

	@Override
	public IChromosome createChromosome(IAllele firstAllele, IAllele secondAllele) {
		return Chromosome.create(firstAllele, secondAllele);
	}

	@Override
	public IAlleleTemplateBuilder createTemplateBuilder(ISpeciesRoot root, IAllele[] alleles) {
		return new AlleleTemplateBuilder(root, alleles);
	}

	@Override
	public IAlleleTemplateBuilder createTemplateBuilder(ISpeciesRoot root) {
		return new AlleleTemplateBuilder(root);
	}
}
