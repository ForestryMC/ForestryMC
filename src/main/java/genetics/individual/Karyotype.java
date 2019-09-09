package genetics.individual;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IKaryotype;

public class Karyotype implements IKaryotype {
	private final String uid;
	private final IChromosomeType[] chromosomeTypes;
	private final IChromosomeType speciesType;
	private final Function<IAlleleTemplateBuilder, IAlleleTemplate> defaultTemplateSupplier;
	private final BiFunction<IKaryotype, IAllele[], IAlleleTemplateBuilder> templateFactory;
	@Nullable
	private IAlleleTemplate defaultTemplate = null;
	@Nullable
	private IGenome defaultGenome = null;

	public Karyotype(String uid, List<IChromosomeType> chromosomeTypes, IChromosomeType speciesType, BiFunction<IKaryotype, IAllele[], IAlleleTemplateBuilder> templateFactory, Function<IAlleleTemplateBuilder, IAlleleTemplate> defaultTemplateSupplier) {
		this.uid = uid;
		this.speciesType = speciesType;
		this.chromosomeTypes = new IChromosomeType[chromosomeTypes.size()];
		this.templateFactory = templateFactory;
		for (IChromosomeType key : chromosomeTypes) {
			this.chromosomeTypes[key.getIndex()] = key;
		}
		this.defaultTemplateSupplier = defaultTemplateSupplier;
	}

	@Override
	public String getUID() {
		return uid;
	}

	@Override
	public IChromosomeType[] getChromosomeTypes() {
		return chromosomeTypes;
	}

	@Override
	public boolean contains(IChromosomeType type) {
		return Arrays.asList(chromosomeTypes).contains(type);
	}

	@Override
	public IChromosomeType getSpeciesType() {
		return speciesType;
	}

	@Override
	public IAlleleTemplate getDefaultTemplate() {
		if (defaultTemplate == null) {
			defaultTemplate = defaultTemplateSupplier.apply(createEmptyTemplate());
		}
		return defaultTemplate;
	}

	@Override
	public IGenome getDefaultGenome() {
		if (defaultGenome == null) {
			defaultGenome = getDefaultTemplate().toGenome();
		}
		return defaultGenome;
	}

	@Override
	public IAlleleTemplateBuilder createTemplate() {
		return getDefaultTemplate().createBuilder();
	}

	@Override
	public IAlleleTemplateBuilder createTemplate(IAllele[] alleles) {
		return templateFactory.apply(this, alleles);
	}

	@Override
	public IAlleleTemplateBuilder createEmptyTemplate() {
		return templateFactory.apply(this, new IAllele[chromosomeTypes.length]);
	}

	@Override
	public IChromosome[] templateAsChromosomes(IAllele[] templateActive, @Nullable IAllele[] templateInactive) {
		Chromosome[] chromosomes = new Chromosome[chromosomeTypes.length];
		for (int i = 0; i < chromosomeTypes.length; i++) {
			if (templateInactive == null) {
				chromosomes[i] = Chromosome.create(templateActive[i], chromosomeTypes[i]);
			} else {
				chromosomes[i] = Chromosome.create(templateActive[i], templateInactive[i], chromosomeTypes[i]);
			}
		}

		return chromosomes;
	}

	@Override
	public IGenome templateAsGenome(IAllele[] templateActive, @Nullable IAllele[] templateInactive) {
		return new Genome(this, templateAsChromosomes(templateActive, templateInactive));
	}
}
