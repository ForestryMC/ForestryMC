package forestry.apiculture.genetics;

import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IKaryotype;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IBeeRoot;
import forestry.apiculture.BeePlugin;
import forestry.apiculture.genetics.alleles.AlleleEffects;
import forestry.core.genetics.alleles.EnumAllele;

public class BeeHelper {

	private BeeHelper() {
	}

	public static IBeeRoot getRoot() {
		return BeePlugin.ROOT.get();
	}

	public static IKaryotype getKaryotype() {
		return getRoot().getKaryotype();
	}

	public static IAlleleTemplateBuilder createTemplate() {
		return getKaryotype().createTemplate();
	}

	public static IAlleleTemplate createDefaultTemplate(IAlleleTemplateBuilder templateBuilder) {
		return templateBuilder.set(BeeChromosomes.SPEED, EnumAllele.Speed.SLOWEST)
			.set(BeeChromosomes.SPECIES, BeeDefinition.FOREST.getSpecies())
			.set(BeeChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTER)
			.set(BeeChromosomes.FERTILITY, EnumAllele.Fertility.NORMAL)
			.set(BeeChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE)
			.set(BeeChromosomes.NEVER_SLEEPS, false)
			.set(BeeChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE)
			.set(BeeChromosomes.TOLERATES_RAIN, false)
			.set(BeeChromosomes.CAVE_DWELLING, false)
			.set(BeeChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA)
			.set(BeeChromosomes.FLOWERING, EnumAllele.Flowering.SLOWEST)
			.set(BeeChromosomes.TERRITORY, EnumAllele.Territory.AVERAGE)
			.set(BeeChromosomes.EFFECT, AlleleEffects.effectNone).build();
	}
}
