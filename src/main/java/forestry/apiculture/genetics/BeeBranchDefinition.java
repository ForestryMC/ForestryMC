package forestry.apiculture.genetics;

import java.util.Locale;

import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.classification.IBranchDefinition;
import genetics.api.classification.IClassification;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.apiculture.genetics.alleles.AlleleEffects;
import forestry.core.genetics.alleles.EnumAllele;

public enum BeeBranchDefinition implements IBranchDefinition {
	HONEY("Apis"),
	NOBLE("Probapis"),
	INDUSTRIOUS("Industrapis"),
	HEROIC("Herapis"),
	INFERNAL("Diapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_2)
				.set(BeeChromosomes.NEVER_SLEEPS, true)
				.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.NETHER)
				.set(BeeChromosomes.FLOWERING, EnumAllele.Flowering.AVERAGE);
		}
	},
	AUSTERE("Modapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1)
				.set(BeeChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1)
				.set(BeeChromosomes.NEVER_SLEEPS, true)
				.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.CACTI);
		}
	},
	TROPICAL("Caldapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1)
				.set(BeeChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.UP_1)
				.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.JUNGLE)
				.set(BeeChromosomes.EFFECT, AlleleEffects.effectMiasmic);
		}
	},
	END("Finapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.FERTILITY, EnumAllele.Fertility.LOW)
				.set(BeeChromosomes.SPEED, EnumAllele.Speed.SLOWER)
				.set(BeeChromosomes.LIFESPAN, EnumAllele.Lifespan.LONGER)
				.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1)
				.set(BeeChromosomes.TERRITORY, EnumAllele.Territory.LARGE)
				.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.END)
				.set(BeeChromosomes.NEVER_SLEEPS, true)
				.set(BeeChromosomes.EFFECT, AlleleEffects.effectMisanthrope);
		}
	},
	FROZEN("Coagapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1)
				.set(BeeChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1)
				.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.SNOW)
				.set(BeeChromosomes.EFFECT, AlleleEffects.effectGlacial);
		}
	},
	VENGEFUL("Punapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.TERRITORY, EnumAllele.Territory.LARGEST)
				.set(BeeChromosomes.EFFECT, AlleleEffects.effectRadioactive);
		}
	},
	FESTIVE("Festapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.SPEED, EnumAllele.Speed.SLOWER)
				.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_2)
				.set(BeeChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1)
				.set(BeeChromosomes.LIFESPAN, EnumAllele.Lifespan.NORMAL);
		}
	},
	AGRARIAN("Rustapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.SPEED, EnumAllele.Speed.SLOWER)
				.set(BeeChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTER)
				.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT)
				.set(BeeChromosomes.FLOWERING, EnumAllele.Flowering.FASTER);
		}
	},
	BOGGY("Paludapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.MUSHROOMS)
				.set(BeeChromosomes.FLOWERING, EnumAllele.Flowering.SLOWER)
				.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
		}
	},
	MONASTIC("Monapis") {
		@Override
		protected void setBranchProperties(IAlleleTemplateBuilder template) {
			template.set(BeeChromosomes.SPEED, EnumAllele.Speed.SLOWER)
				.set(BeeChromosomes.LIFESPAN, EnumAllele.Lifespan.LONG)
				.set(BeeChromosomes.FERTILITY, EnumAllele.Fertility.LOW)
				.set(BeeChromosomes.FLOWERING, EnumAllele.Flowering.FASTER)
				.set(BeeChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1)
				.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1)
				.set(BeeChromosomes.CAVE_DWELLING, true)
				.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
		}
	};

	private final IClassification branch;

	BeeBranchDefinition(String scientific) {
		branch = BeeManager.beeFactory.createBranch(this.name().toLowerCase(Locale.ENGLISH), scientific);
	}

	protected void setBranchProperties(IAlleleTemplateBuilder template) {
	}


	@Override
	public final IAlleleTemplate getTemplate() {
		return getTemplateBuilder().build();
	}

	@Override
	public final IAlleleTemplateBuilder getTemplateBuilder() {
		IAlleleTemplateBuilder template = BeeHelper.createTemplate();
		setBranchProperties(template);
		return template;
	}

	@Override
	public final IClassification getBranch() {
		return branch;
	}
}
