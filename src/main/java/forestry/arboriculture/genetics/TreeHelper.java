package forestry.arboriculture.genetics;

import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IKaryotype;

import forestry.api.arboriculture.genetics.ITreeRoot;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.TreePlugin;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.genetics.alleles.AlleleLeafEffects;
import forestry.core.genetics.alleles.EnumAllele;

public class TreeHelper {
	private TreeHelper() {
	}

	public static ITreeRoot getRoot() {
		return TreePlugin.ROOT.get();
	}

	public static IKaryotype getKaryotype() {
		return getRoot().getKaryotype();
	}

	public static IAlleleTemplateBuilder createTemplate() {
		return getKaryotype().createTemplate();
	}

	public static IAlleleTemplate createDefaultTemplate(IAlleleTemplateBuilder templateBuilder) {
		return templateBuilder.set(TreeChromosomes.FRUITS, AlleleFruits.fruitNone)
			.set(TreeChromosomes.SPECIES, TreeDefinition.Oak.getSpecies())
			.set(TreeChromosomes.HEIGHT, EnumAllele.Height.SMALL)
			.set(TreeChromosomes.FERTILITY, EnumAllele.Saplings.LOWER)
			.set(TreeChromosomes.YIELD, EnumAllele.Yield.LOWEST)
			.set(TreeChromosomes.SAPPINESS, EnumAllele.Sappiness.LOWEST)
			.set(TreeChromosomes.EFFECT, AlleleLeafEffects.leavesNone)
			.set(TreeChromosomes.MATURATION, EnumAllele.Maturation.AVERAGE)
			.set(TreeChromosomes.GIRTH, 1)
			.set(TreeChromosomes.FIREPROOF, EnumAllele.Fireproof.FALSE).build();
	}
}
