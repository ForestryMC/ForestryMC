package genetics.alleles;

import java.util.Arrays;
import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IKaryotype;

import genetics.ApiInstance;

public final class AlleleTemplateBuilder implements IAlleleTemplateBuilder {
	private final IAllele[] alleles;
	private final IKaryotype karyotype;

	public AlleleTemplateBuilder(IKaryotype karyotype, IAllele[] alleles) {
		this.alleles = Arrays.copyOf(alleles, alleles.length);
		this.karyotype = karyotype;
	}

	@Override
	public IAlleleTemplateBuilder set(IChromosomeType chromosomeType, IAllele allele) {
		if (!karyotype.contains(chromosomeType)) {
			String message = String.format("Tried to change a allele template at the position of the chromosome type '%s'. " +
				"Incorrect chromosome type for the karyotype '%s' of this template.", chromosomeType, karyotype.getUID());
			throw new IllegalArgumentException(message);
		}
		IAlleleRegistry registry = ApiInstance.INSTANCE.getAlleleRegistry();
		if (!registry.isValidAllele(allele, chromosomeType)) {
			String message = String.format("Tried to change a allele template at the position of the chromosome type '%s'. " +
				"Incorrect type for allele '%s'.", chromosomeType, allele);
			throw new IllegalArgumentException(message);
		}
		alleles[chromosomeType.getIndex()] = allele;
		return this;
	}

	@Override
	public IAlleleTemplateBuilder set(IChromosomeType chromosomeType, ResourceLocation registryName) {
		if (!karyotype.contains(chromosomeType)) {
			String message = String.format("Tried to change a allele template at the position of the chromosome type '%s'. " +
				"Incorrect chromosome type for the karyotype '%s' of this template.", chromosomeType, karyotype.getUID());
			throw new IllegalArgumentException(message);
		}
		IAlleleRegistry alleleRegistry = ApiInstance.INSTANCE.getAlleleRegistry();
		Optional<IAllele> alleleOptional = alleleRegistry.getAllele(registryName);
		if (!alleleOptional.isPresent()) {
			String message = String.format("Tried to change a allele template at the position of the chromosome type '%s'. " +
				"No allele was registered for the given registry name '%s'.", chromosomeType, registryName);
			throw new IllegalArgumentException(message);
		}
		IAllele allele = alleleOptional.get();
		if (!alleleRegistry.isValidAllele(allele, chromosomeType)) {
			String message = String.format("Tried to change a allele template at the position of the chromosome type '%s'. " +
				"Incorrect type for allele '%s'.", chromosomeType, allele);
			throw new IllegalArgumentException(message);
		}
		alleles[chromosomeType.getIndex()] = allele;
		return this;
	}

	@Override
	public IKaryotype getKaryotype() {
		return karyotype;
	}

	@Override
	public int size() {
		return alleles.length;
	}

	@Override
	public IAlleleTemplate build() {
		return new AlleleTemplate(alleles, karyotype);
	}

	@Override
	public String toString() {
		return Arrays.toString(alleles);
	}
}
