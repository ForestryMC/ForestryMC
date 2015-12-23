package forestry.apiculture.genetics;

import java.util.Arrays;
import java.util.Locale;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.core.genetics.IBranchDefinition;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;

public enum BeeBranchDefinition implements IBranchDefinition {
	HONEY("Apis"),
	NOBLE("Probapis"),
	INDUSTRIOUS("Industrapis"),
	HEROIC("Herapis"),
	INFERNAL("Diapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_2);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.NOCTURNAL, true);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.NETHER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.AVERAGE);
		}
	},
	AUSTERE("Modapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.NOCTURNAL, true);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.CACTI);
		}
	},
	TROPICAL("Caldapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.JUNGLE);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.EFFECT, AlleleEffect.effectMiasmic);
		}
	},
	END("Finapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.LOW);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGE);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.END);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.NOCTURNAL, true);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.EFFECT, AlleleEffect.effectMisanthrope);
		}
	},
	FROZEN("Coagapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.SNOW);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.EFFECT, AlleleEffect.effectGlacial);
		}
	},
	VENGEFUL("Punapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGEST);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.EFFECT, AlleleEffect.effectRadioactive);
		}
	},
	FESTIVE("Festapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_2);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
		}
	},
	AGRARIAN("Rustapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FASTER);
		}
	},
	BOGGY("Paludapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.MUSHROOMS);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
		}
	},
	MONASTIC("Monapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.LOW);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FASTER);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.CAVE_DWELLING, true);
			AlleleHelper.instance.set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
		}
	};

	private final IClassification branch;

	BeeBranchDefinition(String scientific) {
		branch = BeeManager.beeFactory.createBranch(this.name().toLowerCase(Locale.ENGLISH), scientific);
	}

	protected void setBranchProperties(IAllele[] template) {

	}

	@Override
	public final IAllele[] getTemplate() {
		IAllele[] template = getDefaultTemplate();
		setBranchProperties(template);
		return template;
	}

	@Override
	public final IClassification getBranch() {
		return branch;
	}

	private static IAllele[] defaultTemplate;

	private static IAllele[] getDefaultTemplate() {
		if (defaultTemplate == null) {
			defaultTemplate = new IAllele[EnumBeeChromosome.values().length];

			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.NORMAL);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.NOCTURNAL, false);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.TOLERANT_FLYER, false);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.CAVE_DWELLING, false);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWEST);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.AVERAGE);
			AlleleHelper.instance.set(defaultTemplate, EnumBeeChromosome.EFFECT, AlleleEffect.effectNone);
		}
		return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
	}
}
