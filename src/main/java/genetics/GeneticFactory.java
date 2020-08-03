package genetics;

import genetics.alleles.AlleleTemplate;
import genetics.alleles.AlleleTemplateBuilder;
import genetics.api.IGeneTemplate;
import genetics.api.IGeneticFactory;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.*;
import genetics.api.organism.IOrganism;
import genetics.api.organism.IOrganismHandler;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IDisplayHelper;
import genetics.api.root.IIndividualRoot;
import genetics.api.root.IRootDefinition;
import genetics.individual.Chromosome;
import genetics.individual.Genome;
import genetics.individual.IndividualBuilder;
import genetics.items.GeneTemplate;
import genetics.organism.Organism;
import genetics.organism.OrganismHandler;
import genetics.root.DisplayHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Arrays;
import java.util.function.Supplier;

public enum GeneticFactory implements IGeneticFactory {
    INSTANCE;

    @Override
    public IAlleleTemplateBuilder createTemplateBuilder(IKaryotype karyotype) {
        return new AlleleTemplateBuilder(karyotype, karyotype.getDefaultTemplate().alleles());
    }

    @Override
    public IAlleleTemplateBuilder createTemplateBuilder(IKaryotype karyotype, IAllele[] alleles) {
        return new AlleleTemplateBuilder(karyotype, alleles);
    }

    @Override
    public IAlleleTemplate createTemplate(IKaryotype karyotype, IAllele[] alleles) {
        return new AlleleTemplate(Arrays.copyOf(alleles, alleles.length), karyotype);
    }

    @Override
    public IGenome createGenome(IKaryotype karyotype, CompoundNBT compound) {
        return new Genome(karyotype, compound);
    }

    @Override
    public IGenome createGenome(IKaryotype karyotype, IChromosome[] chromosomes) {
        return new Genome(karyotype, chromosomes);
    }

    @Override
    public IChromosome createChromosome(IAllele allele, IChromosomeType type) {
        return Chromosome.create(allele, type);
    }

    @Override
    public IChromosome createChromosome(IAllele firstAllele, IAllele secondAllele, IChromosomeType type) {
        return Chromosome.create(firstAllele, secondAllele, type);
    }

    @Override
    public <I extends IIndividual> IOrganism<I> createOrganism(ItemStack itemStack, IOrganismType type, IRootDefinition<? extends IIndividualRoot<I>> definition) {
        return new Organism<>(itemStack, definition, () -> type);
    }

    @Override
    public <I extends IIndividual> IOrganismHandler<I> createOrganismHandler(IRootDefinition<? extends IIndividualRoot<I>> rootDefinition, Supplier<ItemStack> stack) {
        return new OrganismHandler<>(rootDefinition, stack);
    }

    @Override
    public <I extends IIndividual> IDisplayHelper createDisplayHelper(IIndividualRoot<I> root) {
        return new DisplayHelper<>(root);
    }

    @Override
    public IGeneTemplate createGeneTemplate() {
        return new GeneTemplate();
    }

    @Override
    public <I extends IIndividual> IIndividualBuilder<I> createIndividualBuilder(I individual) {
        return new IndividualBuilder<>(individual);
    }
}
