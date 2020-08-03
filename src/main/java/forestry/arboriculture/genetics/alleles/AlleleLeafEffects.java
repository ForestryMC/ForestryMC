package forestry.arboriculture.genetics.alleles;

import forestry.api.arboriculture.genetics.TreeChromosomes;
import genetics.api.alleles.IAlleleRegistry;

public class AlleleLeafEffects {
    public static final AlleleLeafEffectNone leavesNone = new AlleleLeafEffectNone();

    public static void registerAlleles(IAlleleRegistry registry) {
        registry.registerAllele(leavesNone, TreeChromosomes.EFFECT);
    }
}
