package forestry.apiculture.genetics;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.apiculture.genetics.alleles.AlleleEffects;
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
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_2);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.NETHER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.AVERAGE);
		}
	},
	AUSTERE("Modapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.CACTI);
		}
	},
	TROPICAL("Caldapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.JUNGLE);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.EFFECT, AlleleEffects.effectMiasmic);
		}
	},
	END("Finapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.LOW);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.END);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.EFFECT, AlleleEffects.effectMisanthrope);
		}
	},
	FROZEN("Coagapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.SNOW);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.EFFECT, AlleleEffects.effectGlacial);
		}
	},
	VENGEFUL("Punapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGEST);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.EFFECT, AlleleEffects.effectRadioactive);
		}
	},
	FESTIVE("Festapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_2);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
		}
	},
	AGRARIAN("Rustapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FASTER);
		}
	},
	BOGGY("Paludapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.MUSHROOMS);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
		}
	},
	MONASTIC("Monapis") {
		@Override
		protected void setBranchProperties(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.LOW);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FASTER);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.CAVE_DWELLING, true);
			AlleleHelper.getInstance().set(alleles, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
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

	@Nullable
	private static IAllele[] defaultTemplate;

	private static IAllele[] getDefaultTemplate() {
		if (defaultTemplate == null) {
			defaultTemplate = new IAllele[EnumBeeChromosome.values().length];

			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.NORMAL);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.NEVER_SLEEPS, false);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.TOLERATES_RAIN, false);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.CAVE_DWELLING, false);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWEST);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.AVERAGE);
			AlleleHelper.getInstance().set(defaultTemplate, EnumBeeChromosome.EFFECT, AlleleEffects.effectNone);
		}
		return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
	}
}
