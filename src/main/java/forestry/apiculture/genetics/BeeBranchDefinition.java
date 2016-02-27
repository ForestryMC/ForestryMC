package forestry.apiculture.genetics;

import com.google.common.collect.ImmutableMap;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import forestry.api.apiculture.BeeChromosome;
import forestry.api.apiculture.BeeManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.core.genetics.IBranchDefinition;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;

public enum BeeBranchDefinition implements IBranchDefinition<BeeChromosome> {
	HONEY("Apis"),
	NOBLE("Probapis"),
	INDUSTRIOUS("Industrapis"),
	HEROIC("Herapis"),
	INFERNAL("Diapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_2);
			AlleleHelper.instance.set(alleles, BeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.NETHER);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWERING, EnumAllele.Flowering.AVERAGE);
		}
	},
	AUSTERE("Modapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.CACTI);
		}
	},
	TROPICAL("Caldapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.JUNGLE);
			AlleleHelper.instance.set(alleles, BeeChromosome.EFFECT, AlleleEffect.effectMiasmic);
		}
	},
	END("Finapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.FERTILITY, EnumAllele.Fertility.LOW);
			AlleleHelper.instance.set(alleles, BeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, BeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGER);
			AlleleHelper.instance.set(alleles, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.TERRITORY, EnumAllele.Territory.LARGE);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.END);
			AlleleHelper.instance.set(alleles, BeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.instance.set(alleles, BeeChromosome.EFFECT, AlleleEffect.effectMisanthrope);
		}
	},
	FROZEN("Coagapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.SNOW);
			AlleleHelper.instance.set(alleles, BeeChromosome.EFFECT, AlleleEffect.effectGlacial);
		}
	},
	VENGEFUL("Punapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.TERRITORY, EnumAllele.Territory.LARGEST);
			AlleleHelper.instance.set(alleles, BeeChromosome.EFFECT, AlleleEffect.effectRadioactive);
		}
	},
	FESTIVE("Festapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_2);
			AlleleHelper.instance.set(alleles, BeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
		}
	},
	AGRARIAN("Rustapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, BeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWERING, EnumAllele.Flowering.FASTER);
		}
	},
	BOGGY("Paludapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.MUSHROOMS);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWER);
			AlleleHelper.instance.set(alleles, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
		}
	},
	MONASTIC("Monapis") {
		@Override
		protected void setBranchProperties(Map<BeeChromosome, IAllele> alleles) {
			AlleleHelper.instance.set(alleles, BeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, BeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
			AlleleHelper.instance.set(alleles, BeeChromosome.FERTILITY, EnumAllele.Fertility.LOW);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWERING, EnumAllele.Flowering.FASTER);
			AlleleHelper.instance.set(alleles, BeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
			AlleleHelper.instance.set(alleles, BeeChromosome.CAVE_DWELLING, true);
			AlleleHelper.instance.set(alleles, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
		}
	};

	private final IClassification branch;

	BeeBranchDefinition(String scientific) {
		branch = BeeManager.beeFactory.createBranch(this.name().toLowerCase(Locale.ENGLISH), scientific);
	}

	protected void setBranchProperties(Map<BeeChromosome, IAllele> template) {

	}

	@Override
	public final Map<BeeChromosome, IAllele> getTemplate() {
		Map<BeeChromosome, IAllele> template = getDefaultTemplate();
		setBranchProperties(template);
		return template;
	}

	@Override
	public final IClassification getBranch() {
		return branch;
	}

	private static ImmutableMap<BeeChromosome, IAllele> defaultTemplate;

	private static Map<BeeChromosome, IAllele> getDefaultTemplate() {
		if (defaultTemplate == null) {
			Map<BeeChromosome, IAllele> defaultTemplateBuilder = new EnumMap<>(BeeChromosome.class);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.FERTILITY, EnumAllele.Fertility.NORMAL);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.NEVER_SLEEPS, false);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.TOLERANT_FLYER, false);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.CAVE_DWELLING, false);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWEST);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.TERRITORY, EnumAllele.Territory.AVERAGE);
			AlleleHelper.instance.set(defaultTemplateBuilder, BeeChromosome.EFFECT, AlleleEffect.effectNone);

			defaultTemplate = ImmutableMap.copyOf(defaultTemplateBuilder);
		}

		return new EnumMap<>(defaultTemplate);
	}
}
