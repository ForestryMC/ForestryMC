package genetics.individual;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IKaryotype;
import genetics.api.root.ITemplateContainer;

import genetics.ApiInstance;

@Immutable
public class Chromosome implements IChromosome {
	private static final String ACTIVE_ALLELE_TAG = "UID0";
	private static final String INACTIVE_ALLELE_TAG = "UID1";
	private final IAllele active;
	private final IAllele inactive;
	private final IChromosomeType type;

	private Chromosome(IAllele allele, IChromosomeType type) {
		this.active = inactive = allele;
		this.type = type;
	}

	private Chromosome(IAllele active, IAllele inactive, IChromosomeType type) {
		this.active = active;
		this.inactive = inactive;
		this.type = type;
	}

	public static Chromosome create(@Nullable ResourceLocation primarySpeciesUid, @Nullable ResourceLocation secondarySpeciesUid, IChromosomeType type, CompoundNBT nbt) {
		IAlleleRegistry alleleRegistry = GeneticsAPI.apiInstance.getAlleleRegistry();
		IAllele firstAllele = alleleRegistry.getAllele(nbt.getString(ACTIVE_ALLELE_TAG)).orElse(null);
		IAllele secondAllele = alleleRegistry.getAllele(nbt.getString(INACTIVE_ALLELE_TAG)).orElse(null);
		return create(primarySpeciesUid, secondarySpeciesUid, type, firstAllele, secondAllele);
	}

	public static Chromosome create(@Nullable ResourceLocation primaryTemplateIdentifier, @Nullable ResourceLocation secondaryTemplateIdentifier, IChromosomeType type, @Nullable IAllele firstAllele, @Nullable IAllele secondAllele) {
		return create(getStringOrNull(primaryTemplateIdentifier), getStringOrNull(secondaryTemplateIdentifier), type, firstAllele, secondAllele);
	}

	public static Chromosome create(@Nullable String primaryTemplateIdentifier, @Nullable String secondaryTemplateIdentifier, IChromosomeType type, @Nullable IAllele firstAllele, @Nullable IAllele secondAllele) {
		firstAllele = validateAllele(primaryTemplateIdentifier, type, firstAllele);
		secondAllele = validateAllele(secondaryTemplateIdentifier, type, secondAllele);

		return new Chromosome(firstAllele, secondAllele, type);
	}

	@Nullable
	private static String getStringOrNull(@Nullable ResourceLocation location) {
		return location != null ? location.toString() : null;
	}

	private static IAllele validateAllele(@Nullable String templateIdentifier, IChromosomeType type, @Nullable IAllele allele) {
		IAlleleRegistry alleleRegistry = ApiInstance.INSTANCE.getAlleleRegistry();
		if (allele == null || !alleleRegistry.isValidAllele(allele, type)) {
			ITemplateContainer container = type.getRoot().getTemplates();
			IKaryotype karyotype = container.getKaryotype();
			IAllele[] template = null;

			if (templateIdentifier != null) {
				template = container.getTemplate(templateIdentifier);
			}

			if (template == null) {
				template = karyotype.getDefaultTemplate().alleles();
			}

			return template[type.getIndex()];
		}
		return allele;
	}

	public static Chromosome create(IAllele allele, IChromosomeType geneType) {
		return new Chromosome(allele, geneType);
	}

	static Optional<IAllele> getActiveAllele(CompoundNBT chromosomeNBT) {
		String alleleUid = chromosomeNBT.getString(Chromosome.ACTIVE_ALLELE_TAG);
		return GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(alleleUid);
	}

	static Optional<IAllele> getInactiveAllele(CompoundNBT chromosomeNBT) {
		String alleleUid = chromosomeNBT.getString(Chromosome.INACTIVE_ALLELE_TAG);
		return GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(alleleUid);
	}

	@Override
	public IChromosomeType getType() {
		return type;
	}

	@Override
	public IAllele getActiveAllele() {
		return active;
	}

	@Override
	public IAllele getInactiveAllele() {
		return inactive;
	}

	@Override
	public boolean isPureBred() {
		return active.equals(inactive);
	}

	@Override
	public boolean isGeneticEqual(IChromosome other) {
		if (!active.equals(other.getActiveAllele())) {
			return false;
		}
		return inactive.equals(other.getInactiveAllele());
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbttagcompound) {
		nbttagcompound.putString(ACTIVE_ALLELE_TAG, active.getRegistryName().toString());
		nbttagcompound.putString(INACTIVE_ALLELE_TAG, inactive.getRegistryName().toString());
		return nbttagcompound;
	}

	@Override
	public IChromosome inheritChromosome(Random rand, IChromosome other) {
		IAllele firstChoice;
		if (rand.nextBoolean()) {
			firstChoice = getActiveAllele();
		} else {
			firstChoice = getInactiveAllele();
		}

		IAllele secondChoice;
		if (rand.nextBoolean()) {
			secondChoice = other.getActiveAllele();
		} else {
			secondChoice = other.getInactiveAllele();
		}

		if (rand.nextBoolean()) {
			return Chromosome.create(firstChoice, secondChoice, type);
		} else {
			return Chromosome.create(secondChoice, firstChoice, type);
		}
	}

	public static Chromosome create(IAllele firstAllele, IAllele secondAllele, IChromosomeType geneType) {
		firstAllele = getActiveAllele(firstAllele, secondAllele);
		secondAllele = getInactiveAllele(firstAllele, secondAllele);
		return new Chromosome(firstAllele, secondAllele, geneType);
	}

	private static IAllele getActiveAllele(IAllele firstAllele, IAllele secondAllele) {
		if (firstAllele.isDominant()) {
			return firstAllele;
		}
		if (secondAllele.isDominant()) {
			return secondAllele;
		}
		// Leaves only the case of both being recessive
		return firstAllele;
	}

	private static IAllele getInactiveAllele(IAllele firstAllele, IAllele secondAllele) {
		if (!secondAllele.isDominant()) {
			return secondAllele;
		}
		if (!firstAllele.isDominant()) {
			return firstAllele;
		}
		// Leaves only the case of both being dominant
		return secondAllele;
	}

	@Override
	public String toString() {
		return "{" + active + ", " + inactive + "}";
	}
}
