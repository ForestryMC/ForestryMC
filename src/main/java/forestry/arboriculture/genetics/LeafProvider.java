package forestry.arboriculture.genetics;

import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.IItemProvider;
import forestry.arboriculture.features.ArboricultureBlocks;
import genetics.api.alleles.IAllele;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class LeafProvider implements ILeafProvider {

    @Nullable
    private IAlleleTreeSpecies treeSpecies = null;

    @Override
    public void init(IAlleleTreeSpecies treeSpecies) {
        this.treeSpecies = treeSpecies;
    }

    @Override
    public ItemStack getDecorativeLeaves() {
        IAllele allele = treeSpecies;
        if (allele == null) {
            allele = TreeDefinition.Oak.getTemplate().get(TreeChromosomes.SPECIES);
        }

        String alleleName = allele.getRegistryName().getPath().replace("tree_", "");
        return ArboricultureBlocks.LEAVES_DECORATIVE.findFeature(alleleName)
                                                    .map(IItemProvider::stack)
                                                    .orElse(ItemStack.EMPTY);
    }

}