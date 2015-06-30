package forestry.apiculture.genetics;

import java.util.Arrays;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.core.genetics.alleles.Allele;
import forestry.core.genetics.alleles.EnumAllele;

public enum BeeBranchDefinition {
	HONEY("Apis"),
	NOBLE("Probapis"),
	INDUSTRIOUS("Industrapis"),
	HEROIC("Herapis"),
	INFERNAL("Diapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_2);
			Allele.helper.set(alleles, EnumBeeChromosome.NOCTURNAL, true);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.NETHER);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.AVERAGE);
		}
	},
	AUSTERE("Modapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			Allele.helper.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			Allele.helper.set(alleles, EnumBeeChromosome.NOCTURNAL, true);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.CACTI);
		}
	},
	TROPICAL("Caldapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			Allele.helper.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.UP_1);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.JUNGLE);
			Allele.helper.set(alleles, EnumBeeChromosome.EFFECT, Allele.effectMiasmic);
		}
	},
	END("Finapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.LOW);
			Allele.helper.set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			Allele.helper.set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGER);
			Allele.helper.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			Allele.helper.set(alleles, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGE);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.END);
			Allele.helper.set(alleles, EnumBeeChromosome.NOCTURNAL, true);
			Allele.helper.set(alleles, EnumBeeChromosome.EFFECT, Allele.effectMisanthrope);
		}
	},
	FROZEN("Coagapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			Allele.helper.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.SNOW);
			Allele.helper.set(alleles, EnumBeeChromosome.EFFECT, Allele.effectGlacial);
		}
	},
	VENGEFUL("Punapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGEST);
			Allele.helper.set(alleles, EnumBeeChromosome.EFFECT, Allele.effectRadioactive);
		}
	},
	FESTIVE("Festapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			Allele.helper.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_2);
			Allele.helper.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			Allele.helper.set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
		}
	},
	AGRARIAN("Rustapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			Allele.helper.set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FASTER);
		}
	},
	BOGGY("Paludapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.MUSHROOMS);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWER);
			Allele.helper.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
		}
	},
	MONASTIC("Monapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			Allele.helper.set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			Allele.helper.set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
			Allele.helper.set(alleles, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.LOW);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FASTER);
			Allele.helper.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			Allele.helper.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			Allele.helper.set(alleles, EnumBeeChromosome.CAVE_DWELLING, true);
			Allele.helper.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
		}
	};

	private final IClassification branch;

	BeeBranchDefinition(String scientific) {
		branch = BeeManager.beeFactory.createBranch(this.name().toLowerCase(), scientific);
	}

	protected void setBranchProperties(IAllele[] template) {

	}

	public final IAllele[] getTemplate() {
		IAllele[] template = getDefaultTemplate();
		setBranchProperties(template);
		return template;
	}

	public final IClassification getBranch() {
		return branch;
	}

	private static IAllele[] defaultTemplate;

	private static IAllele[] getDefaultTemplate() {
		if (defaultTemplate == null) {
			defaultTemplate = new IAllele[EnumBeeChromosome.values().length];

			Allele.helper.set(defaultTemplate, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.NORMAL);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.NOCTURNAL, false);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.TOLERANT_FLYER, false);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.CAVE_DWELLING, false);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWEST);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.AVERAGE);
			Allele.helper.set(defaultTemplate, EnumBeeChromosome.EFFECT, Allele.effectNone);
		}
		return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
	}
}
