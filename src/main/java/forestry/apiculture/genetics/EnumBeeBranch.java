package forestry.apiculture.genetics;

import java.util.Arrays;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.IAllele;
import forestry.core.genetics.Allele;

public enum EnumBeeBranch {
	HONEY("Apis"),
	NOBLE("Probapis"),
	INDUSTRIOUS("Industrapis"),
	HEROIC("Herapis"),
	INFERNAL("Diapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown2;
			alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersNether;
			alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringAverage;
		}
	},
	AUSTERE("Modapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
			alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceDown1;
			alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersCacti;
		}
	},
	TROPICAL("Caldapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceUp1;
			alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceUp1;
			alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersJungle;
			alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectMiasmic;
		}
	},
	END("Finapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityLow;
			alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
			alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceUp1;
			alleles[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLarge;
			alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersEnd;
			alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectMisanthrope;
		}
	},
	FROZEN("Coagapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceUp1;
			alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
			alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersSnow;
			alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectGlacial;
		}
	},
	VENGEFUL("Punapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLargest;
			alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectRadioactive;
		}
	},
	FESTIVE("Festapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth2;
			alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
			alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		}
	},
	AGRARIAN("Rustapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShorter;
			alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersWheat;
			alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFaster;
		}
	},
	BOGGY("Paludapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersMushrooms;
			alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlower;
			alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		}
	},
	MONASTIC("Monapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
			alleles[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityLow;
			alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFaster;
			alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
			alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
			alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolTrue;
			alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersWheat;
		}
	}
	;

	private final BranchBees branch;

	EnumBeeBranch(String scientific) {
		branch = new BranchBees(this.name().toLowerCase(), scientific);
	}

	protected void setBranchProperties(IAllele[] template) {

	}

	public final IAllele[] getTemplate() {
		IAllele[] template = getDefaultTemplate();
		setBranchProperties(template);
		return template;
	}

	public final BranchBees getBranch() {
		return branch;
	}

	private static IAllele[] defaultTemplate;

	private static IAllele[] getDefaultTemplate() {
		if (defaultTemplate == null) {
			defaultTemplate = new IAllele[EnumBeeChromosome.values().length];

			defaultTemplate[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesForest;
			defaultTemplate[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
			defaultTemplate[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShorter;
			defaultTemplate[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityNormal;
			defaultTemplate[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceNone;
			defaultTemplate[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolFalse;
			defaultTemplate[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceNone;
			defaultTemplate[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolFalse;
			defaultTemplate[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolFalse;
			defaultTemplate[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersVanilla;
			defaultTemplate[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlowest;
			defaultTemplate[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryDefault;
			defaultTemplate[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectNone;
		}
		return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
	}
}
