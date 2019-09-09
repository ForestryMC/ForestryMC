package genetics.api.individual;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.root.ITemplateContainer;

/**
 * The IKaryotype defines how many chromosomes a {@link IGenome} contains and which type the {@link IChromosome}s have.
 * <p>
 * You can use a {@link IKaryotypeBuilder} to create an instance or you create the instance directly with
 * {@link IKaryotypeFactory#createKaryotype(String, Class)} if you have a enum that contains your {@link IChromosomeType}s.
 */
public interface IKaryotype extends Iterable<IChromosomeType> {
	/**
	 * @return Short identifier that is only used if something went wrong.
	 */
	String getUID();

	/**
	 * @return All gene types of this IKaryotype.
	 */
	IChromosomeType[] getChromosomeTypes();

	/**
	 * Checks if this karyotype contains the given type.
	 */
	boolean contains(IChromosomeType type);

	/**
	 * @return The {@link IChromosomeType} that is used by the {@link ITemplateContainer} to identify the different templates.
	 * It uses the {@link IAllele#getRegistryName()} of the allele that is at the active position of the template in the
	 * chromosome with this type.
	 */
	IChromosomeType getSpeciesType();

	/**
	 * Creates a template builder that contains a copy of the default template allele array.
	 */
	IAlleleTemplateBuilder createTemplate();

	/**
	 * Creates a template builder that contains a copy of the allele array.
	 */
	IAlleleTemplateBuilder createTemplate(IAllele[] alleles);

	IAlleleTemplateBuilder createEmptyTemplate();

	/**
	 * @return Default individual template for use when stuff breaks.
	 */
	IAlleleTemplate getDefaultTemplate();

	/*
	 * @return The default template as a IGenome.
	 */
	IGenome getDefaultGenome();

	default IChromosome[] templateAsChromosomes(IAllele[] template) {
		return templateAsChromosomes(template, null);
	}

	IChromosome[] templateAsChromosomes(IAllele[] templateActive, @Nullable IAllele[] templateInactive);

	default IGenome templateAsGenome(IAllele[] template) {
		return templateAsGenome(template, null);
	}

	IGenome templateAsGenome(IAllele[] templateActive, @Nullable IAllele[] templateInactive);

	@Override
	default Iterator<IChromosomeType> iterator() {
		return Arrays.stream(getChromosomeTypes()).iterator();
	}
}
