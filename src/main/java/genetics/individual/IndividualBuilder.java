package genetics.individual;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.*;
import genetics.api.root.IIndividualRoot;
import net.minecraft.util.ResourceLocation;

public final class IndividualBuilder<I extends IIndividual> implements IIndividualBuilder<I> {
    private final IIndividualRoot<I> root;
    private final IAlleleTemplateBuilder activeBuilder;
    private final IAlleleTemplateBuilder inactiveBuilder;
    private final I creationIndividual;

    @SuppressWarnings("unchecked")
    public IndividualBuilder(I individual) {
        this.root = (IIndividualRoot<I>) individual.getRoot();
        IGenome genome = individual.getGenome();
        IKaryotype karyotype = root.getKaryotype();
        this.activeBuilder = karyotype.createTemplate(genome.getActiveAlleles());
        this.inactiveBuilder = karyotype.createTemplate(genome.getInactiveAlleles());
        this.creationIndividual = individual;
    }

    @Override
    public IIndividualBuilder<I> set(IChromosomeType type, IAllele allele, boolean active) {
        IAlleleTemplateBuilder builder = active ? activeBuilder : inactiveBuilder;
        builder.set(type, allele);
        return this;
    }

    @Override
    public IIndividualBuilder<I> set(IChromosomeType type, ResourceLocation registryName, boolean active) {
        IAlleleTemplateBuilder builder = active ? activeBuilder : inactiveBuilder;
        builder.set(type, registryName);
        return this;
    }

    @Override
    public IIndividualRoot<I> getRoot() {
        return root;
    }

    @Override
    public I getCreationIndividual() {
        return creationIndividual;
    }

    @Override
    public I build() {
        IAlleleTemplate activeTemplate = activeBuilder.build();
        IAlleleTemplate inactiveTemplate = inactiveBuilder.build();
        I individual = root.create(activeTemplate.toGenome(inactiveTemplate));
        individual.onBuild(creationIndividual);
        return individual;
    }
}
