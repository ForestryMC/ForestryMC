package forestry.lepidopterology.genetics;

import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IKaryotype;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IButterflyRoot;
import forestry.core.genetics.alleles.EnumAllele;
import forestry.lepidopterology.ButterflyPlugin;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;

public class ButterflyHelper {
	private ButterflyHelper() {
	}

	public static IButterflyRoot getRoot() {
		return ButterflyPlugin.ROOT.get();
	}

	public static IKaryotype getKaryotype() {
		return getRoot().getKaryotype();
	}

	public static IAlleleTemplateBuilder createTemplate() {
		return getKaryotype().createTemplate();
	}

	public static IAlleleTemplate createDefaultTemplate(IAlleleTemplateBuilder templateBuilder) {
		return templateBuilder.set(ButterflyChromosomes.SIZE, EnumAllele.Size.SMALL)
			.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWEST)
			.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTER)
			.set(ButterflyChromosomes.METABOLISM, 3)
			.set(ButterflyChromosomes.FERTILITY, 3)
			.set(ButterflyChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE)
			.set(ButterflyChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE)
			.set(ButterflyChromosomes.NOCTURNAL, false)
			.set(ButterflyChromosomes.TOLERANT_FLYER, false)
			.set(ButterflyChromosomes.FIRE_RESIST, false)
			.set(ButterflyChromosomes.FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA)
			.set(ButterflyChromosomes.EFFECT, ButterflyAlleles.butterflyNone)
			.set(ButterflyChromosomes.COCOON, ButterflyAlleles.cocoonDefault)
			.set(ButterflyChromosomes.SPECIES, ButterflyDefinition.Monarch.getSpecies())
			.build();
	}
}
