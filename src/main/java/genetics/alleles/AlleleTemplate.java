package genetics.alleles;

import javax.annotation.Nullable;
import java.util.Arrays;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import genetics.api.individual.IKaryotype;
import genetics.api.root.IIndividualRoot;

public final class AlleleTemplate implements IAlleleTemplate {
	private final IAllele[] alleles;
	private final IKaryotype karyotype;

	public AlleleTemplate(IAllele[] alleles, IKaryotype karyotype) {
		this.alleles = alleles;
		this.karyotype = karyotype;
	}

	@Override
	public IAllele get(IChromosomeType type) {
		return alleles[type.getIndex()];
	}

	@Override
	public IAllele[] alleles() {
		return Arrays.copyOf(alleles, alleles.length);
	}

	@Override
	public int size() {
		return alleles.length;
	}

	@Override
	public IAlleleTemplate copy() {
		return new AlleleTemplate(alleles(), karyotype);
	}

	@Override
	public IAlleleTemplateBuilder createBuilder() {
		return new AlleleTemplateBuilder(karyotype, alleles());
	}

	@Override
	public IKaryotype getKaryotype() {
		return karyotype;
	}

	@Override
	public <I extends IIndividual> I toIndividual(IIndividualRoot<I> root, @Nullable IAlleleTemplate inactiveTemplate) {
		return root.templateAsIndividual(alleles, inactiveTemplate == null ? null : inactiveTemplate.alleles());
	}

	@Override
	public IGenome toGenome(@Nullable IAlleleTemplate inactiveTemplate) {
		return karyotype.templateAsGenome(alleles, inactiveTemplate == null ? null : inactiveTemplate.alleles());
	}

	@Override
	public IChromosome[] toChromosomes(@Nullable IAlleleTemplate inactiveTemplate) {
		return karyotype.templateAsChromosomes(alleles, inactiveTemplate == null ? null : inactiveTemplate.alleles());
	}

	@Override
	public String toString() {
		return Arrays.toString(alleles);
	}
}
