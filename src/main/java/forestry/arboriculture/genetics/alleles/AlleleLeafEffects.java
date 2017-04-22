package forestry.arboriculture.genetics.alleles;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.core.genetics.alleles.Allele;

public class AlleleLeafEffects {
	public static final Allele leavesNone = new AlleleLeafEffectNone();

	public static void registerAlleles() {
		AlleleManager.alleleRegistry.registerAllele(leavesNone, EnumTreeChromosome.EFFECT);
	}
}
