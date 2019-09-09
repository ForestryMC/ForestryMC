package forestry.arboriculture.genetics.alleles;

import genetics.api.alleles.IAlleleRegistry;

import forestry.api.arboriculture.genetics.TreeChromosomes;

public class AlleleLeafEffects {
	public static final AlleleLeafEffectNone leavesNone = new AlleleLeafEffectNone();

	public static void registerAlleles(IAlleleRegistry registry) {
		registry.registerAllele(leavesNone, TreeChromosomes.EFFECT);
	}
}
